-- ERP 项目建表脚本 (Week 10 + Week 11)
CREATE DATABASE IF NOT EXISTS erp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE erp;

-- ===== 系统：sys_* =====
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_operation_log;
DROP TABLE IF EXISTS sys_dict_item;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_dept;

CREATE TABLE sys_dept (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id   BIGINT       DEFAULT 0,
    name        VARCHAR(64)  NOT NULL,
    sort        INT          DEFAULT 0,
    status      TINYINT      DEFAULT 1,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64)  NOT NULL,
    password    VARCHAR(128) NOT NULL,
    nickname    VARCHAR(64),
    email       VARCHAR(128),
    phone       VARCHAR(20),
    avatar      VARCHAR(255),
    dept_id     BIGINT,
    status      TINYINT      DEFAULT 1,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by  BIGINT,
    updated_by  BIGINT,
    deleted     TINYINT      DEFAULT 0,
    UNIQUE KEY uk_username (username, deleted)
);

CREATE TABLE sys_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(64)  NOT NULL,
    code        VARCHAR(64)  NOT NULL,
    sort        INT          DEFAULT 0,
    data_scope  TINYINT      DEFAULT 1,
    status      TINYINT      DEFAULT 1,
    remark      VARCHAR(255),
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
);

CREATE TABLE sys_menu (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id   BIGINT       DEFAULT 0,
    name        VARCHAR(64)  NOT NULL,
    type        TINYINT      NOT NULL,
    path        VARCHAR(128),
    component   VARCHAR(128),
    permission  VARCHAR(128),
    icon        VARCHAR(64),
    sort        INT          DEFAULT 0,
    status      TINYINT      DEFAULT 1
);

CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);

CREATE TABLE sys_dict_item (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    dict_code   VARCHAR(64)  NOT NULL,
    label       VARCHAR(64)  NOT NULL,
    value       VARCHAR(64)  NOT NULL,
    sort        INT          DEFAULT 0,
    status      TINYINT      DEFAULT 1,
    INDEX idx_code (dict_code)
);

CREATE TABLE sys_operation_log (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT,
    username    VARCHAR(64),
    module      VARCHAR(64),
    operation   VARCHAR(64),
    method      VARCHAR(255),
    params      TEXT,
    ip          VARCHAR(64),
    user_agent  VARCHAR(255),
    duration_ms BIGINT,
    success     TINYINT      DEFAULT 1,
    error_msg   VARCHAR(1000),
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_created (created_at)
);

-- ===== 初始化数据 =====
-- 部门
INSERT INTO sys_dept (id, parent_id, name) VALUES (1, 0, '总部');

-- admin 用户（密码: admin123）
-- BCrypt 加密 'admin123' 的一个有效散列（每次生成都不同，下面这个是 strength=10 的实际值）
INSERT INTO sys_user (id, username, password, nickname, dept_id, status) VALUES
(1, 'admin', '$2a$10$tj6BGZAuwlIGcl9LWMgewu/D6kE9XEsuhSm0KU3JKQHEzj9Ohop2K', '超级管理员', 1, 1),
(2, 'tester', '$2a$10$tj6BGZAuwlIGcl9LWMgewu/D6kE9XEsuhSm0KU3JKQHEzj9Ohop2K', '测试员', 1, 1);

-- 角色
INSERT INTO sys_role (id, name, code, data_scope, status) VALUES
(1, '超级管理员', 'admin', 1, 1),
(2, '普通用户', 'user',  3, 1);

-- 菜单
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission, icon, sort) VALUES
(1,  0, '系统管理',   1, '/system',         'Layout',          NULL,                'system', 1),
(11, 1, '用户管理',   2, '/system/user',    'system/user/index', 'sys:user:list',    'user',   1),
(12, 1, '角色管理',   2, '/system/role',    'system/role/index', 'sys:role:list',    'role',   2),
(13, 1, '菜单管理',   2, '/system/menu',    'system/menu/index', 'sys:menu:list',    'menu',   3),
(14, 1, '字典管理',   2, '/system/dict',    'system/dict/index', 'sys:dict:list',    'dict',   4),
(15, 1, '操作日志',   2, '/system/log',     'system/log/index',  'sys:log:list',     'log',    5),

(111, 11, '用户查询', 3, NULL, NULL, 'sys:user:query',  NULL, 1),
(112, 11, '用户新增', 3, NULL, NULL, 'sys:user:add',    NULL, 2),
(113, 11, '用户修改', 3, NULL, NULL, 'sys:user:edit',   NULL, 3),
(114, 11, '用户删除', 3, NULL, NULL, 'sys:user:remove', NULL, 4),

(2,  0, '业务管理',   1, '/business',       'Layout',          NULL,                'business', 2),
(21, 2, '物料档案',   2, '/business/material', 'business/material/index', 'mat:material:list', 'material', 1),
(22, 2, '库存管理',   2, '/business/stock',    'business/stock/index',    'wms:stock:list',    'stock',    2),
(23, 2, '采购订单',   2, '/business/purchase', 'business/purchase/index', 'pur:order:list',    'pur',      3),
(24, 2, '销售订单',   2, '/business/sale',     'business/sale/index',     'sal:order:list',    'sal',      4);

-- 用户-角色
INSERT INTO sys_user_role VALUES (1, 1), (2, 2);

-- 角色-菜单（admin 给全部；user 给查询权限）
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu;
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 1), (2, 11), (2, 111),
(2, 2), (2, 21), (2, 22);

-- 字典
INSERT INTO sys_dict_item (dict_code, label, value, sort) VALUES
('gender', '男', '0', 1),
('gender', '女', '1', 2),
('order_status', '草稿',    'DRAFT',    1),
('order_status', '待审核',  'PENDING',  2),
('order_status', '已审核',  'APPROVED', 3),
('order_status', '已完成',  'DONE',     4),
('order_status', '已作废',  'VOIDED',   5);
