# Week 10 §00 · ERP 概览

## 1. ERP 是什么

**Enterprise Resource Planning**，企业资源计划。一句话：**把公司的"人 / 物 / 钱 / 单"放在一个系统里管**。

典型模块（按业务分）：

```
基础模块（每个 ERP 都有）
├── 用户 / 角色 / 权限
├── 组织架构（部门 / 岗位）
├── 数据字典（性别、状态、币种...）
├── 系统参数
└── 操作日志 / 登录日志

业务模块（按行业不同）
├── 采购：供应商、采购订单、入库
├── 销售：客户、销售订单、出库
├── 库存：物料、仓库、库存流水、盘点
├── 财务：应收应付、凭证、报表
├── 生产（制造业）：BOM、工单、排程
└── HR：员工、考勤、工资
```

本仓库做的范围：**基础 + 物料 + 库存 + 采购 + 销售**（学习足够，财务/生产水太深暂不涉及）。

---

## 2. 为什么选单体多模块（而非微服务）

| | 单体多模块 | 微服务 |
|--|-----------|--------|
| 上手难度 | 低 | 高（服务发现 / 网关 / 链路追踪 / 分布式事务） |
| 部署 | 一个 jar | 一堆 jar + 多机 |
| 调试 | 一个进程，断点直达 | 跨进程，链路复杂 |
| 适合规模 | 中小团队 / 单业务线 | 大团队 / 多业务线 |

ERP 这种**强相关、强一致**业务，单体多模块**完全够用**。等你熟练了想拆微服务，分支演进就行。

---

## 3. 模块拆分原则

```
erp/
├── erp-common/        通用工具：Result / 错误码 / 工具类 / 常量 / 注解
├── erp-framework/     基础设施：Security / Redis / MyBatis-Plus 配置
├── erp-system/        通用模块：用户 / 角色 / 菜单 / 字典 / 日志 / 部门
├── erp-business/      业务模块：物料 / 库存 / 采购 / 销售
└── erp-admin/         主启动：装 Application.java + application.yml
```

**好处**
- `business` 依赖 `system`，但 `system` 不依赖 `business`（业务模块可换，基础不动）
- 编译某个模块时不用编全部
- 未来拆微服务，按模块边界切就行

---

## 4. 命名约定

### 表

- 系统表加前缀 `sys_`：`sys_user` / `sys_role` / `sys_dict`
- 业务表按模块前缀：`mat_` 物料 / `wms_` 仓储 / `pur_` 采购 / `sal_` 销售
- 时间字段：`created_at` / `updated_at`（不用 `create_time` 那种）
- 删除标记：`deleted` (0 / 1)
- 主键：`id BIGINT`

### Java 包

```
com.learning.erp
├── common.{result, exception, constant, annotation, util}
├── framework.{security, mybatis, redis, cors}
├── system.{user, role, menu, dept, dict, log, login}
│   └── 每个子包：entity / mapper / service / controller / dto
└── business.{material, warehouse, stock, purchase, sale}
```

### 错误码

```
1xxxx  用户相关  10001 用户不存在  10002 已存在  10003 密码错  ...
2xxxx  权限相关  20001 没有访问权限
3xxxx  物料相关  30001 物料不存在
4xxxx  库存相关  40001 库存不足
5xxxx  采购相关
6xxxx  销售相关
```

---

## 5. 选型

| | 选择 | 理由 |
|--|-----|------|
| 后端框架 | Spring Boot 3.3.x | 现役主流 |
| ORM | MyBatis-Plus 3.5.x | 国产、生态好、CRUD 自动 |
| 数据库 | MySQL 8 | 工作中最常见 |
| 缓存 | Redis 7 | 标准选择 |
| 安全 | Spring Security 6 + JWT | 前后端分离标配 |
| 文档 | Knife4j | 中文 UI 更友好 |
| Excel | EasyExcel | 阿里出，比 POI 内存友好 |
| 前端（不属于本仓库重点） | Vue 3 + Element Plus + Vite | 国内 ERP 主流 |
| 容器 | Docker / Docker Compose | 部署标准 |

---

## 6. 参考项目

强烈建议跟着读源码：

- [若依 ruoyi-vue-pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro) —— 最完整的国产 ERP 脚手架，本仓库表结构参考它
- [若依 ruoyi-vue](https://gitee.com/y_project/RuoYi-Vue) —— ruoyi 的基础版
- [pig](https://gitee.com/log4j/pig) —— 微服务版（暂不看）
- [jeecg-boot](https://github.com/jeecgboot/JeecgBoot) —— 低代码风格

读源码方法：先读 `sys_user` / `sys_role` 表的 Entity / Mapper / Service / Controller，跟着调用链走一遍。

---

## 7. 自查

- [ ] 能列出 ERP 至少 5 个基础模块和 4 个业务模块
- [ ] 解释单体多模块的好处（vs 单体单模块 vs 微服务）
- [ ] 设计自己的命名约定，写在项目 README 里
- [ ] 拉一份若依源码，找到 `sys_user` 表 → 对应的 Entity → Service → Controller，跟着走一遍
