package file_counter;

import java.io.IOException;
// NIO.2 路径相关类
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
// AtomicLong 是线程安全的 long
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * 综合 demo：多线程统计目录下所有 .java 文件的代码行数。
 *
 * 用法（默认统计当前 Java-Learn 仓库）：
 *   java file_counter.FileLineCounter [扫描目录]
 *
 * 涵盖：
 *  - Files.walk 遍历目录
 *  - ThreadPoolExecutor 提交任务
 *  - CompletableFuture / Future 拿结果
 *  - AtomicLong 累计
 *  - try-with-resources 关闭 Stream
 */
public class FileLineCounter {

    public static void main(String[] args) throws Exception {
        // args 是命令行参数；用三元 ? : 给个默认值 "."（当前目录）
        Path root = Path.of(args.length > 0 ? args[0] : ".");

        // 校验：必须是目录
        if (!Files.isDirectory(root)) {
            System.err.println("不是目录: " + root);
            return;
        }

        // 1. 收集所有 .java 文件
        List<Path> javaFiles;

        // Files.walk 返回 Stream<Path>，**递归**遍历目录及子目录
        // 是惰性的，且持有文件描述符，必须 try-with-resources
        try (Stream<Path> stream = Files.walk(root)) {
            javaFiles = stream
                .filter(Files::isRegularFile)                         // 过滤掉目录、符号链接等
                .filter(p -> p.toString().endsWith(".java"))           // 只要 .java
                .toList();                                              // 收集为不可变 List
        }
        System.out.println("找到 " + javaFiles.size() + " 个 .java 文件");

        // 2. 创建线程池
        // Math.min(8, CPU核心数)：最多 8 线程，避免在 CPU 少的机器上创建过多线程
        ExecutorService pool = Executors.newFixedThreadPool(
            Math.min(8, Runtime.getRuntime().availableProcessors()));

        // AtomicLong：多线程下安全累加
        AtomicLong totalLines = new AtomicLong();
        AtomicLong totalCodeLines = new AtomicLong();
        AtomicLong totalBlankLines = new AtomicLong();

        // 计时开始
        long t0 = System.nanoTime();

        // 存所有 Future（Future 表示"未来某时刻能拿到结果"）
        List<Future<long[]>> futures = new ArrayList<>();

        // 给每个文件提交一个统计任务
        for (Path p : javaFiles) {
            // submit(Callable) 提交一个有返回值的任务
            // 这里 Callable 是 Lambda：() -> long[] { ... }，返回 [总行, 代码行, 空行]
            futures.add(pool.submit(() -> countLines(p)));
        }

        // 收集每个任务的结果，累加到原子变量
        for (Future<long[]> f : futures) {
            // get() 阻塞等待该任务完成，拿到结果
            long[] r = f.get();
            // addAndGet 原子加
            totalLines.addAndGet(r[0]);
            totalCodeLines.addAndGet(r[1]);
            totalBlankLines.addAndGet(r[2]);
        }
        pool.shutdown();

        // 计时结束（毫秒）
        long ms = (System.nanoTime() - t0) / 1_000_000;

        // 打印结果
        System.out.printf("%n=== 统计结果 ===%n");
        System.out.printf("总行数:   %d%n", totalLines.get());
        System.out.printf("代码行:   %d%n", totalCodeLines.get());
        System.out.printf("空行:     %d%n", totalBlankLines.get());
        System.out.printf("耗时:     %d ms%n", ms);
    }

    /**
     * 统计单文件的行信息
     * 返回 [总行数, 代码行数, 空行数]
     */
    static long[] countLines(Path file) throws IOException {
        long total = 0, code = 0, blank = 0;

        // 流式读文件，try-with-resources 保证关闭
        try (Stream<String> lines = Files.lines(file)) {
            // iterator() 把流转成迭代器，便于 while 循环
            for (Iterator<String> it = lines.iterator(); it.hasNext(); ) {
                String line = it.next().trim();   // 去掉首尾空白
                total++;
                if (line.isEmpty()) blank++;       // 空白行
                else code++;                       // 有内容算代码（简化版，不区分注释）
            }
        }
        // 返回数组（Java 没有 tuple，常用数组或 record）
        return new long[]{total, code, blank};
    }
}
