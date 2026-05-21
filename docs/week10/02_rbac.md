# Week 10 §02 · RBAC 权限模型

> 5 张表搞定一切：用户 / 角色 / 菜单 / 用户-角色 / 角色-菜单。市面上 99% 的管理系统是这套。

---

## 1. 核心思想

```
用户 (sys_user)
  ↓ N:N
角色 (sys_role)
  ↓ N:N
菜单/权限 (sys_menu)
```

- 一个用户能有多个角色
- 一个角色能有多个菜单/按钮权限
- 用户的实际权限 = 所有角色权限的并集

---

## 2. 五表设计

```sql
-- 用户
CREATE TABLE sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64) NOT NULL UNIQUE,
    password    VARCHAR(128) NOT NULL,         -- BCrypt
    nickname    VARCHAR(64),
    email       VARCHAR(128),
    phone       VARCHAR(20),
    avatar      VARCHAR(255),
    dept_id     BIGINT,                          -- 所属部门
    status      TINYINT DEFAULT 1,               -- 0 禁用 / 1 启用
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT DEFAULT 0
);

-- 角色
CREATE TABLE sys_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(64) NOT NULL,            -- 角色名（中文显示）
    code        VARCHAR(64) NOT NULL UNIQUE,     -- 角色编码（程序判断用：admin / user / finance）
    sort        INT DEFAULT 0,
    data_scope  TINYINT DEFAULT 1,                -- 1 全部 / 2 本部门及下 / 3 本部门 / 4 仅本人 / 5 自定义
    status      TINYINT DEFAULT 1,
    remark      VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT DEFAULT 0
);

-- 菜单（含按钮级权限）
CREATE TABLE sys_menu (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id   BIGINT      DEFAULT 0,           -- 树形：0 是根
    name        VARCHAR(64) NOT NULL,             -- 显示名
    type        TINYINT     NOT NULL,             -- 1 目录 / 2 菜单 / 3 按钮
    path        VARCHAR(128),                     -- 前端路由 path
    component   VARCHAR(128),                     -- 前端组件路径
    permission  VARCHAR(128),                     -- 权限标识 user:add / user:edit
    icon        VARCHAR(64),
    sort        INT DEFAULT 0,
    status      TINYINT DEFAULT 1,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户-角色 关联
CREATE TABLE sys_user_role (
    user_id     BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- 角色-菜单 关联
CREATE TABLE sys_role_menu (
    role_id     BIGINT NOT NULL,
    menu_id     BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id)
);
```

---

## 3. 数据流：登录到拿权限

```
1. 用户登录 → 拿到 JWT token
2. 前端拉菜单 / 权限：GET /api/user/me
   → 后端：
     a. 从 token 解出 userId
     b. user_id → 用户的所有 role_id
     c. role_id 们 → 所有 menu_id
     d. menu_id → 所有 permission（如 "user:add"）
     e. 返回 { user, roles, permissions, menus(树) }
3. 前端：
   - 用 menus 渲染左侧菜单 / 路由
   - 用 permissions 控制按钮显示（v-permission="user:add"）
4. 后端接口：用 @PreAuthorize("hasAuthority('user:add')") 守护
```

---

## 4. Spring Security 集成

JWT Filter 解析 token 后，把 `permissions` 设到 `Authentication.authorities` 里：

```java
List<SimpleGrantedAuthority> authorities = permissions.stream()
    .map(SimpleGrantedAuthority::new)            // 字符串 → GrantedAuthority
    .collect(Collectors.toList());

UsernamePasswordAuthenticationToken auth =
    new UsernamePasswordAuthenticationToken(userId, null, authorities);
SecurityContextHolder.getContext().setAuthentication(auth);
```

接口上：

```java
@PreAuthorize("hasAuthority('user:add')")
@PostMapping
public Result<Long> create(@RequestBody UserCreateReq req) { ... }
```

---

## 5. 数据权限：不止"能不能看"，还有"看哪些数据"

场景：所有销售都能看销售订单，但每个销售**只能看自己部门的**。

```sql
SELECT * FROM sale_order
WHERE deleted = 0
  AND dept_id IN (1, 5, 6);    -- 当前用户所在部门 + 子部门
```

实现思路（高级题，本周先了解）：
1. 用户登录后算出他能看的部门列表，存 Redis
2. AOP 切面拦带 `@DataScope` 的方法
3. 在查询前修改 `QueryWrapper` 加 `IN (depts)` 条件

若依 ruoyi 的源码可参考。本仓库 MVP 阶段先不实现，看完笔记知道有这回事即可。

---

## 6. 内置 admin 用户

通常初始化脚本里插：

```sql
INSERT INTO sys_user (username, password, nickname, status)
VALUES ('admin', '$2a$10$xxxx...', '超级管理员', 1);  -- BCrypt 加密的 'admin123'

INSERT INTO sys_role (name, code, data_scope) VALUES ('超级管理员', 'admin', 1);

INSERT INTO sys_user_role VALUES (1, 1);
-- 给 admin 角色绑所有菜单（具体看菜单数据）
```

---

## 7. 自查

- [ ] 不查文档画出 5 张表的 ER 图
- [ ] 解释为什么用"用户-角色-菜单" 三层，不直接"用户-菜单"
- [ ] 在 SQL 里写出"查询用户 1 的所有权限标识"（三表 join）
- [ ] 解释什么时候用 `hasRole` vs `hasAuthority`
- [ ] 数据权限的 4 种范围分别是什么场景
