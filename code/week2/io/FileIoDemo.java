package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * 文件 IO 演示：用现代 NIO.2 API (Files + Path)，比老的 FileReader/FileWriter 简洁很多。
 *
 * 运行前确保有 tmp 目录写权限。
 */
public class FileIoDemo {

    public static void main(String[] args) throws IOException {
        Path tmpDir = Path.of("tmp-week2");
        Files.createDirectories(tmpDir);
        Path file = tmpDir.resolve("demo.txt");

        // ====== 写 ======
        Files.writeString(file, "line1\nline2\nline3\n");
        Files.writeString(file, "line4\n", StandardOpenOption.APPEND);

        // ====== 读所有行 ======
        List<String> lines = Files.readAllLines(file);
        System.out.println("readAllLines: " + lines);

        // ====== 读为字符串 ======
        String content = Files.readString(file);
        System.out.println("readString:\n" + content);

        // ====== 流式读（大文件友好）======
        // 注意：Files.lines() 返回的 Stream 持有文件句柄，必须 try-with-resources
        try (Stream<String> stream = Files.lines(file)) {
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
            entries.forEach(System.out::println);
        }

        // ====== 清理 ======
        Files.delete(file);
        Files.delete(tmpDir);
        System.out.println("\n清理完毕");
    }
}
