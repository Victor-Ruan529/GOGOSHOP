-- GOGO购商城 PostgreSQL 表结构
-- 用法：psql -h localhost -U postgres -d gogoshop -f db/schema.sql
-- 注意：会删除并重建所有业务表

DROP TABLE IF EXISTS collect, orders, goods, type, carousel, notice, users, business, admin CASCADE;

-- 管理员
CREATE TABLE admin (
    id       SERIAL PRIMARY KEY,
    username VARCHAR(32)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,                 -- BCrypt 哈希
    name     VARCHAR(64),
    avatar   VARCHAR(255),
    role     VARCHAR(16)  NOT NULL DEFAULT 'ADMIN',
    phone    VARCHAR(20),
    email    VARCHAR(64)
);
COMMENT ON TABLE admin IS '管理员';

-- 商家
CREATE TABLE business (
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(32)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    name        VARCHAR(64),                        -- 店铺名
    avatar      VARCHAR(255),
    role        VARCHAR(16)  NOT NULL DEFAULT 'BUSINESS',
    phone       VARCHAR(20),
    email       VARCHAR(64),
    description TEXT,
    status      VARCHAR(16)  NOT NULL DEFAULT '审核中'  -- 审核中 / 审核通过 / 审核不通过
);
COMMENT ON TABLE business IS '商家';

-- 普通用户（user 是 PostgreSQL 保留字，表名用 users）
CREATE TABLE users (
    id       SERIAL PRIMARY KEY,
    username VARCHAR(32)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name     VARCHAR(64),
    avatar   VARCHAR(255),
    role     VARCHAR(16)  NOT NULL DEFAULT 'USER',
    phone    VARCHAR(20),
    email    VARCHAR(64)
);
COMMENT ON TABLE users IS '普通用户';

-- 商品分类
CREATE TABLE type (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    img         VARCHAR(255)
);
COMMENT ON TABLE type IS '商品分类';

-- 商品
CREATE TABLE goods (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(128)   NOT NULL,
    description TEXT,
    img         VARCHAR(255),
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    unit        VARCHAR(16),
    count       INTEGER        NOT NULL DEFAULT 0 CHECK (count >= 0),   -- 销量
    stock       INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0),   -- 库存
    type_id     INTEGER REFERENCES type (id) ON DELETE SET NULL,
    business_id INTEGER REFERENCES business (id) ON DELETE CASCADE
);
CREATE INDEX idx_goods_type ON goods (type_id);
CREATE INDEX idx_goods_business ON goods (business_id);
COMMENT ON TABLE goods IS '商品';

-- 订单：商品信息在下单时做快照，商品改价/删除不影响历史订单
CREATE TABLE orders (
    id          SERIAL PRIMARY KEY,
    order_id    BIGINT         NOT NULL UNIQUE,     -- 雪花算法订单号
    user_id     INTEGER        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    business_id INTEGER REFERENCES business (id) ON DELETE SET NULL,
    goods_id    INTEGER REFERENCES goods (id) ON DELETE SET NULL,
    goods_name  VARCHAR(128)   NOT NULL,
    goods_img   VARCHAR(255),
    goods_unit  VARCHAR(16),
    goods_price NUMERIC(10, 2) NOT NULL,            -- 成交单价
    quantity    INTEGER        NOT NULL CHECK (quantity > 0),
    total_price NUMERIC(12, 2) NOT NULL,
    status      VARCHAR(16)    NOT NULL,            -- 待发货 / 已发货 / 已完成 / 已取消
    create_time TIMESTAMP      NOT NULL DEFAULT now(),
    update_time TIMESTAMP
);
CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_business ON orders (business_id);
COMMENT ON TABLE orders IS '订单';

-- 收藏
CREATE TABLE collect (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    business_id INTEGER REFERENCES business (id) ON DELETE CASCADE,
    goods_id    INTEGER NOT NULL REFERENCES goods (id) ON DELETE CASCADE,
    UNIQUE (user_id, goods_id)
);
COMMENT ON TABLE collect IS '收藏';

-- 公告
CREATE TABLE notice (
    id      SERIAL PRIMARY KEY,
    title   VARCHAR(255) NOT NULL,
    content TEXT,
    time    DATE         NOT NULL DEFAULT CURRENT_DATE,
    "user"  VARCHAR(64)
);
COMMENT ON TABLE notice IS '公告';

-- 轮播图
CREATE TABLE carousel (
    id     SERIAL PRIMARY KEY,
    img    VARCHAR(255) NOT NULL,
    name   VARCHAR(64),
    time   DATE         NOT NULL DEFAULT CURRENT_DATE,
    "user" VARCHAR(64)
);
COMMENT ON TABLE carousel IS '轮播图';
