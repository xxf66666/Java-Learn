package thread_basics;

import java.util.concurrent.*;

public class ThreadBasicsDemo {

    public static void main(String[] args) throws Exception {
        wayRunnable();
        wayCallable();
        interruptDemo();
    }

    /** 方式 1：Runnable + 匿名 Lambda */
    static void wayRunnable() throws InterruptedException {
        System.out.println("\n=== Runnable ===");
        Thread t = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                System.out.println(Thread.currentThread().getName() + " tick " + i);
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "worker-1");
        t.start();
        t.join();
        System.out.println("worker-1 完成");
    }

    /** 方式 2：Callable + Future（能拿返回值）*/
    static void wayCallable() throws Exception {
        System.out.println("\n=== Callable ===");
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<Integer> future = pool.submit(() -> {
            Thread.sleep(200);
            return 42;
        });
        System.out.println("结果 = " + future.get());
        pool.shutdown();
    }

    /** 中断协作 */
    static void interruptDemo() throws InterruptedException {
        System.out.println("\n=== Interrupt ===");
        Thread worker = new Thread(() -> {
            int i = 0;
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("workin... " + (++i));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("被中断了，退出");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.start();
        Thread.sleep(350);
        worker.interrupt();
        worker.join();
    }
}
