-- GOGO购商城 演示数据
-- 用法：psql -h localhost -U postgres -d gogoshop -f db/data.sql
-- 演示账号（密码均为 BCrypt 哈希存储）：
--   管理员 admin / admin123
--   商家   shop1 / business123（已审核）  shop2 / business123（审核中）
--   用户   user1 / user123

INSERT INTO admin (id, username, password, name, avatar, role, phone, email) VALUES
(1, 'admin', '$2a$10$83BKTVMNzTi4A.ShmWoQY.tE6fALcrP6jDCKhmCJTLX6Ah5bkEJhi', '管理员',
 'http://localhost:9090/files/1697438073596-avatar.png', 'ADMIN', '13677889922', 'admin@gogoshop.local');

INSERT INTO business (id, username, password, name, avatar, role, phone, email, description, status) VALUES
(1, 'shop1', '$2a$10$urUgt4g/7pWTK1/neD3Agebza7CJ4Gq0pGK3TaFiAfzGSq6rvaL4O', 'GOGO自营旗舰店',
 'http://localhost:9090/files/1697438073596-avatar.png', 'BUSINESS', '13800000001', 'shop1@gogoshop.local', '平台自营，正品保障', '审核通过'),
(2, 'shop2', '$2a$10$urUgt4g/7pWTK1/neD3Agebza7CJ4Gq0pGK3TaFiAfzGSq6rvaL4O', '新开小店',
 'http://localhost:9090/files/1697438073596-avatar.png', 'BUSINESS', '13800000002', 'shop2@gogoshop.local', '等待审核的演示商家', '审核中');

INSERT INTO users (id, username, password, name, avatar, role, phone, email) VALUES
(1, 'user1', '$2a$10$F/yVl6t8onYTy0RwHuL9KujiHmsFxfEUTNSU5ygmdDvUGZvlU4h7G', '小明',
 'http://localhost:9090/files/1697438073596-avatar.png', 'USER', '13900000001', 'user1@gogoshop.local');

INSERT INTO type (id, name, description, img) VALUES
(1,  '女装内衣', '时尚女装与内衣',   'http://localhost:9090/files/1701760724028-女装内衣.png'),
(2,  '男装',     '潮流男装',         'http://localhost:9090/files/1701760712270-男装.png'),
(3,  '鞋/箱包',  '鞋靴与箱包',       'http://localhost:9090/files/1701760692792-鞋_箱包.png'),
(4,  '珠宝饰品', '珠宝首饰',         'http://localhost:9090/files/1701760676851-珠宝饰品.png'),
(5,  '电脑',     '电脑整机与配件',   'http://localhost:9090/files/1701760657879-电脑.png'),
(6,  '数码手机', '手机与数码产品',   'http://localhost:9090/files/1701760634406-数码手机.png'),
(7,  '母婴玩具', '母婴用品与玩具',   'http://localhost:9090/files/1701760612425-母婴玩具.png'),
(8,  '食品',     '休闲零食与粮油',   'http://localhost:9090/files/1701760600846-食品.png'),
(9,  '喵鲜生',   '生鲜水果',         'http://localhost:9090/files/1701760575650-喵鲜生.png'),
(10, '家用电器', '大小家电',         'http://localhost:9090/files/1701760558612-家用电器.png'),
(11, '家具建材', '家具与装修建材',   'http://localhost:9090/files/1701760473072-家具建材.png'),
(12, '汽车配件', '车载用品与配件',   'http://localhost:9090/files/1701760442964-汽车配件.png'),
(13, '家纺家饰', '床品与家居装饰',   'http://localhost:9090/files/1701760427560-家纺家饰.png'),
(14, '医药保健', '保健与常备药品',   'http://localhost:9090/files/1701760402856-医药保健.png'),
(15, '居家',     '居家日用',         'http://localhost:9090/files/1701760372701-居家.png'),
(16, '图书音像', '图书与音像制品',   'http://localhost:9090/files/1701760348280-图书音像.png');

INSERT INTO goods (id, name, description, img, price, unit, count, stock, type_id, business_id) VALUES
(1, '轻薄笔记本电脑 14英寸', '16G 内存，512G 固态硬盘，续航 12 小时', 'http://localhost:9090/files/1701760657879-电脑.png',     4999.00, '台', 12, 50,  5,  1),
(2, '5G 智能手机 256G',      '旗舰影像，120Hz 高刷屏',            'http://localhost:9090/files/1701760634406-数码手机.png', 2999.00, '部', 30, 100, 6,  1),
(3, '纯棉休闲衬衫',          '透气舒适，多色可选',                'http://localhost:9090/files/1701760712270-男装.png',     129.00,  '件', 58, 300, 2,  1),
(4, '每日坚果礼盒 750g',     '六种坚果科学配比',                  'http://localhost:9090/files/1701760600846-食品.png',     89.90,   '盒', 120, 500, 8, 1),
(5, '智能扫地机器人',        '激光导航，自动集尘',                'http://localhost:9090/files/1701760558612-家用电器.png', 1599.00, '台', 8, 30,   10, 1),
(6, '儿童益智积木 500 粒',   '环保材质，开发动手能力',            'http://localhost:9090/files/1701760612425-母婴玩具.png', 159.00,  '套', 40, 200, 7,  1),
(7, '新鲜脐橙 5kg',          '产地直发，现摘现发',                'http://localhost:9090/files/1701760575650-喵鲜生.png',   49.90,   '箱', 75, 400, 9,  1),
(8, '畅销小说精选套装',      '经典文学，全 4 册',                 'http://localhost:9090/files/1701760348280-图书音像.png', 99.00,   '套', 22, 150, 16, 1);

INSERT INTO notice (id, title, content, time, "user") VALUES
(1, '今天系统正式上线，开始内测', '今天系统正式上线，开始内测', '2023-09-05', 'admin'),
(2, '所有功能都已完成，可以正常使用', '所有功能都已完成，可以正常使用', '2023-09-05', 'admin'),
(3, '数据库已迁移至 PostgreSQL', '系统已完成安全加固并迁移到 PostgreSQL', CURRENT_DATE, 'admin');

INSERT INTO carousel (id, img, name, time, "user") VALUES
(1, 'http://localhost:9090/files/carousel-1.jpg', '欢迎来到 GOGO购', CURRENT_DATE, 'admin'),
(2, 'http://localhost:9090/files/carousel-2.jpg', '好物推荐',        CURRENT_DATE, 'admin');

-- 显式插入 id 后需要同步序列
SELECT setval('admin_id_seq',    (SELECT MAX(id) FROM admin));
SELECT setval('business_id_seq', (SELECT MAX(id) FROM business));
SELECT setval('users_id_seq',    (SELECT MAX(id) FROM users));
SELECT setval('type_id_seq',     (SELECT MAX(id) FROM type));
SELECT setval('goods_id_seq',    (SELECT MAX(id) FROM goods));
SELECT setval('notice_id_seq',   (SELECT MAX(id) FROM notice));
SELECT setval('carousel_id_seq', (SELECT MAX(id) FROM carousel));
