package student;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * 学生管理系统 CLI：菜单驱动。
 * 跑起来后输入 1-7 操作。
 */
public class StudentApp {

    private static final Path DATA = Path.of("tmp-week2/students.csv");
    private static final StudentRepository repo = new StudentRepository();

    public static void main(String[] args) throws IOException {
        loadIfExists();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                System.out.print("请输入 (q 退出): ");
                String op = sc.nextLine().trim();
                if (op.equalsIgnoreCase("q")) break;

                try {
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
                    System.err.println("❌ " + e.getMessage());
                }
            }
        }

        save();
        System.out.println("再见！");
    }

    static void printMenu() {
        System.out.println("\n========================");
        System.out.println("1. 添加学生");
        System.out.println("2. 删除学生");
        System.out.println("3. 按学号查找");
        System.out.println("4. 列出全部 (共 " + repo.size() + " 人)");
        System.out.println("5. 按专业筛选");
        System.out.println("6. 各专业人数统计");
        System.out.println("7. 保存到文件");
    }

    static void add(Scanner sc) {
        System.out.print("学号: "); String id = sc.nextLine().trim();
        System.out.print("姓名: "); String name = sc.nextLine().trim();
        System.out.print("年龄: "); int age = Integer.parseInt(sc.nextLine().trim());
        System.out.print("专业: "); String major = sc.nextLine().trim();
        repo.add(new Student(id, name, age, major));
        System.out.println("✅ 已添加");
    }

    static void remove(Scanner sc) {
        System.out.print("要删除的学号: ");
        String id = sc.nextLine().trim();
        if (repo.removeById(id)) System.out.println("✅ 已删除");
        else System.out.println("⚠️ 未找到");
    }

    static void find(Scanner sc) {
        System.out.print("学号: "); String id = sc.nextLine().trim();
        repo.findById(id).ifPresentOrElse(
            s -> System.out.println(s),
            () -> System.out.println("未找到")
        );
    }

    static void listAll() {
        if (repo.size() == 0) { System.out.println("(空)"); return; }
        repo.findAll().forEach(System.out::println);
    }

    static void filterByMajor(Scanner sc) {
        System.out.print("专业: "); String major = sc.nextLine().trim();
        repo.findByMajor(major).forEach(System.out::println);
    }

    static void stats() {
        repo.countByMajor().forEach((m, c) -> System.out.println(m + " -> " + c + " 人"));
    }

    static void save() throws IOException {
        java.nio.file.Files.createDirectories(DATA.getParent());
        repo.saveToCsv(DATA);
        System.out.println("✅ 已保存到 " + DATA);
    }

    static void loadIfExists() throws IOException {
        repo.loadFromCsv(DATA);
        System.out.println("启动加载：共 " + repo.size() + " 人");
    }
}
