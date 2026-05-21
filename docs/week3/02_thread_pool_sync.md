# Week 3 §02 · 线程池 + 同步 + 并发集合

> 实际工作中**不要裸用 `new Thread()`**，全部走线程池。本章讲为什么以及怎么用。

---

## 1. 为什么用线程池

- 创建线程是昂贵的操作（OS 资源）
- 不限并发数会把 CPU / 内存打爆
- 线程池**复用**线程 + **限制**最大并发数 + **统一管理**生命周期

---

## 2. `ExecutorService`：标准入口

```java
import java.util.concurrent.*;

// 固定 4 个线程
ExecutorService pool = Executors.newFixedThreadPool(4);

// 提交无返回值任务
pool.execute(() -> System.out.println("hello"));

// 提交有返回值任务
Future<Integer> f = pool.submit(() -> 42);
Integer r = f.get();

// 优雅关闭
pool.shutdown();                                  // 不接新任务，等所有已提交任务完成
pool.awaitTermination(10, TimeUnit.SECONDS);     // 最多等 10 秒

// 强制关闭
pool.shutdownNow();                               // 中断所有线程，立即返回
```

### `Executors` 的工厂方法

```java
Executors.newFixedThreadPool(4);           // 固定大小
Executors.newCachedThreadPool();            // 按需创建，空闲回收
Executors.newSingleThreadExecutor();        // 串行
Executors.newScheduledThreadPool(2);        // 定时 / 周期任务
```

**⚠️ 阿里规约**：禁止用 `Executors` 创建线程池，要用 `ThreadPoolExecutor` 直接构造，以便明确指定队列容量。但学习阶段先用 `Executors`，知道这个差异。

### `ThreadPoolExecutor` 七参构造（面试常考）

```java
new ThreadPoolExecutor(
    4,                                  // corePoolSize：核心线程数
    8,                                  // maximumPoolSize：最大线程数
    60, TimeUnit.SECONDS,               // 非核心线程空闲多久后回收
    new LinkedBlockingQueue<>(100),     // 任务队列
    Executors.defaultThreadFactory(),   // 线程工厂（命名等）
    new ThreadPoolExecutor.AbortPolicy()  // 拒绝策略：队列满 + 线程满怎么办
);
```

**任务到来时的流程**

```
来一个任务
  → 核心线程没满？开新核心线程跑
  → 满了？放进队列
  → 队列满了？开非核心线程（直到 max）
  → 还满？走拒绝策略
```

**拒绝策略**

| 策略 | 行为 |
|------|------|
| `AbortPolicy`（默认） | 抛 `RejectedExecutionException` |
| `CallerRunsPolicy` | 让调用方线程自己跑（"减速带"） |
| `DiscardPolicy` | 静默丢弃 |
| `DiscardOldestPolicy` | 丢弃队列里最老的任务 |

---

## 3. 同步：让多线程不互相干扰

### 3.1 `synchronized`：最常见

```java
public class Counter {
    private int value;

    public synchronized void incr() {          // 方法级别：锁的是 this
        value++;
    }

    public int get() {
        synchronized (this) {                   // 块级别：明确锁对象
            return value;
        }
    }
}
```

**锁的是什么？**
- 实例方法上 → 锁 `this`
- 静态方法上 → 锁 `XxxClass.class`
- 同步块 → 锁 `synchronized(obj)` 里写的 obj

### 3.2 `ReentrantLock`：比 synchronized 灵活

```java
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int value;
    private final ReentrantLock lock = new ReentrantLock();

    public void incr() {
        lock.lock();
        try {
            value++;
        } finally {
            lock.unlock();           // ⚠️ 必须放 finally，否则异常就死锁
        }
    }
}
```

**优势**
- `tryLock(timeout)`：限时尝试加锁
- 可中断：`lockInterruptibly()`
- 公平锁：`new ReentrantLock(true)`

什么时候用：**默认 `synchronized` 够用**；需要超时 / 公平 / 多条件变量时换 `ReentrantLock`。

### 3.3 `volatile`：可见性

```java
private volatile boolean running = true;
```

`volatile` 保证：**一个线程改了，其它线程立刻看得到**（但**不保证原子性**，不能替代 `synchronized` 做计数器）。

典型用法：**标志位**。

```java
class Worker implements Runnable {
    private volatile boolean stop = false;

    public void stop() { stop = true; }

    public void run() {
        while (!stop) { /* work */ }
    }
}
```

### 3.4 原子类：性能最好的"小操作"

```java
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();       // 原子 +1，无锁，性能优于 synchronized
counter.get();
counter.compareAndSet(0, 10);    // CAS：如果当前是 0，改成 10
```

类似的还有 `AtomicLong`、`AtomicReference<T>`、`AtomicBoolean`。

---

## 4. 并发集合：替代 `Collections.synchronizedXxx`

```java
import java.util.concurrent.*;

ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
BlockingQueue<String> bq = new LinkedBlockingQueue<>(100);
```

**最常用：`ConcurrentHashMap`**
- 高性能并发读写
- 替代 `HashMap` 用于多线程场景
- 不要再用老的 `Hashtable` 或 `Collections.synchronizedMap`

```java
map.putIfAbsent("a", 1);
map.compute("a", (k, v) -> v == null ? 1 : v + 1);    // 原子地"读-改-写"
map.merge("a", 1, Integer::sum);                       // 同上，常用于计数
```

---

## 5. 协作工具：JUC 三件套

### 5.1 `CountDownLatch`：等一组任务全部完成

```java
CountDownLatch latch = new CountDownLatch(3);

for (int i = 0; i < 3; i++) {
    int taskId = i;
    pool.submit(() -> {
        System.out.println("task " + taskId + " done");
        latch.countDown();        // 计数 -1
    });
}

latch.await();                     // 阻塞，直到计数变 0
System.out.println("all done");
```

### 5.2 `CyclicBarrier`：让一组线程"约好同时出发"

```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("all ready"));

for (int i = 0; i < 3; i++) {
    int id = i;
    pool.submit(() -> {
        System.out.println("worker " + id + " ready");
        try { barrier.await(); } catch (Exception e) {}
        System.out.println("worker " + id + " go!");
    });
}
```

### 5.3 `Semaphore`：限流

```java
Semaphore sem = new Semaphore(3);            // 最多同时 3 个

pool.submit(() -> {
    try {
        sem.acquire();
        // 关键操作
    } finally {
        sem.release();
    }
});
```

---

## 6. 自查

- [ ] 用 `synchronized` 修复上节的 `Counter` 并发 bug
- [ ] 用 `AtomicInteger` 重写一遍，对比性能（写两段代码计时）
- [ ] 用 `ConcurrentHashMap` 多线程统计词频
- [ ] 用 `CountDownLatch` 等待 5 个任务全部完成
- [ ] 解释 `volatile` 和 `synchronized` 的区别（可见性 vs 原子性）
- [ ] 用 `ThreadPoolExecutor` 显式构造一个线程池（不要走 Executors）

## 代码示例

→ [`code/week3/thread_pool/`](../../code/week3/thread_pool/)
→ [`code/week3/concurrent_map/`](../../code/week3/concurrent_map/)
