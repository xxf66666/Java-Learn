-- Week 11 ERP 业务模块建表（接 erp.sql）
USE erp;

DROP TABLE IF EXISTS sal_order_item;
DROP TABLE IF EXISTS sal_order;
DROP TABLE IF EXISTS pur_order_item;
DROP TABLE IF EXISTS pur_order;
DROP TABLE IF EXISTS wms_stock_log;
DROP TABLE IF EXISTS wms_stock;
DROP TABLE IF EXISTS wms_warehouse;
DROP TABLE IF EXISTS mat_material;

CREATE TABLE mat_material (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    category    VARCHAR(64),
    unit        VARCHAR(16)   DEFAULT '件',
    spec        VARCHAR(255),
    price       DECIMAL(18,2) DEFAULT 0,
    status      TINYINT       DEFAULT 1,
    remark      VARCHAR(255),
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT       DEFAULT 0,
    UNIQUE KEY uk_code (code, deleted)
);

CREATE TABLE wms_warehouse (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(64)  NOT NULL,
    name        VARCHAR(64)  NOT NULL,
    address     VARCHAR(255),
    status      TINYINT      DEFAULT 1,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0,
    UNIQUE KEY uk_code (code, deleted)
);

CREATE TABLE wms_stock (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id     BIGINT NOT NULL,
    warehouse_id    BIGINT NOT NULL,
    quantity        DECIMAL(18,4) NOT NULL DEFAULT 0,
    version         INT NOT NULL DEFAULT 0,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_mat_wh (material_id, warehouse_id),
    INDEX idx_material (material_id)
);

CREATE TABLE wms_stock_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id     BIGINT NOT NULL,
    warehouse_id    BIGINT NOT NULL,
    direction       TINYINT NOT NULL,
    quantity        DECIMAL(18,4) NOT NULL,
    quantity_after  DECIMAL(18,4) NOT NULL,
    biz_type        VARCHAR(32),
    biz_no          VARCHAR(64),
    remark          VARCHAR(255),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,
    INDEX idx_mat (material_id),
    INDEX idx_biz (biz_type, biz_no),
    INDEX idx_created (created_at)
);

CREATE TABLE pur_order (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no      VARCHAR(32) NOT NULL,
    supplier_id   BIGINT,
    supplier_name VARCHAR(128),
    warehouse_id  BIGINT NOT NULL,
    total_amount  DECIMAL(18,2) NOT NULL DEFAULT 0,
    status        VARCHAR(16)   NOT NULL DEFAULT 'DRAFT',
    remark        VARCHAR(255),
    created_at    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    approved_at   DATETIME,
    approved_by   BIGINT,
    deleted       TINYINT       DEFAULT 0,
    UNIQUE KEY uk_no (order_no, deleted),
    INDEX idx_status (status)
);

CREATE TABLE pur_order_item (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity    DECIMAL(18,4) NOT NULL,
    price       DECIMAL(18,2) NOT NULL,
    amount      DECIMAL(18,2) NOT NULL,
    INDEX idx_order (order_id)
);

CREATE TABLE sal_order (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no      VARCHAR(32) NOT NULL,
    customer_id   BIGINT,
    customer_name VARCHAR(128),
    warehouse_id  BIGINT NOT NULL,
    total_amount  DECIMAL(18,2) NOT NULL DEFAULT 0,
    status        VARCHAR(16)   NOT NULL DEFAULT 'DRAFT',
    remark        VARCHAR(255),
    created_at    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    approved_at   DATETIME,
    approved_by   BIGINT,
    deleted       TINYINT       DEFAULT 0,
    UNIQUE KEY uk_no (order_no, deleted),
    INDEX idx_status (status)
);

CREATE TABLE sal_order_item (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity    DECIMAL(18,4) NOT NULL,
    price       DECIMAL(18,2) NOT NULL,
    amount      DECIMAL(18,2) NOT NULL,
    INDEX idx_order (order_id)
);

-- 种子数据
INSERT INTO wms_warehouse (code, name, address) VALUES ('W01', '主仓库', '上海市浦东');
INSERT INTO mat_material (code, name, category, unit, price) VALUES
('M0001', 'iPhone 15',    '电子产品', '台', 5999),
('M0002', 'iPad Air',     '电子产品', '台', 4999),
('M0003', 'MacBook Pro',  '电子产品', '台', 14999);
