# Week 1 · 代码

> 配套笔记：[`../../docs/week1/`](../../docs/week1/)

## 文件清单

| 文件 | 主题 | 配套笔记 |
|------|------|----------|
| `HelloWorld.java` | 第一个程序，理解 `main` 方法 + 命令行参数 | [02_first_program.md](../../docs/week1/02_first_program.md) |
| `SyntaxCheatsheet.java` | 语法对照速查：基本类型、字符串、控制流、集合、引用语义、== vs equals、重载 | [00_java_for_cpp_python_dev.md](../../docs/week1/00_java_for_cpp_python_dev.md) |
| `CalculatorCli.java` | 综合：Scanner 输入 + try-catch + switch 表达式 + 循环交互 | [02_first_program.md](../../docs/week1/02_first_program.md) |

## 在 IDEA 里运行

### ⚠️ 看不到 main 方法旁的绿色小三角？

Java 文件必须在 **Sources Root** 目录下，IDEA 才会识别 `main` 方法。

**操作步骤**（一次性配置）：

1. `File → Open` 打开 `Java-Learn` 整个仓库目录
2. 在左侧项目树里**右键** `code/week1` 文件夹
3. 选 `Mark Directory as` → `Sources Root`
   - 图标会变成**蓝色文件夹**（带橙色小角）
   - IDEA 重新索引几秒
4. 打开任意 `.java` 文件，`main` 方法左侧的**绿色三角**就出现了
5. 点三角 → `Run 'HelloWorld.main()'`，或快捷键 `⌃R` (Mac) / `Ctrl+Shift+F10` (Win)

> 对 `code/week2`、`code/week3` 也都各自 Mark 一次。从 Week 4 起用 Maven，**不用再 Mark**，打开 `pom.xml` 就自动识别。

### 如果还不行

- 检查项目 SDK：`File → Project Structure → Project → SDK` 要是 JDK 21
- 检查模块语言级别：`Project Structure → Modules → Language level` 选 21
- 重启 IDEA：`File → Invalidate Caches… → Invalidate and Restart`

## 命令行运行（不依赖 IDEA）

```bash
cd code/week1
javac HelloWorld.java
java HelloWorld arg1 arg2
```

## 本周自查清单

- [ ] 三个程序都能跑通
- [ ] 在 `SyntaxCheatsheet` 的每个 section 里至少打一个断点 debug 一遍
- [ ] 能不查文档解释：为什么 `Integer x=200,y=200; x==y` 是 `false`
- [ ] 能不查文档写出 HashMap 的增删改查
- [ ] 能解释 `String s = "hello"` 和 `String s = new String("hello")` 的区别
- [ ] 把 `CalculatorCli` 改造成支持 "连续运算"（用上次的结果作为下次的 a）

## 完成本周后

→ 进入 [Week 2](../week2/)：面向对象 + 集合 + 异常 + IO
