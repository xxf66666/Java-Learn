package thread_basics;

// java.util.concurrent 提供 ExecutorService / Future 等并发工具
import java.util.concurrent.*;

public class ThreadBasicsDemo {

    // 抛 Exception：本类几个方法分别抛 InterruptedException / ExecutionException
    public static void main(String[] args) throws Exception {
        wayRunnable();
        wayCallable();
        interruptDemo();
    }

    /** 方式 1：Runnable + 匿名 Lambda */
    static void wayRunnable() throws InterruptedException {
        System.out.println("\n=== Runnable ===");

        // new Thread(Runnable, name) 创建线程对象（还没开始跑）
        // Lambda () -> {...} 自动适配 Runnable（无参无返回的函数式接口）
        Thread t = new Thread(() -> {
            // 在新线程里跑：循环 3 次，每次睡 100ms
            for (int i = 0; i < 3; i++) {
                // Thread.currentThread() 返回当前正在跑的线程对象
                // getName() 是线程名
                System.out.println(Thread.currentThread().getName() + " tick " + i);

                try {
                    // sleep 是静态方法，让当前线程暂停指定毫秒
                    // 可能被 interrupt 中断，抛 InterruptedException（checked）
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // 捕到中断后要"恢复中断标志"，让外层逻辑能感知
                    Thread.currentThread().interrupt();
                    return;        // 退出 run 方法
                }
            }
        }, "worker-1");           // 给线程起名 "worker-1"

        // start() 开启新线程并执行 run；不是 run()（那样是同步调用，不开新线程）
        t.start();

        // join() 等待这个线程结束才继续；阻塞当前（main）线程
        t.join();
        System.out.println("worker-1 完成");
    }

    /** 方式 2：Callable + Future（能拿返回值）*/
    static void wayCallable() throws Exception {
        System.out.println("\n=== Callable ===");

        // Executors 是 ExecutorService 的工厂
        // newSingleThreadExecutor 创建只有一个线程的线程池（任务串行执行）
        ExecutorService pool = Executors.newSingleThreadExecutor();

        // submit(Callable) 提交一个**有返回值**的任务
        // Callable 是函数式接口，签名 V call() throws Exception
        // submit 立刻返回 Future，任务在线程池里异步执行
        Future<Integer> future = pool.submit(() -> {
            Thread.sleep(200);
            return 42;          // Callable 可以返回值，也可以 throws
        });

        // future.get() 阻塞当前线程，直到任务完成、拿到结果
        // 如果任务里抛异常，这里会用 ExecutionException 包起来重新抛
        System.out.println("结果 = " + future.get());

        // shutdown 拒绝新任务，已提交的还会跑完
        pool.shutdown();
    }

    /** 中断协作：教科书式的"礼貌停下" */
    static void interruptDemo() throws InterruptedException {
        System.out.println("\n=== Interrupt ===");

        Thread worker = new Thread(() -> {
            int i = 0;
            // isInterrupted() 检查"我是不是被外面 interrupt 了"
            // 这是协作式中断：被中断的线程自己决定何时退出
            while (!Thread.currentThread().isInterrupted()) {
                // 前缀 ++i：先 +1 再用值
                System.out.println("workin... " + (++i));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // sleep 被打断时**会清掉中断标志**，所以这里手动 interrupt 一下保留
                    System.out.println("被中断了，退出");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        worker.start();

        // 主线程睡 350ms 让 worker 跑几轮
        Thread.sleep(350);

        // 给 worker 发中断信号（不强制停，靠 worker 自己响应）
        worker.interrupt();

        // 等 worker 真正退出
        worker.join();
    }
}
