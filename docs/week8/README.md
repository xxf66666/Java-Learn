# Week 8 · Spring Security + JWT + 工程规范

> 目标：给系统加上"登录鉴权"，建立完整的工程规范（统一响应 / 全局异常 / 参数校验 / 接口文档）。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_spring_security.md`](00_spring_security.md) | Spring Security 6 架构 + 登录流程 |
| 01 | [`01_jwt.md`](01_jwt.md) | JWT 原理 + 集成 Spring Security |
| 02 | [`02_norms.md`](02_norms.md) | 统一响应 + 全局异常 + 参数校验 + Knife4j 文档 |

## 配套代码

→ [`../../code/week8/`](../../code/week8/)

## 本周里程碑

- 知道 Spring Security 过滤器链是什么
- 实现一个完整的 "注册 + 登录 + 鉴权" 流程
- JWT Token 生成 + 验证
- 给所有非登录接口加上 JWT 校验
- 给参数错误、业务错误、未鉴权三种错误统一响应格式
- Knife4j 生成在线接口文档
