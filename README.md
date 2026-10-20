# GOGO购商城

一个前后端分离的多商家电商平台：用户在前台浏览、收藏、下单，商家在后台发布商品、处理订单，管理员负责审核商家和维护平台内容。

> 本科时的课程项目，现已完成数据库迁移（MySQL → PostgreSQL）、安全加固和业务完善，详见 [项目规划](docs/项目规划.md)。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 2.6、Vue Router、Element UI、axios |
| 后端 | Spring Boot 2.7、MyBatis、PageHelper、java-jwt、Spring Security Crypto（BCrypt）、Hutool、Lombok |
| 数据库 | PostgreSQL 16 |
| 构建 | Maven Wrapper、Vue CLI 5 |

## 功能

- **用户**：注册/登录、按分类浏览、搜索、商品详情、收藏、选择数量下单、取消订单、确认收货、个人信息
- **商家**：注册后等待管理员审核；审核通过后发布和管理自己的商品（价格、库存），给本店订单发货
- **管理员**：管理管理员/商家/用户账号、审核商家、维护商品分类、公告、轮播图，查看全部订单

## 快速开始

### 1. 环境要求

- JDK 17+
- Node.js 16+
- PostgreSQL 13+

### 2. 初始化数据库

```bash
createdb -h localhost -U postgres gogoshop
```

```bash
psql -h localhost -U postgres -d gogoshop -f db/schema.sql
```

```bash
psql -h localhost -U postgres -d gogoshop -f db/data.sql
```

### 3. 启动后端（端口 9090）

```bash
cd springboot && ./mvnw spring-boot:run
```

默认连接 `localhost:5432/gogoshop`，用户名 `postgres`，密码 `123456`。可以用环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 覆盖，其他配置见 [系统架构 · 配置项](docs/系统架构.md#6-配置项)。

### 4. 启动前端（端口 8080）

```bash
cd vue && npm install && npm run serve
```

浏览器访问 <http://localhost:8080>。

### 演示账号

| 角色 | 账号 | 密码 | 说明 |
|---|---|---|---|
| 管理员 | admin | admin123 | |
| 商家 | shop1 | business123 | 已审核，有 8 件商品 |
| 商家 | shop2 | business123 | 审核中，不能发布商品 |
| 用户 | user1 | user123 | |

## 测试

```bash
cd springboot && ./mvnw test
```

接口冒烟测试，覆盖下单流程和全部安全修复（需要先启动后端；会写入测试数据，测完可重新导入 `db/` 下的脚本）：

```bash
bash scripts/smoke-test.sh
```

## 目录结构

```
GOGOSHOP
├── db/                 PostgreSQL 建表脚本与演示数据
├── docs/               项目规划、系统架构、安全修复说明
├── files/              上传的图片
├── scripts/            冒烟测试脚本
├── springboot/         后端
└── vue/                前端
```

## 文档

- [项目规划](docs/项目规划.md)：已完成内容与后续路线图
- [系统架构](docs/系统架构.md)：架构图、认证流程、权限矩阵、数据模型、订单状态机
- [安全修复说明](docs/安全修复说明.md)：修复的问题、修复方式与验证方法
