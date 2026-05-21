# Week 3 · 代码

> 配套笔记：[`../../docs/week3/`](../../docs/week3/)

## 模块清单

| 子目录 | 主题 | 入口 |
|--------|------|------|
| `stream_demo/` | Lambda、函数式接口、Stream API、Optional | `StreamDemo.main` |
| `thread_basics/` | Runnable / Callable / Future、中断 | `ThreadBasicsDemo.main` |
| `thread_pool/` | ThreadPoolExecutor、synchronized vs AtomicInteger、CountDownLatch | `ThreadPoolDemo.main` |
| `concurrent_map/` | ConcurrentHashMap 多线程词频统计 | `ConcurrentWordCount.main` |
| `jvm_demo/` | 堆 OOM、栈溢出、Runtime 信息 | `JvmStressTest.main` |
| `file_counter/` | **综合**：多线程统计目录下所有 .java 文件代码行数 | `FileLineCounter.main` |

## 命令行运行

```bash
cd code/week3
javac -d build $(find . -name "*.java")

java -cp build stream_demo.StreamDemo
java -cp build thread_basics.ThreadBasicsDemo
java -cp build thread_pool.ThreadPoolDemo
java -cp build concurrent_map.ConcurrentWordCount
java -cp build jvm_demo.JvmStressTest
java -cp build file_counter.FileLineCounter ..        # 统计整个 Java-Learn 仓库
```

## 本周自查

- [ ] StreamDemo 完整跑通；能解释 `groupingBy + averagingDouble` 的输出
- [ ] ThreadBasicsDemo 跑通；能解释为什么 worker 收到 interrupt 后能干净退出
- [ ] ThreadPoolDemo 跑通；记录 `synchronized` vs `AtomicInteger` 的耗时差异
- [ ] ConcurrentWordCount 输出正确
- [ ] 打开 JvmStressTest 里的 `heapOOM()`、加 `-Xmx32m` 跑出堆溢出
- [ ] FileLineCounter 跑出整个仓库的统计
