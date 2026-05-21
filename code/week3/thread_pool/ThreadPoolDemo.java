package thread_pool;

import java.util.concurrent.*;
// AtomicInteger 是原子整数，多线程下 +1 不会丢
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池演示：
 *  - 用 ThreadPoolExecutor 显式构造（阿里规约推荐）
 *  - 对比 synchronized vs AtomicInteger 的性能
 *  - 用 CountDownLatch 等待全部完成
 */
public class ThreadPoolDemo {

    public static void main(String[] args) throws Exception {
        compareCounter();
        countDownLatchDemo();
    }

    /** 内嵌静态类：演示线程安全的计数器（用 synchronized）*/
    static class SyncCounter {
        private int value = 0;

        // synchronized 关键字：方法整体加锁
        // 等价于：方法体外包一层 synchronized (this) { ... }
        // 同一时刻只允许一个线程进入这个方法
        public synchronized void incr() { value++; }

        // 注意 get 没加锁；学习目的，性能对比够用
        public int get() { return value; }
    }

    /** 对比两种线程安全计数器的性能 */
    static void compareCounter() throws InterruptedException {
        System.out.println("\n=== synchronized vs AtomicInteger ===");

        int threads = 10, perThread = 100_000;

        // 1) synchronized 版本
        SyncCounter sc = new SyncCounter();
        // System.nanoTime 拿当前纳秒时间戳，做精确计时
        long t1 = System.nanoTime();
        // sc::incr 是方法引用 -> 等价于 () -> sc.incr()
        // 它会被适配成 Runnable 传给 runConcurrent
        runConcurrent(threads, perThread, sc::incr);
        // 纳秒转毫秒
        long d1 = (System.nanoTime() - t1) / 1_000_000;
        System.out.println("synchronized: 结果=" + sc.get() + ", 耗时=" + d1 + "ms");

        // 2) AtomicInteger 版本：原子操作，无锁（基于 CPU 的 CAS 指令）
        AtomicInteger ai = new AtomicInteger();
        long t2 = System.nanoTime();
        // ai::incrementAndGet 等价于 () -> ai.incrementAndGet()
        runConcurrent(threads, perThread, ai::incrementAndGet);
        long d2 = (System.nanoTime() - t2) / 1_000_000;
        System.out.println("AtomicInteger: 结果=" + ai.get() + ", 耗时=" + d2 + "ms");
    }

    /**
     * 并发跑指定操作：threads 个线程，每个线程做 perThread 次 op
     */
    static void runConcurrent(int threads, int perThread, Runnable op) throws InterruptedException {
        // ThreadPoolExecutor 七参构造（阿里规约推荐显式构造）：
        //   corePoolSize       核心线程数
        //   maximumPoolSize    最大线程数
        //   keepAliveTime      非核心线程空闲多久回收
        //   unit               时间单位
        //   workQueue          任务队列
        //   threadFactory      创建线程的工厂（命名 / 守护属性）
        //   handler            拒绝策略
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
            threads, threads,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            // ThreadFactory 是函数式接口：Thread newThread(Runnable r)
            r -> {
                Thread t = new Thread(r);
                t.setName("pool-worker");
                return t;
            },
            // 拒绝策略：队列满时调用方自己跑、抛异常、丢弃...
            new ThreadPoolExecutor.AbortPolicy()
        );

        // CountDownLatch(n)：内部计数器从 n 开始
        // 每次 countDown 减 1；await 阻塞直到归零
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                for (int j = 0; j < perThread; j++) op.run();
                // 任务结束让 latch 计数 -1
                latch.countDown();
            });
        }

        // 主线程阻塞，等所有任务跑完
        latch.await();
        pool.shutdown();
    }

    /** CountDownLatch 单独示例：等 3 个任务全部完成 */
    static void countDownLatchDemo() throws InterruptedException {
        System.out.println("\n=== CountDownLatch ===");

        // newFixedThreadPool(3) 创建固定 3 线程的线程池
        ExecutorService pool = Executors.newFixedThreadPool(3);

        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            // 注意：Lambda 引用外部变量必须是"事实上 final"
            // i 在循环里在变，所以拷贝一份到 final 的 id
            int id = i;
            pool.submit(() -> {
                try {
                    // 不同任务睡不同时间，看顺序无关
                    Thread.sleep(200 + id * 100);
                } catch (InterruptedException e) { }
                System.out.println("task " + id + " done");
                latch.countDown();
            });
        }

        latch.await();
        System.out.println("✅ all 3 tasks finished");
        pool.shutdown();
    }
}
