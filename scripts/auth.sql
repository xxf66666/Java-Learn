-- Week 8 auth-demo 建表
CREATE DATABASE IF NOT EXISTS auth_demo DEFAULT CHARACTER SET utf8mb4;
USE auth_demo;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(64)  NOT NULL,
    password   VARCHAR(128) NOT NULL,
    role       VARCHAR(32)  NOT NULL DEFAULT 'USER',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
