# Week 1 §01 · 环境搭建（macOS + IntelliJ IDEA）

> 一次性配置，后面 12 周用同一套环境。

---

## 1. 安装 JDK 21（LTS）

**推荐方式 A：SDKMAN（多版本切换）**

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

sdk install java 21.0.5-tem      # Temurin 是 Eclipse 维护的开源 OpenJDK
sdk default java 21.0.5-tem

java -version
# openjdk version "21.0.5" 2024-10-15 LTS
```

**推荐方式 B：Homebrew**

```bash
brew install openjdk@21
sudo ln -sfn $(brew --prefix openjdk@21)/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-21.jdk
java -version
```

**验证**

```bash
which java                  # 应该指向 JDK 21
javac -version              # 编译器版本
echo $JAVA_HOME             # SDKMAN 会自动设置
```

---

## 2. 安装 IntelliJ IDEA

下载地址：<https://www.jetbrains.com/idea/>

| 版本 | 价格 | 推荐场景 |
|------|------|---------|
| Ultimate | 付费 / 30 天试用 / 学生免费 | **首选** —— Spring / 数据库 / HTTP Client / 微服务等全套支持 |
| Community | 免费 | 只学 Java 基础够用，但学到 Spring 后会缺很多功能 |

**学生注册**：用 `.edu` 邮箱在 <https://www.jetbrains.com/community/education/> 申请，全套免费 1 年。

### 必装插件（IDEA 内 `Settings → Plugins`）

- **Lombok**（IDEA Ultimate 内置，Community 需手装）—— 注解生成 getter/setter
- **Chinese (Simplified) Language Pack**（可选，中文界面）
- **Translation** —— 划词翻译，看英文文档救命
- **Rainbow Brackets** —— 彩色括号配对
- **MyBatisX** —— Mapper ↔ XML 跳转（学到 Week 7 用）
- **Maven Helper** —— 依赖冲突分析
- **GitToolBox** —— 行内 git blame
- **String Manipulation** —— 命名风格快速转换
- **Key Promoter X** —— 快捷键提示，强制肌肉记忆

### 重要快捷键（macOS）

| 快捷键 | 功能 |
|--------|------|
| `⌘ + N` | 新建（类 / 方法 / 字段） |
| `⌘ + O` | 查找类 |
| `⌘ + ⇧ + O` | 查找文件 |
| `⌘ + ⇧ + F` | 全局搜索 |
| `⌘ + B` | 跳转到定义 |
| `⌥ + F7` | 查找用法 |
| `⌘ + ⌥ + L` | 格式化代码 |
| `⌘ + ⌥ + O` | 优化 import |
| `⌃ + R` | 运行 |
| `⌃ + D` | 调试 |
| `⌘ + F8` | 切换断点 |
| `F8` / `F7` | 单步跳过 / 进入 |
| `⌥ + ⏎` | 快速修复（万能） |
| `⌃ + T` | 重构菜单 |
| `⇧ ⇧` | 搜索任何东西（Search Everywhere） |

> 把 `Key Promoter X` 装上，几周就能形成肌肉记忆。

---

## 3. 配置 IDEA

### 3.1 设置 JDK

`File → Project Structure → SDKs → +` 添加刚才装的 JDK 21。

### 3.2 设置字符编码（避免中文乱码）

`Settings → Editor → File Encodings`：
- Global / Project / Default: **UTF-8**
- Properties Files: UTF-8，勾选 "Transparent native-to-ascii conversion"

### 3.3 设置 Maven

`Settings → Build, Execution, Deployment → Build Tools → Maven`：

如果用 IDEA 内置 Maven，无需配置；如果你想用自己装的 Maven：

```
Maven home path: /opt/homebrew/Cellar/maven/3.9.x/libexec
User settings file: ~/.m2/settings.xml
```

**配置阿里云镜像（国内下载快 10 倍）** —— 编辑 `~/.m2/settings.xml`：

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <name>aliyun maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

### 3.4 设置外观（可选）

- `Settings → Appearance → Theme`：Darcula（暗）/ Light（亮）
- `Settings → Editor → Font`：JetBrains Mono（自带）+ 字号 14-15
- `Settings → Editor → General → Auto Import`：勾选 "Add unambiguous imports on the fly" 和 "Optimize imports on the fly"

---

## 4. 装 MySQL + Redis（用 Docker，最省心）

```bash
# 装 Docker Desktop: https://www.docker.com/products/docker-desktop/

# MySQL 8
docker run -d --name mysql8 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=test \
  -p 3306:3306 \
  -v ~/docker-data/mysql:/var/lib/mysql \
  mysql:8

# Redis 7
docker run -d --name redis7 \
  -p 6379:6379 \
  -v ~/docker-data/redis:/data \
  redis:7

# 验证
docker ps
docker exec -it mysql8 mysql -uroot -proot -e "SELECT VERSION();"
docker exec -it redis7 redis-cli PING
```

### 数据库可视化客户端

- **DBeaver Community**（免费，推荐）：<https://dbeaver.io>
- **DataGrip**（JetBrains 出品，付费，IDEA Ultimate 内置数据库模块基本等价）

连接信息：
- Host: `localhost`, Port: `3306`, User: `root`, Password: `root`

---

## 5. 接口测试工具

- **Apifox**（推荐，国产，免费，集成度高）：<https://apifox.com>
- **Postman**：老牌，也行
- **IDEA Ultimate 内置 HTTP Client**：直接写 `.http` 文件，最方便，无需第三方

---

## 6. Git 配置（如果还没配）

```bash
git config --global user.name "你的名字"
git config --global user.email "你的邮箱"
git config --global core.editor "code --wait"   # 用 VS Code 当编辑器
git config --global init.defaultBranch main
```

---

## 7. 检查清单

跑完这些，环境就 OK：

- [ ] `java -version` 显示 21
- [ ] `javac -version` 显示 21
- [ ] IDEA 能打开，能新建 Java 项目
- [ ] IDEA 装好了 Lombok / Translation / MyBatisX
- [ ] `docker ps` 看到 mysql8 和 redis7 running
- [ ] DBeaver / DataGrip 能连上 MySQL
- [ ] Apifox 装好（或 IDEA HTTP Client 能用）
- [ ] git 配置好用户名邮箱

---

## 下一步

→ [`02_first_program.md`](02_first_program.md)：在 IDEA 里新建项目，跑通 HelloWorld，理解 IDEA 项目结构
