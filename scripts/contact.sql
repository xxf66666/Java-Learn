-- Week 4 通讯录建表脚本
CREATE DATABASE IF NOT EXISTS learning DEFAULT CHARACTER SET utf8mb4;
USE learning;

DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(64) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(128),
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO contact (name, phone, email) VALUES
('Alice', '13800138001', 'alice@example.com'),
('Bob',   '13800138002', 'bob@example.com'),
('Carol', '13800138003', 'carol@example.com');
