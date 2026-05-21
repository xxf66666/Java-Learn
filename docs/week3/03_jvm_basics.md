# Week 3 §03 · JVM 基础

> 目标：知道 Java 程序运行起来后内存"长什么样"，理解几个常见的内存问题。这是面试常考点，也是后面调优的基础。

---

## 1. Java 程序怎么跑起来的

```
.java 源代码
   ↓ javac 编译
.class 字节码  ←── 跨平台的"中间语言"
   ↓ java 启动 JVM
JVM 加载 → 解释执行 + JIT 编译热点代码 → 机器码 → CPU 执行
```

**关键概念**
- **JVM** = Java Virtual Machine，Java 程序的"运行容器"，每个 `java xxx` 命令启动一个 JVM
- **JIT**（Just-In-Time）= 把跑得最热的方法编译成本地机器码，比纯解释快得多
- **跨平台**靠的是 JVM：同一份 .class 在 Windows / Mac / Linux 上都能跑（前提是装了对应平台的 JDK）

---

## 2. JVM 内存结构（HotSpot JDK 8+）

```
┌─────────────────────────────────────────────┐
│                    JVM                      │
│                                             │
│  ┌──────────────┐  ┌──────────────────────┐│
│  │  方法区/      │  │       堆 (Heap)       ││
│  │  元空间       │  │  ┌───────────────┐    ││
│  │  Metaspace   │  │  │  新生代       │    ││
│  │              │  │  │ Eden + S0/S1 │    ││
│  │  - 类元信息   │  │  └───────────────┘    ││
│  │  - 静态字段   │  │  ┌───────────────┐    ││
│  │  - 常量池     │  │  │  老年代       │    ││
│  └──────────────┘  │  └───────────────┘    ││
│                    └──────────────────────┘│
│                                             │
│  ┌──────────────┐  ┌──────────────┐        │
│  │ JVM 栈       │  │ 本地方法栈    │        │
│  │ (每线程一个)  │  │ (每线程一个)   │        │
│  └──────────────┘  └──────────────┘        │
│                                             │
│  ┌─────────────────────────────────────────┐│
│  │     程序计数器 (每线程一个)               ││
│  └─────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

### 2.1 堆（Heap）——**所有对象都放这里**

- `new` 出来的对象、数组都在堆
- 所有线程共享
- GC 主要工作场所
- 分新生代 / 老年代（**分代收集**思想）

```java
Student s = new Student("A", 20);
//          ^^^^^^^^^^^^^^^^^^^^ 在堆上
// s       ^ 这个引用变量在栈上
```

### 2.2 栈（JVM Stack）—— 每个线程一个

- 存**局部变量**、方法参数、调用链
- 每次调方法，就 push 一个**栈帧**；方法返回，栈帧 pop
- 线程私有（不会有并发问题）
- `StackOverflowError` 就是栈帧太深（典型场景：无限递归）

```java
public void foo() {
    int x = 10;            // x 在栈上
    String s = "hello";    // s 引用在栈，字符串本身在堆
    bar();                 // 调 bar：push 新栈帧
}                          // foo 返回：pop foo 的栈帧
```

### 2.3 方法区 / 元空间（Metaspace）

- 存**类元信息**（类名、父类、方法签名、字段定义）
- 存**静态字段**（`static int x`）
- 存**字符串常量池**（"hello" 字面量）

> JDK 8 起，永久代被元空间替代，元空间使用本地内存（不再受 -Xmx 限制）。

### 2.4 程序计数器 + 本地方法栈

知道有这两个就行，工作中基本不用关心。

---

## 3. 对象生命周期 + GC

```
new 对象 → Eden 区
   ↓ Eden 满，触发 Minor GC
活下来 → Survivor 区（S0 ↔ S1 来回搬几次）
   ↓ 搬了一定次数（默认 15）
晋升 → 老年代
   ↓ 老年代满
触发 Full GC（慢，要尽量避免）
```

**核心思想**
- 大部分对象朝生夕死 → 新生代频繁但快速回收（Minor GC）
- 长寿对象进老年代 → 偶尔但耗时（Full GC）

### 常见 GC 算法 / 收集器（了解即可）

| 收集器 | 适用场景 |
|--------|---------|
| **Serial** | 单线程，小程序 |
| **Parallel** | 吞吐优先，JDK 8 默认 |
| **CMS** | 低延迟（已废弃） |
| **G1** | JDK 9+ 默认，新生代+老年代统一处理，可控停顿 |
| **ZGC** | JDK 11+，亚毫秒级停顿，大堆首选 |
| **Shenandoah** | OpenJDK 低延迟选项 |

JDK 21 工作场景下：用 **G1**（默认）或 **ZGC**（堆 ≥ 16G）。

---

## 4. JVM 启动参数

```bash
java -Xms512m -Xmx2g \             # 堆初始 512M、最大 2G
     -Xss512k \                     # 每个线程栈大小
     -XX:+UseG1GC \                  # 用 G1 收集器
     -XX:MaxMetaspaceSize=256m \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=./heap.hprof \
     -Dfile.encoding=UTF-8 \
     -jar app.jar
```

工作中最常调整：
- `-Xmx` 堆最大
- `-Xms` 堆初始（建议和 `-Xmx` 一样，避免动态扩容）
- 出问题时加 `-XX:+HeapDumpOnOutOfMemoryError`

---

## 5. 常见内存错误

| 错误 | 含义 | 典型原因 |
|------|------|---------|
| `OutOfMemoryError: Java heap space` | 堆爆了 | 数据加载太多 / 内存泄漏 / `-Xmx` 太小 |
| `OutOfMemoryError: Metaspace` | 类元空间爆了 | 类加载器泄漏 / 反射/动态生成类太多 |
| `OutOfMemoryError: unable to create new native thread` | 线程数到上限 | 线程池配置错 / 没关线程 |
| `StackOverflowError` | 栈帧太深 | 无限递归 / 递归太深 |

---

## 6. 类加载机制：双亲委派（面试题，工作中很少改）

```
Bootstrap ClassLoader   ← 加载 JDK 核心（rt.jar / java.base）
   ↑
Platform ClassLoader     ← 加载平台扩展
   ↑
Application ClassLoader  ← 加载 classpath / 你写的代码
   ↑
你自定义的 ClassLoader
```

**规则**：找类时**先问父加载器**，父找不到才自己加载。这样保证 `java.lang.String` 永远是 JDK 那个，不会被你自己写的 `String` 篡改。

---

## 7. 实用：看程序的内存

```bash
# 列出本机所有 Java 进程
jps -l

# 看某进程的内存使用
jstat -gc <pid> 1000 5     # 每秒一次，连续 5 次

# 导出堆快照
jmap -dump:format=b,file=heap.hprof <pid>

# 用 Eclipse Memory Analyzer (MAT) 或 IDEA Profiler 分析
```

IDEA Ultimate 自带 Profiler，启动应用时点 "Run with Profiler" → CPU / Memory 图表实时看。

---

## 8. 自查

- [ ] 画出 JVM 主要内存区域（堆 / 栈 / 元空间）+ 知道各放什么
- [ ] 解释 `StackOverflowError` 和 `OutOfMemoryError` 的区别
- [ ] 解释 `new Foo()` 这一行后，对象和引用变量分别在哪里
- [ ] 知道 G1 / ZGC 大致什么场景用
- [ ] 写一段会 `StackOverflowError` 的代码（无限递归）
- [ ] 用 `jps` + `jstat` 看一个跑着的 Java 程序的 GC

## 代码示例

→ [`code/week3/jvm_demo/JvmStressTest.java`](../../code/week3/jvm_demo/JvmStressTest.java)
