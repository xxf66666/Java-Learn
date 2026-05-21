# ERP 项目（多模块）

Phase 4 实战项目，从 Week 10 开始建仓，Week 12 收尾。

## 模块

```
erp/
├── erp-common/        Result / 异常 / 错误码 / SysLog 注解 / 工具
├── erp-framework/     Spring Security / JWT / MyBatis-Plus / 全局异常 / 操作日志切面
├── erp-system/        用户 / 角色 / 菜单 / 字典 / 操作日志（RBAC 核心）
├── erp-business/      物料 / 库存 / 采购 / 销售 (Week 11 落地)
└── erp-admin/         主启动 + application.yml
```

## 跑

```bash
# 1. 起 MySQL + Redis
docker run -d --name mysql8 -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8
docker run -d --name redis7 -p 6379:6379 redis:7

# 2. 跑建表脚本
mysql -h127.0.0.1 -uroot -proot < scripts/erp.sql

# 3. 编译 + 启动
cd code/project
mvn clean install -DskipTests
mvn -pl erp-admin spring-boot:run
```

启动后：
- `http://localhost:8080/doc.html` —— 接口文档
- 默认账号：`admin / admin123`（超管，所有权限）；`tester / admin123`（只读用户）

## 接口速查

```http
### 登录
POST /api/login
{
  "username": "admin",
  "password": "admin123"
}

### 查看我的菜单（要 token）
GET /api/menus/me
Authorization: Bearer {token}

### 查看我的信息（要 token）
GET /api/me
Authorization: Bearer {token}

### 用户列表（需要 sys:user:list 权限）
GET /api/users?page=1&size=10
Authorization: Bearer {token}

### 字典
GET /api/dict/gender
GET /api/dict/order_status
```

## 当前能跑的功能

- [x] 用户登录 / 退出 / 获取自己信息
- [x] 拉取我的权限菜单（前端动态渲染基础）
- [x] 用户 CRUD（含按权限点鉴权）
- [x] 字典查询
- [x] 自动写操作日志（标了 `@SysLog` 的方法）
- [ ] Week 11：物料 / 库存 / 采购 / 销售
- [ ] Week 12：前后端联调 / Docker 部署

## 学习要点

- 多模块 Maven 工程的依赖方向：admin → business → system → framework → common
- `LoginUserLoader` + `OperationLogSink` 用 SPI 风格让 framework 不反过来依赖 system
- 权限模型 RBAC 5 表（user / role / menu + 两张关联表）
- `@PreAuthorize("hasAnyAuthority('*:*:*', 'sys:user:list')")` 用 `*:*:*` 给超管开后门
- `@SysLog` 注解 + AOP 切面自动入库操作日志（异步）
