# Week 3 §01 · 线程基础

> 目标：能创建线程、能让线程协作、能从线程拿到返回值。

---

## 1. 三种创建线程的方式

### 方式 1：继承 `Thread`（不推荐，浪费"单继承"额度）

```java
class MyThread extends Thread {
    @Override public void run() {
        System.out.println("hello from " + Thread.currentThread().getName());
    }
}
new MyThread().start();
```

### 方式 2：实现 `Runnable`（常用）

```java
Runnable task = () -> System.out.println("hello");
new Thread(task).start();
```

### 方式 3：实现 `Callable<T>` + `Future`（能返回结果 / 抛异常）

```java
import java.util.concurrent.*;

ExecutorService pool = Executors.newSingleThreadExecutor();
Future<Integer> future = pool.submit(() -> {
    Thread.sleep(1000);
    return 42;
});
Integer result = future.get();      // 阻塞等结果
pool.shutdown();
```

| | Runnable | Callable |
|--|----------|----------|
| 返回值 | 无 | 有 |
| 抛 checked 异常 | 不能 | 能 |
| 拿结果 | 无法 | `Future.get()` |

---

## 2. 线程的关键 API

```java
Thread t = new Thread(() -> { ... });
t.start();             // 启动（注意不是 t.run() ！直接调 run() 是同步调用，不开新线程）
t.join();              // 等待这个线程结束
t.interrupt();         // 请求中断（不强制停，让线程自己响应）
t.isAlive();
t.getName();
t.setDaemon(true);     // 守护线程，主线程结束它自动结束

Thread.sleep(1000);    // 当前线程睡 1 秒（要 catch InterruptedException）
Thread.currentThread();
```

### `start()` vs `run()`

```java
new Thread(task).start();    // ✅ 开新线程
new Thread(task).run();      // ❌ 在当前线程同步执行，没有并行
```

---

## 3. 线程的生命周期

```
NEW
 ↓ start()
RUNNABLE  ←——————→  TIMED_WAITING (sleep / join(ms) / wait(ms))
 ↓ 等锁
BLOCKED
 ↓ wait() / join()
WAITING
 ↓ 任务结束
TERMINATED
```

实操中不必背状态机，知道 sleep 阻塞、join 阻塞、wait 阻塞这几个常见动作就够。

---

## 4. 中断：让线程"礼貌地停下"

Java 没有 `Thread.stop()`（已弃用，会破坏数据一致性）。停一个线程的标准做法：

```java
Thread worker = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        // do work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();    // 重新设置中断标志
            break;
        }
    }
    System.out.println("worker 退出");
});

worker.start();
Thread.sleep(500);
worker.interrupt();    // 请求停止
worker.join();
```

---

## 5. 并发问题：先看到坑

```java
public class Counter {
    private int value = 0;
    public void incr() { value++; }
    public int get() { return value; }
}

// 10 个线程，每个 incr 10000 次，期望 100000
Counter c = new Counter();
ExecutorService pool = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) {
    pool.submit(() -> {
        for (int j = 0; j < 10000; j++) c.incr();
    });
}
pool.shutdown();
pool.awaitTermination(10, TimeUnit.SECONDS);
System.out.println(c.get());    // 可能是 87000 / 93000 等，几乎不会是 100000
```

**为什么**：`value++` 不是原子的，等价于 `读 → +1 → 写`，多线程交错时丢更新。

**解决方案见 [`02_thread_pool_sync.md`](02_thread_pool_sync.md)**：
1. `synchronized incr()`
2. 用 `AtomicInteger`
3. 用 `ReentrantLock`

---

## 6. 自查

- [ ] 用 Lambda 启动一个线程，等它结束再继续主线程
- [ ] 用 `Callable + Future` 让一个线程返回结果
- [ ] 写一个 "while !isInterrupted" 循环，被中断时能正确退出
- [ ] 复现 `Counter` 的并发 bug（看到不是 100000 的输出）

## 代码示例

→ [`code/week3/thread_basics/`](../../code/week3/thread_basics/)
