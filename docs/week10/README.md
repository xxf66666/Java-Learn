# Week 10 · ERP 架构 + RBAC + 基础模块

> 进入 Phase 4：把前 9 周学的串起来，搭一个真实 ERP 后端。本周聚焦"通用基建"：项目结构 + 用户角色权限 + 字典 + 操作日志。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_erp_overview.md`](00_erp_overview.md) | ERP 是什么、模块划分、为什么选单体多模块 |
| 01 | [`01_project_layout.md`](01_project_layout.md) | 多模块 Maven 工程结构 + 包命名规范 |
| 02 | [`02_rbac.md`](02_rbac.md) | RBAC 5 表设计 + 数据权限思路 |
| 03 | [`03_dict_log.md`](03_dict_log.md) | 字典管理 + 操作日志（AOP 自动记录） |

## 配套代码

→ [`../../code/project/`](../../code/project/) —— ERP 项目本周开始建仓
→ SQL：[`../../scripts/erp.sql`](../../scripts/erp.sql)

## 本周里程碑

- 在 `code/project/` 建立完整多模块 Maven 工程
- 设计并落地 RBAC 5 张表
- 跑通：登录 → 拿到 JWT + 用户权限列表
- 写 `@SysLog` 注解 + AOP 切面，自动记录操作日志
- 字典管理（CRUD）+ 前端通过字典 type 拉枚举值
