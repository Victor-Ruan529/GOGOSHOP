#!/usr/bin/env bash
# 后端冒烟测试：验证核心业务流程和安全修复。
# 前提：后端已在 http://localhost:9090 启动，且数据库已导入 db/schema.sql + db/data.sql（会写入测试数据）
# 用法：bash scripts/smoke-test.sh
set -u
BASE=${BASE:-http://localhost:9090}
PASS=0; FAIL=0
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

json() { python3 -c "import sys,json; d=json.load(sys.stdin); print(eval('d' + sys.argv[1]))" "$1"; }
req() { # req METHOD PATH TOKEN [BODY]
  curl -s -X "$1" "$BASE$2" -H "Content-Type: application/json" ${3:+-H "token: $3"} ${4:+-d "$4"}
}
check() { # check 描述 期望code 实际响应
  local code; code=$(echo "$3" | json "['code']" 2>/dev/null)
  if [ "$code" = "$2" ]; then PASS=$((PASS+1)); echo "  ✔ $1"; else FAIL=$((FAIL+1)); echo "  ✘ $1 (期望 $2，实际: $3)"; fi
}
login() { req POST /login "" "{\"username\":\"$1\",\"password\":\"$2\",\"role\":\"$3\"}" | json "['data']['token']"; }

SUFFIX=$RANDOM
echo "== 认证"
ADMIN=$(login admin admin123 ADMIN)
SHOP=$(login shop1 business123 BUSINESS)
SHOP2=$(login shop2 business123 BUSINESS)
USER=$(login user1 user123 USER)
[ -n "$ADMIN" ] && [ -n "$SHOP" ] && [ -n "$USER" ] && PASS=$((PASS+1)) && echo "  ✔ 三种角色登录成功"
R=$(req POST /login "" '{"username":"admin","password":"admin123","role":"ADMIN"}')
echo "$R" | grep -q '"password"' && { FAIL=$((FAIL+1)); echo "  ✘ 登录响应泄露了密码"; } || { PASS=$((PASS+1)); echo "  ✔ 登录响应不包含密码"; }
check "错误密码被拒绝" 5003 "$(req POST /login "" '{"username":"admin","password":"wrong","role":"ADMIN"}')"
check "无 token 访问被拒绝" 401 "$(req GET /goods/selectAll "")"
check "伪造 token 被拒绝" 401 "$(req GET /goods/selectAll 'eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMS1BRE1JTiJdfQ.x')"
check "URL 参数传 token 不再有效" 401 "$(req GET "/goods/selectAll?token=$ADMIN" "")"

echo "== 注册越权"
R=$(req POST /register "" "{\"username\":\"evil$SUFFIX\",\"password\":\"123456\",\"role\":\"ADMIN\"}"); check "不能注册管理员" 403 "$R"
R=$(req POST /register "" "{\"username\":\"u$SUFFIX\",\"password\":\"123456\",\"role\":\"USER\"}"); check "可以注册用户" 200 "$R"
R=$(req POST /register "" "{\"username\":\"b$SUFFIX\",\"password\":\"123456\",\"role\":\"BUSINESS\",\"status\":\"审核通过\"}"); check "注册商家时无法自己设置审核通过" 200 "$R"
B_STATUS=$(req POST /login "" "{\"username\":\"b$SUFFIX\",\"password\":\"123456\",\"role\":\"BUSINESS\"}" | json "['data']['status']")
[ "$B_STATUS" = "审核中" ] && { PASS=$((PASS+1)); echo "  ✔ 新注册商家状态为审核中"; } || { FAIL=$((FAIL+1)); echo "  ✘ 新注册商家状态为 $B_STATUS"; }
R=$(req POST /register "" "{\"username\":\"w$SUFFIX\",\"password\":\"123\",\"role\":\"USER\"}"); check "弱密码被拒绝" 400 "$R"

echo "== 角色权限"
check "用户不能查看管理员列表" 403 "$(req GET /admin/selectPage "$USER")"
check "商家不能查看用户列表" 403 "$(req GET /user/selectPage "$SHOP")"
check "用户不能新增商品" 403 "$(req POST /goods/add "$USER" '{"name":"x","price":1}')"
check "用户不能删除分类" 403 "$(req DELETE /type/delete/1 "$USER")"
check "用户不能查看其他用户资料" 403 "$(req GET /user/selectById/999 "$USER")"
check "用户可以查看自己的资料" 200 "$(req GET /user/selectById/1 "$USER")"
check "未审核商家不能发布商品" 5012 "$(req POST /goods/add "$SHOP2" '{"name":"x","price":1,"stock":1}')"
check "管理员新增商品" 200 "$(req POST /goods/add "$ADMIN" '{"name":"管理员商品","price":1,"stock":1}')"
OTHER_GOODS=$(req GET /goods/selectAll "$ADMIN" | json "['data'][-1]['id']")
R=$(req PUT /goods/update "$SHOP" "{\"id\":$OTHER_GOODS,\"price\":0}"); check "商家修改不属于自己的商品被拒绝" 403 "$R"
check "商品价格不能为负数" 400 "$(req POST /goods/add "$SHOP" '{"name":"x","price":-1,"stock":1}')"
check "商家改自己资料时不能改审核状态" 200 "$(req PUT /business/update "$SHOP2" '{"id":2,"status":"审核通过"}')"
S2=$(req GET /business/selectById/2 "$ADMIN" | json "['data']['status']")
[ "$S2" = "审核中" ] && { PASS=$((PASS+1)); echo "  ✔ 审核状态未被商家篡改"; } || { FAIL=$((FAIL+1)); echo "  ✘ 审核状态被改成了 $S2"; }

echo "== 下单与库存"
STOCK0=$(req GET "/goods/selectById?id=4" "$USER" | json "['data']['stock']")
R=$(req POST /orders/add "$USER" '{"goodsId":4,"quantity":2,"goodsPrice":0.01,"userId":999,"status":"已完成"}')
check "下单成功" 200 "$R"
OID=$(echo "$R" | json "['data']['id']")
TOTAL=$(echo "$R" | json "['data']['totalPrice']")
ST=$(echo "$R" | json "['data']['status']")
[ "$TOTAL" = "179.8" ] || [ "$TOTAL" = "179.80" ] && { PASS=$((PASS+1)); echo "  ✔ 价格由服务端计算（忽略前端传的 0.01）"; } || { FAIL=$((FAIL+1)); echo "  ✘ 总价为 $TOTAL"; }
[ "$ST" = "待发货" ] && { PASS=$((PASS+1)); echo "  ✔ 状态由服务端设置（忽略前端传的 已完成）"; } || { FAIL=$((FAIL+1)); echo "  ✘ 状态为 $ST"; }
STOCK1=$(req GET "/goods/selectById?id=4" "$USER" | json "['data']['stock']")
[ $((STOCK0 - 2)) -eq "$STOCK1" ] && { PASS=$((PASS+1)); echo "  ✔ 库存已扣减 $STOCK0 -> $STOCK1"; } || { FAIL=$((FAIL+1)); echo "  ✘ 库存 $STOCK0 -> $STOCK1"; }
check "库存不足时下单失败" 5009 "$(req POST /orders/add "$USER" '{"goodsId":4,"quantity":99}' >/dev/null; req POST /orders/add "$USER" '{"goodsId":1,"quantity":99}')"
check "商家不能下单" 403 "$(req POST /orders/add "$SHOP" '{"goodsId":4,"quantity":1}')"

echo "== 订单状态机与越权"
NEWUSER=$(login "u$SUFFIX" 123456 USER)
check "其他用户不能查看该订单" 403 "$(req GET /orders/selectById/$OID "$NEWUSER")"
check "其他用户不能删除该订单" 403 "$(req DELETE /orders/delete/$OID "$NEWUSER")"
R=$(req PUT /orders/update "$USER" "{\"id\":$OID,\"status\":\"已发货\"}"); check "用户不能自己把订单改为已发货" 5011 "$R"
check "未完成订单不能删除" 5011 "$(req DELETE /orders/delete/$OID "$USER")"
R=$(req PUT /orders/update "$SHOP" "{\"id\":$OID,\"status\":\"已发货\"}"); check "商家发货" 200 "$R"
R=$(req PUT /orders/update "$SHOP" "{\"id\":$OID,\"status\":\"已发货\"}"); check "重复发货被拒绝" 5011 "$R"
R=$(req PUT /orders/update "$USER" "{\"id\":$OID,\"status\":\"已取消\"}"); check "已发货订单不能取消" 5011 "$R"
R=$(req PUT /orders/update "$USER" "{\"id\":$OID,\"status\":\"已完成\"}"); check "用户确认收货" 200 "$R"
check "已完成订单可以删除" 200 "$(req DELETE /orders/delete/$OID "$USER")"
R=$(req POST /orders/add "$USER" '{"goodsId":4,"quantity":3}'); OID2=$(echo "$R" | json "['data']['id']")
STOCK2=$(req GET "/goods/selectById?id=4" "$USER" | json "['data']['stock']")
R=$(req PUT /orders/update "$USER" "{\"id\":$OID2,\"status\":\"已取消\"}"); check "用户取消待发货订单" 200 "$R"
STOCK3=$(req GET "/goods/selectById?id=4" "$USER" | json "['data']['stock']")
[ $((STOCK2 + 3)) -eq "$STOCK3" ] && { PASS=$((PASS+1)); echo "  ✔ 取消后库存回退 $STOCK2 -> $STOCK3"; } || { FAIL=$((FAIL+1)); echo "  ✘ 库存 $STOCK2 -> $STOCK3"; }
ORDER_NO=$(req GET /orders/selectPage "$USER" | json "['data']['list'][0]['orderId']")
python3 -c "import sys; sys.exit(0 if isinstance('$ORDER_NO', str) and len('$ORDER_NO') >= 18 else 1)" && { PASS=$((PASS+1)); echo "  ✔ 订单号为字符串形式的雪花 ID ($ORDER_NO)"; } || { FAIL=$((FAIL+1)); echo "  ✘ 订单号 $ORDER_NO"; }

echo "== 收藏"
R=$(req POST /collect/add "$NEWUSER" '{"goodsId":2,"userId":1}')
check "收藏成功" 200 "$R"
CID=$(req GET /collect/selectAll "$NEWUSER" | json "['data'][0]['id']")
UID_OF=$(req GET /collect/selectAll "$NEWUSER" | json "['data'][0]['userId']")
[ "$UID_OF" != "1" ] && { PASS=$((PASS+1)); echo "  ✔ 收藏归属当前登录用户（忽略前端传的 userId）"; } || { FAIL=$((FAIL+1)); echo "  ✘ 收藏被记到了用户 1 名下"; }
check "重复收藏被拒绝" 5006 "$(req POST /collect/add "$NEWUSER" '{"goodsId":2}')"
check "其他用户不能删除该收藏" 403 "$(req DELETE /collect/delete/$CID "$USER")"

echo "== 修改密码"
check "只能修改自己的密码（请求体里的用户名被忽略）" 200 "$(req PUT /updatePassword "$NEWUSER" '{"username":"admin","password":"123456","newPassword":"654321"}')"
check "原 token 在改密后失效" 401 "$(req GET /collect/selectAll "$NEWUSER")"
check "管理员密码未被修改" 200 "$(req POST /login "" '{"username":"admin","password":"admin123","role":"ADMIN"}')"

echo "== 文件"
printf '\x89PNG\r\n\x1a\n0000' > "$TMP/a.png"
printf '<script>alert(1)</script>' > "$TMP/x.png"
printf '<html></html>' > "$TMP/x.html"
up() { curl -s -X POST "$BASE/files/upload" ${2:+-H "token: $2"} -F "file=@$1"; }
check "未登录不能上传" 401 "$(up "$TMP/a.png" "")"
R=$(up "$TMP/a.png" "$USER"); check "登录后可上传图片" 200 "$R"
URL=$(echo "$R" | json "['data']")
check "伪装成 png 的脚本被拒绝" 5013 "$(up "$TMP/x.png" "$USER")"
check "html 文件被拒绝" 5013 "$(up "$TMP/x.html" "$USER")"
CT=$(curl -s -o /dev/null -w '%{content_type}' "$URL")
[ "$CT" = "image/png" ] && { PASS=$((PASS+1)); echo "  ✔ 图片以 image/png 返回"; } || { FAIL=$((FAIL+1)); echo "  ✘ Content-Type: $CT"; }
SC=$(curl -s -o /dev/null -w '%{http_code}' "$BASE/files/..%2F..%2Fspringboot%2Fpom.xml")
[ "$SC" != "200" ] && { PASS=$((PASS+1)); echo "  ✔ 路径穿越被拒绝 ($SC)"; } || { FAIL=$((FAIL+1)); echo "  ✘ 路径穿越成功读取到了文件"; }
check "普通用户不能删除文件" 403 "$(req DELETE "/files/$(basename "$URL")" "$USER")"
check "管理员可以删除文件" 200 "$(req DELETE "/files/$(basename "$URL")" "$ADMIN")"

echo "== 登录限流"
for i in 1 2 3 4 5; do req POST /login "" "{\"username\":\"u$SUFFIX\",\"password\":\"bad\",\"role\":\"USER\"}" >/dev/null; done
R=$(req POST /login "" "{\"username\":\"u$SUFFIX\",\"password\":\"654321\",\"role\":\"USER\"}"); check "连续失败 5 次后锁定（正确密码也无法登录）" 5007 "$R"

echo
echo "通过 $PASS 项，失败 $FAIL 项"
[ "$FAIL" -eq 0 ]
