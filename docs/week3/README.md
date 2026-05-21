# Week 3 · 并发 + JVM + Lambda + Stream

> 目标：理解 Java 多线程的写法（线程 / 线程池 / 锁 / 并发集合），JVM 内存的大致样子；用 Lambda + Stream 写更现代的代码。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_lambda_stream.md`](00_lambda_stream.md) | Lambda + 函数式接口 + Stream API |
| 01 | [`01_threads.md`](01_threads.md) | 线程基础：Thread / Runnable / Callable / Future |
| 02 | [`02_thread_pool_sync.md`](02_thread_pool_sync.md) | 线程池 + synchronized / volatile / 原子类 + 并发集合 |
| 03 | [`03_jvm_basics.md`](03_jvm_basics.md) | JVM 内存模型（栈 / 堆 / 元空间）+ GC 简介 |

## 配套代码

→ [`../../code/week3/`](../../code/week3/)

## 本周里程碑

到周末你应该能：
- 用 Stream 写出"按部门分组求平均薪资"
- 用 `ExecutorService` 提交任务并拿回结果
- 解释 `synchronized` 锁的是什么对象
- 写一个**线程安全**的计数器（用 `AtomicInteger` 或锁）
- 画出 JVM 主要内存区域（栈 / 堆 / 元空间）+ 知道什么放哪
- 完成"多线程文件统计工具"：统计目录下所有 .java 文件的代码行数
