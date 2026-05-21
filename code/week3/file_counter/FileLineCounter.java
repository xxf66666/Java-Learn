package file_counter;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
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
        Path root = Path.of(args.length > 0 ? args[0] : ".");
        if (!Files.isDirectory(root)) {
            System.err.println("不是目录: " + root);
            return;
        }

        // 1. 收集所有 .java 文件
        List<Path> javaFiles;
        try (Stream<Path> stream = Files.walk(root)) {
            javaFiles = stream
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .toList();
        }
        System.out.println("找到 " + javaFiles.size() + " 个 .java 文件");

        // 2. 用线程池并发统计每个文件的行数
        ExecutorService pool = Executors.newFixedThreadPool(
            Math.min(8, Runtime.getRuntime().availableProcessors()));

        AtomicLong totalLines = new AtomicLong();
        AtomicLong totalCodeLines = new AtomicLong();
        AtomicLong totalBlankLines = new AtomicLong();

        long t0 = System.nanoTime();
        List<Future<long[]>> futures = new ArrayList<>();
        for (Path p : javaFiles) {
            futures.add(pool.submit(() -> countLines(p)));
        }
        for (Future<long[]> f : futures) {
            long[] r = f.get();             // [总行, 代码行, 空行]
            totalLines.addAndGet(r[0]);
            totalCodeLines.addAndGet(r[1]);
            totalBlankLines.addAndGet(r[2]);
        }
        pool.shutdown();

        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("%n=== 统计结果 ===%n");
        System.out.printf("总行数:   %d%n", totalLines.get());
        System.out.printf("代码行:   %d%n", totalCodeLines.get());
        System.out.printf("空行:     %d%n", totalBlankLines.get());
        System.out.printf("耗时:     %d ms%n", ms);
    }

    /** 返回 [总行数, 代码行数, 空行数] */
    static long[] countLines(Path file) throws IOException {
        long total = 0, code = 0, blank = 0;
        try (Stream<String> lines = Files.lines(file)) {
            for (Iterator<String> it = lines.iterator(); it.hasNext(); ) {
                String line = it.next().trim();
                total++;
                if (line.isEmpty()) blank++;
                else code++;
            }
        }
        return new long[]{total, code, blank};
    }
}
