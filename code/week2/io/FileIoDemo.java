package io;

// IOException 是 checked 异常，做 IO 几乎都会遇到
import java.io.IOException;
// Files 是 NIO.2 提供的静态工具类，几乎封装了所有文件操作
import java.nio.file.Files;
// Path 表示一个文件/目录的路径（替代老的 File 类）
import java.nio.file.Path;
// StandardOpenOption 提供"追加" / "覆盖" 等打开模式枚举
import java.nio.file.StandardOpenOption;
import java.util.List;
// Stream 是 Java 8 引入的"流"概念，支持链式处理
import java.util.stream.Stream;

/**
 * 文件 IO 演示：用现代 NIO.2 API (Files + Path)，比老的 FileReader/FileWriter 简洁很多。
 *
 * 运行前确保有 tmp 目录写权限。
 */
public class FileIoDemo {

    // throws IOException 声明：这个方法可能抛 IOException
    // checked 异常必须 catch 或者 throws 往上抛；main 这里偷懒往 JVM 抛
    public static void main(String[] args) throws IOException {

        // Path.of(...) 创建相对路径（不立刻访问磁盘）
        Path tmpDir = Path.of("tmp-week2");

        // 递归创建目录；已存在不会报错
        Files.createDirectories(tmpDir);

        // resolve("xxx") 在当前路径下拼接子路径，等价于 / 操作
        Path file = tmpDir.resolve("demo.txt");

        // ====== 写 ======
        // writeString 一次写入整个字符串；默认是"覆盖写"
        // 内部自动处理打开 / 写 / 关闭（不需要 try-with-resources）
        Files.writeString(file, "line1\nline2\nline3\n");

        // 追加写：通过 StandardOpenOption.APPEND 传选项
        Files.writeString(file, "line4\n", StandardOpenOption.APPEND);

        // ====== 读所有行 ======
        // readAllLines 一次性把整个文件读到 List<String>，每个元素一行
        // 大文件不要这样读（占内存），用 Files.lines 流式读
        List<String> lines = Files.readAllLines(file);
        System.out.println("readAllLines: " + lines);

        // ====== 读为字符串 ======
        // readString 一次读整个文件为一个 String（JDK 11+）
        String content = Files.readString(file);
        System.out.println("readString:\n" + content);

        // ====== 流式读（大文件友好）======
        // Files.lines() 返回 Stream<String>，懒求值，按需读
        // ⚠️ 这个 Stream **持有文件句柄**，必须用 try-with-resources 自动关闭
        // try ( ... ) 里声明的资源，try 块结束会自动调 close()
        try (Stream<String> stream = Files.lines(file)) {
            // filter 过滤，count 终止操作返回数量
            long count = stream.filter(line -> line.startsWith("line"))
                               .count();
            System.out.println("匹配 line 开头的行数 = " + count);
        }

        // ====== 文件信息 ======
        System.out.println("文件大小 = " + Files.size(file) + " bytes");
        System.out.println("存在? " + Files.exists(file));

        // ====== 列目录 ======
        System.out.println("\n--- tmp 目录下文件 ---");
        try (Stream<Path> entries = Files.list(tmpDir)) {
            // Stream::forEach 是 Consumer，每个元素跑一遍
            // System.out::println 是方法引用，等价于 e -> System.out.println(e)
            entries.forEach(System.out::println);
        }

        // ====== 清理 ======
        Files.delete(file);
        Files.delete(tmpDir);
        System.out.println("\n清理完毕");
    }
}
