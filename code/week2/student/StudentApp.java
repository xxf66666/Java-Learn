package student;

import java.io.IOException;
import java.nio.file.Path;
// Scanner 读控制台输入
import java.util.Scanner;

/**
 * 学生管理系统 CLI：菜单驱动。
 * 跑起来后输入 1-7 操作。
 */
public class StudentApp {

    // 数据文件路径（相对工作目录）
    // static final = 类常量，写在外面方便复用、改一次全改
    private static final Path DATA = Path.of("tmp-week2/students.csv");

    // 仓库实例：整个程序共用
    private static final StudentRepository repo = new StudentRepository();

    // main 抛 IOException 给 JVM（学习阶段允许；生产应该 try-catch + 友好提示）
    public static void main(String[] args) throws IOException {
        // 启动时如果有历史文件，先加载进内存
        loadIfExists();

        // try-with-resources：Scanner 实现了 AutoCloseable
        // 出了 try 块 JVM 自动调 sc.close()，不用手动写 finally
        try (Scanner sc = new Scanner(System.in)) {

            // 死循环；通过 break 退出
            while (true) {
                printMenu();
                System.out.print("请输入 (q 退出): ");
                String op = sc.nextLine().trim();

                if (op.equalsIgnoreCase("q")) break;

                // 每次操作都用 try-catch 包住，避免一次输入错误把程序整个挂掉
                try {
                    // switch 表达式 + 多分支
                    // 这里用的是"语句"形式：每个 case -> { ... }，没有返回值
                    switch (op) {
                        case "1" -> add(sc);
                        case "2" -> remove(sc);
                        case "3" -> find(sc);
                        case "4" -> listAll();
                        case "5" -> filterByMajor(sc);
                        case "6" -> stats();
                        case "7" -> save();
                        default -> System.out.println("未识别");
                    }
                } catch (Exception e) {
                    // 捕获所有异常，打印消息但不退出循环
                    System.err.println("❌ " + e.getMessage());
                }
            }
        }

        // 退出前自动保存
        save();
        System.out.println("再见！");
    }

    /** 打印菜单 */
    static void printMenu() {
        System.out.println("\n========================");
        System.out.println("1. 添加学生");
        System.out.println("2. 删除学生");
        System.out.println("3. 按学号查找");
        // 拼接：字符串 + int 自动转 String
        System.out.println("4. 列出全部 (共 " + repo.size() + " 人)");
        System.out.println("5. 按专业筛选");
        System.out.println("6. 各专业人数统计");
        System.out.println("7. 保存到文件");
    }

    /** 添加学生：交互式收集字段 */
    static void add(Scanner sc) {
        System.out.print("学号: "); String id = sc.nextLine().trim();
        System.out.print("姓名: "); String name = sc.nextLine().trim();

        // parseInt 把 "20" 转 int 20；解析失败抛 NumberFormatException
        System.out.print("年龄: "); int age = Integer.parseInt(sc.nextLine().trim());

        System.out.print("专业: "); String major = sc.nextLine().trim();

        // 调用 repo 的 add 方法，重复学号会抛 IllegalArgumentException
        repo.add(new Student(id, name, age, major));
        System.out.println("✅ 已添加");
    }

    /** 删除学生 */
    static void remove(Scanner sc) {
        System.out.print("要删除的学号: ");
        String id = sc.nextLine().trim();
        if (repo.removeById(id)) System.out.println("✅ 已删除");
        else System.out.println("⚠️ 未找到");
    }

    /** 查找单个学生 */
    static void find(Scanner sc) {
        System.out.print("学号: "); String id = sc.nextLine().trim();

        // Optional 的 ifPresentOrElse：
        //   有值时跑第一个 Lambda（Consumer），把值传进来
        //   没值时跑第二个 Lambda（Runnable）
        repo.findById(id).ifPresentOrElse(
            s -> System.out.println(s),       // 找到了：打印（自动调 toString）
            () -> System.out.println("未找到")  // 没找到
        );
    }

    /** 列全部 */
    static void listAll() {
        if (repo.size() == 0) { System.out.println("(空)"); return; }

        // forEach + 方法引用 System.out::println
        // 等价于 forEach(s -> System.out.println(s))
        repo.findAll().forEach(System.out::println);
    }

    /** 按专业筛选 */
    static void filterByMajor(Scanner sc) {
        System.out.print("专业: "); String major = sc.nextLine().trim();
        repo.findByMajor(major).forEach(System.out::println);
    }

    /** 各专业人数统计 */
    static void stats() {
        // Map.forEach((k, v) -> ...)：遍历每个键值对
        repo.countByMajor().forEach((m, c) -> System.out.println(m + " -> " + c + " 人"));
    }

    /** 保存到磁盘 */
    static void save() throws IOException {
        // getParent() 拿到 DATA 路径的父目录（tmp-week2/）
        // createDirectories 递归创建（已存在不报错）
        java.nio.file.Files.createDirectories(DATA.getParent());
        repo.saveToCsv(DATA);
        System.out.println("✅ 已保存到 " + DATA);
    }

    /** 启动时如果文件存在，先加载 */
    static void loadIfExists() throws IOException {
        repo.loadFromCsv(DATA);
        System.out.println("启动加载：共 " + repo.size() + " 人");
    }
}
