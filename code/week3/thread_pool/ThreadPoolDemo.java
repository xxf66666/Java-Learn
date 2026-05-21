package thread_pool;

import java.util.concurrent.*;
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

    static class SyncCounter {
        private int value = 0;
        public synchronized void incr() { value++; }
        public int get() { return value; }
    }

    static void compareCounter() throws InterruptedException {
        System.out.println("\n=== synchronized vs AtomicInteger ===");
        int threads = 10, perThread = 100_000;

        // synchronized 版本
        SyncCounter sc = new SyncCounter();
        long t1 = System.nanoTime();
        runConcurrent(threads, perThread, sc::incr);
        long d1 = (System.nanoTime() - t1) / 1_000_000;
        System.out.println("synchronized: 结果=" + sc.get() + ", 耗时=" + d1 + "ms");

        // AtomicInteger 版本
        AtomicInteger ai = new AtomicInteger();
        long t2 = System.nanoTime();
        runConcurrent(threads, perThread, ai::incrementAndGet);
        long d2 = (System.nanoTime() - t2) / 1_000_000;
        System.out.println("AtomicInteger: 结果=" + ai.get() + ", 耗时=" + d2 + "ms");
    }

    static void runConcurrent(int threads, int perThread, Runnable op) throws InterruptedException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
            threads, threads,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            r -> {
                Thread t = new Thread(r);
                t.setName("pool-worker");
                return t;
            },
            new ThreadPoolExecutor.AbortPolicy()
        );

        CountDownLatch latch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                for (int j = 0; j < perThread; j++) op.run();
                latch.countDown();
            });
        }
        latch.await();
        pool.shutdown();
    }

    static void countDownLatchDemo() throws InterruptedException {
        System.out.println("\n=== CountDownLatch ===");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            int id = i;
            pool.submit(() -> {
                try { Thread.sleep(200 + id * 100); } catch (InterruptedException e) { }
                System.out.println("task " + id + " done");
                latch.countDown();
            });
        }

        latch.await();
        System.out.println("✅ all 3 tasks finished");
        pool.shutdown();
    }
}
