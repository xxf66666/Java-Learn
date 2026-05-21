# Week 10 §01 · 多模块工程结构

> 这套结构会用到 Week 12 完成，本节定下来后基本不再大改。

---

## 1. 整体结构

```
code/project/                          ← ERP 项目根
├── pom.xml                            ← 父 pom (packaging: pom)
├── erp-common/
│   └── src/main/java/com/learning/erp/common/
│       ├── result/Result.java
│       ├── result/PageResult.java
│       ├── exception/BusinessException.java
│       ├── exception/ErrorCode.java
│       ├── annotation/SysLog.java
│       ├── constant/Constants.java
│       └── util/...
├── erp-framework/
│   └── src/main/java/com/learning/erp/framework/
│       ├── security/  (JWT 工具、过滤器、SecurityConfig)
│       ├── mybatis/   (分页、自动填充、逻辑删除配置)
│       ├── web/       (GlobalExceptionHandler、CORS)
│       └── redis/     (RedisConfig)
├── erp-system/
│   └── src/main/java/com/learning/erp/system/
│       ├── user/      (entity / mapper / service / controller / dto)
│       ├── role/
│       ├── menu/
│       ├── dept/
│       ├── dict/
│       └── log/
├── erp-business/
│   └── src/main/java/com/learning/erp/business/
│       ├── material/
│       ├── warehouse/
│       ├── stock/
│       ├── purchase/
│       └── sale/
└── erp-admin/
    └── src/main/
        ├── java/com/learning/erp/AdminApplication.java
        └── resources/
            ├── application.yml
            ├── application-dev.yml
            └── application-prod.yml
```

---

## 2. 父 pom：统一版本管理

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.learning.erp</groupId>
    <artifactId>erp</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
        <relativePath/>
    </parent>

    <modules>
        <module>erp-common</module>
        <module>erp-framework</module>
        <module>erp-system</module>
        <module>erp-business</module>
        <module>erp-admin</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <mybatis-plus.version>3.5.7</mybatis-plus.version>
        <jjwt.version>0.12.6</jjwt.version>
        <knife4j.version>4.5.0</knife4j.version>
        <easyexcel.version>3.3.4</easyexcel.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- 同项目其他模块 -->
            <dependency>
                <groupId>com.learning.erp</groupId>
                <artifactId>erp-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.learning.erp</groupId>
                <artifactId>erp-framework</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.learning.erp</groupId>
                <artifactId>erp-system</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.learning.erp</groupId>
                <artifactId>erp-business</artifactId>
                <version>${project.version}</version>
            </dependency>

            <!-- 第三方版本统一 -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <!-- 更多... -->
        </dependencies>
    </dependencyManagement>
</project>
```

---

## 3. 模块依赖关系

```
erp-admin   ──→  erp-business  ──→  erp-system  ──→  erp-framework  ──→  erp-common
                                          ↓
                                  也依赖 erp-framework
```

简单原则：**上层依赖下层，下层不依赖上层**。

---

## 4. 实操命名习惯

### 4.1 包名

每个业务领域一个包：

```
com.learning.erp.system.user
   ├── entity/SysUser.java
   ├── mapper/SysUserMapper.java
   ├── service/SysUserService.java
   ├── controller/SysUserController.java
   └── dto/UserCreateReq.java / UserUpdateReq.java / UserVO.java
```

### 4.2 类后缀约定

| 后缀 | 含义 |
|------|------|
| `*Entity` 或不带后缀 | 数据库实体（与表对应） |
| `*DTO` / `*Req` | 入参 / 传输对象 |
| `*VO` | 出参 / 视图对象 |
| `*Mapper` | MyBatis Mapper 接口 |
| `*Service` / `*ServiceImpl` | Service 接口 / 实现 |
| `*Controller` | REST 接口 |

实体一般直接用类名（`SysUser`、`Material`）；DTO/VO 是 Controller 进出层用的，**不直接暴露 Entity**。

---

## 5. 主启动放在 erp-admin

```java
@SpringBootApplication(scanBasePackages = "com.learning.erp")
@MapperScan("com.learning.erp.**.mapper")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
```

注意：
- `scanBasePackages` 要写到根，让所有子模块的 `@Component` 都能扫到
- `@MapperScan` 同理，用通配符匹配所有 `mapper` 子包

---

## 6. 自查

- [ ] 在 `code/project/` 下建好父 pom + 5 个子模块的最小骨架
- [ ] 父 pom 把所有版本号都统一管理（`<dependencyManagement>`）
- [ ] 子模块之间能正确引用，编译通过
- [ ] 主启动放在 `erp-admin`，`mvn -pl erp-admin spring-boot:run` 跑起来
