# Week 4 §00 · Maven

> 目标：理解 Maven 解决什么问题、`pom.xml` 怎么写、多模块项目是什么样。

---

## 1. 为什么需要 Maven

前 3 周我们手工 `javac` + `java -cp ...`，但真实项目里：
- 要用 100+ 第三方 jar 包（Spring、MyBatis、Jackson、Logback...）
- 这些 jar 还互相依赖、版本要对得上
- 还要编译、打包、跑测试、部署 ...

Maven 就是为这套流程而生：**依赖管理 + 项目构建 + 标准化目录**。

---

## 2. 标准目录结构

```
my-project/
├── pom.xml                  ← 项目描述（核心）
├── src/
│   ├── main/
│   │   ├── java/            ← 主代码
│   │   └── resources/       ← 配置文件（yml/xml/properties）
│   └── test/
│       ├── java/            ← 测试代码
│       └── resources/       ← 测试配置
└── target/                  ← 编译/打包产物（gitignore）
```

**所有 Spring Boot 项目都长这样**，提前记住。

---

## 3. 最小 `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <!-- 项目坐标（三件套）：唯一标识一个 artifact -->
    <groupId>com.learning</groupId>
    <artifactId>week4-demo</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- 添加依赖：在 https://central.sonatype.com 搜 -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.13</version>
        </dependency>
    </dependencies>
</project>
```

**坐标**`groupId : artifactId : version`（简写 `GAV`），全球唯一。

---

## 4. 依赖管理

### 依赖范围 `<scope>`

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>            <!-- 只在测试期可用，不打进最终包 -->
</dependency>
```

| scope | 编译期 | 测试期 | 运行期 | 打包 | 典型 |
|-------|--------|--------|--------|------|------|
| `compile`（默认） | ✅ | ✅ | ✅ | ✅ | Spring Boot Starter |
| `test` | ❌ | ✅ | ❌ | ❌ | JUnit / Mockito |
| `provided` | ✅ | ✅ | ❌ | ❌ | servlet-api（容器提供） |
| `runtime` | ❌ | ✅ | ✅ | ✅ | JDBC 驱动 |

### 传递依赖

A 依赖 B，B 依赖 C → A 自动得到 C。这就是 Maven 比手工管理 jar 强的地方。

### 排除依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 版本统一管理

```xml
<properties>
    <spring-boot.version>3.3.0</spring-boot.version>
    <mybatis-plus.version>3.5.7</mybatis-plus.version>
</properties>

<dependencies>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>${mybatis-plus.version}</version>
    </dependency>
</dependencies>
```

---

## 5. Maven 命令

```bash
mvn clean                    # 删除 target/
mvn compile                  # 编译主代码
mvn test                     # 跑测试（会先编译）
mvn package                  # 打 jar（会先编译 + 测试）
mvn install                  # 装到本地仓库 ~/.m2/repository
mvn clean package -DskipTests   # 跳过测试打包
mvn dependency:tree          # 看依赖树（排查版本冲突神器）
```

**实操**：90% 时间只用 `mvn clean package` 和 IDEA 右侧的 Maven 面板。

---

## 6. 阿里云镜像（国内速度提升 10x）

编辑 `~/.m2/settings.xml`：

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

---

## 7. 多模块项目（为 ERP 铺垫）

ERP 项目会拆成多个模块，目录长这样：

```
erp/
├── pom.xml                    ← 父 pom（packaging: pom）
├── erp-common/
│   ├── pom.xml
│   └── src/...
├── erp-system/                 ← 用户/角色/菜单/字典
│   └── pom.xml
└── erp-admin/                  ← 主启动
    └── pom.xml
```

### 父 pom

```xml
<project>
    <groupId>com.learning.erp</groupId>
    <artifactId>erp</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>            <!-- 注意 pom 而不是 jar -->

    <modules>
        <module>erp-common</module>
        <module>erp-system</module>
        <module>erp-admin</module>
    </modules>

    <!-- 在父 pom 里统一管理版本（不会下载） -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.learning.erp</groupId>
                <artifactId>erp-common</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 子模块 pom

```xml
<project>
    <parent>
        <groupId>com.learning.erp</groupId>
        <artifactId>erp</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>erp-system</artifactId>

    <dependencies>
        <!-- 引用同项目的另一个模块（不写版本，从父 pom 取） -->
        <dependency>
            <groupId>com.learning.erp</groupId>
            <artifactId>erp-common</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## 8. IDEA 里的 Maven 操作

- **打开 Maven 项目**：File → Open → 选 `pom.xml` 所在目录
- **重新加载依赖**：右侧 Maven 面板 → 刷新图标
- **看依赖树**：右侧 Maven 面板 → Dependencies 树
- **跑命令**：右侧 Maven 面板 → Lifecycle / Plugins → 双击命令

---

## 9. 自查

- [ ] 用 IDEA 新建一个 Maven 项目（不是普通项目）
- [ ] 看懂 `pom.xml` 里 GAV 三件套的意思
- [ ] 加一个第三方依赖（如 `commons-lang3`），import 使用
- [ ] 跑 `mvn dependency:tree` 看到依赖关系
- [ ] 排除一个传递依赖
- [ ] 用 `${...}` 统一管理 2 个依赖的版本

## 代码示例

→ [`code/week4/maven-demo/`](../../code/week4/maven-demo/) —— 最小 Maven 项目
→ [`code/week4/multi-module/`](../../code/week4/multi-module/) —— 多模块结构示例
