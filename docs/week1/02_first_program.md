# Week 1 §02 · 在 IDEA 里跑通第一个 Java 程序

> 目标：理解 IDEA 项目结构、`package` / `class` / `main` 三件套，会用 debug。

---

## 1. 新建项目

**菜单**：`File → New → Project`

| 字段 | 值 |
|------|-----|
| Name | `java-week1` |
| Location | `~/Desktop/project/github/Java-Learn/code/week1` （你的仓库路径） |
| Language | Java |
| Build system | **IntelliJ**（先用 IDEA 自己的，Week 4 换 Maven） |
| JDK | 21（前面装的） |
| Add sample code | ✅ 勾上 |

点 Create，IDEA 会生成一个最简结构：

```
java-week1/
├── .idea/              # IDEA 元数据
├── src/                # 源码根（重点）
│   └── Main.java
└── java-week1.iml      # 模块文件
```

---

## 2. 关键概念：源码结构 vs 包名

```java
// src/com/learning/HelloWorld.java
package com.learning;             // ← 包声明，必须和目录结构一致

public class HelloWorld {         // ← public 类必须和文件名相同
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}
```

**规则**
- `package com.learning;` 这一行 ↔ `src/com/learning/` 目录路径（一一对应）
- 一个 `.java` 文件里只能有一个 `public` 类，且文件名 = 类名
- `main` 方法签名必须是 `public static void main(String[] args)`，一字不差

**命名约定**
- 包名：全小写，倒序域名 `com.公司名.项目.模块`
- 类名：大驼峰 `UserService`
- 方法 / 字段：小驼峰 `getUserById`
- 常量：全大写下划线 `MAX_SIZE`

---

## 3. 运行 + Debug

### 运行
- 类名旁的绿色三角箭头 → "Run 'HelloWorld.main()'"
- 快捷键 `⌃ + R`

### Debug
1. 在某一行行号左侧点击 → 出现红色断点
2. 右键 "Debug 'HelloWorld.main()'"，或 `⌃ + D`
3. 程序停在断点处
4. 下方 Debugger 窗口看变量值
5. `F8` 单步执行，`F7` 进入方法，`F9` 继续

**Debug 技巧**
- **条件断点**：右键断点 → Condition `i == 5`，只在条件满足时停
- **表达式求值**：`⌥ + F8`，在 debug 时计算任意表达式（神器）
- **修改变量**：Debugger 窗口里双击变量值可以临时改

---

## 4. IDEA 项目结构 vs Maven 项目结构

**现在（IDEA 默认）**
```
src/
└── 你的代码
```

**Week 4 学 Maven 后（标准）**
```
src/
├── main/
│   ├── java/             # 主代码
│   └── resources/        # 配置文件（yml / xml / properties）
└── test/
    ├── java/             # 测试代码
    └── resources/        # 测试配置
```

提前记住这个标准目录结构，所有 Spring Boot 项目都长这样。

---

## 5. 第一个交互程序：命令行计算器

打开本周代码示例 `code/week1/CalculatorCli.java`，运行起来。

期望体验：
```
请输入第一个数字：3
请输入运算符 (+ - * /)：*
请输入第二个数字：5
3.0 * 5.0 = 15.0
```

读完代码后回答：
1. `Scanner` 为什么要 `.close()`？不关会怎样？
2. `nextDouble()` 输入 `abc` 会怎样？怎么处理？
3. 除零会抛什么异常？怎么 try-catch？

答案：见代码里的 TODO 注释，自己补全。

---

## 6. 今天的 TODO

- [ ] 跑通 `HelloWorld.java`、`SyntaxCheatsheet.java`、`CalculatorCli.java`
- [ ] 在 `SyntaxCheatsheet.java` 里至少打 3 处断点 debug 一遍
- [ ] 把 `CalculatorCli.java` 改造支持：连续运算、回车退出
- [ ] 把今天踩的坑记到 `docs/week1/troubleshooting.md`（自己创建）
