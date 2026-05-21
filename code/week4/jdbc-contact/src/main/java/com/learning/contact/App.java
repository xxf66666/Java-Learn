package com.learning.contact;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * 通讯录 CLI：JDBC + MySQL + HikariCP 综合示例。
 *
 * 跑之前确保：
 *   1. MySQL 启动（docker run mysql:8 ...）
 *   2. 跑过 scripts/contact.sql 建表
 *   3. 数据库连接信息正确（默认 root/root@localhost:3306/learning）
 */
public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306/learning?serverTimezone=Asia/Shanghai&useSSL=false");
        cfg.setUsername("root");
        cfg.setPassword("root");
        cfg.setMaximumPoolSize(5);

        try (HikariDataSource ds = new HikariDataSource(cfg)) {
            ContactDao dao = new ContactDao(ds);
            new Cli(dao).run();
        }
    }

    static class Cli {
        private final ContactDao dao;
        Cli(ContactDao dao) { this.dao = dao; }

        void run() throws Exception {
            try (Scanner sc = new Scanner(System.in)) {
                while (true) {
                    menu();
                    System.out.print("> ");
                    String op = sc.nextLine().trim();
                    if ("q".equalsIgnoreCase(op)) break;
                    try {
                        switch (op) {
                            case "1" -> add(sc);
                            case "2" -> list();
                            case "3" -> findById(sc);
                            case "4" -> search(sc);
                            case "5" -> update(sc);
                            case "6" -> delete(sc);
                            default -> System.out.println("无效");
                        }
                    } catch (Exception e) {
                        log.error("操作失败", e);
                    }
                }
            }
        }

        void menu() {
            System.out.println("\n1.添加 2.列表 3.按 ID 查 4.按名搜 5.改 6.删 q.退出");
        }

        void add(Scanner sc) throws Exception {
            System.out.print("姓名: "); String n = sc.nextLine().trim();
            System.out.print("手机: "); String p = sc.nextLine().trim();
            System.out.print("邮箱: "); String e = sc.nextLine().trim();
            Long id = dao.insert(new Contact(n, p, e));
            System.out.println("✅ 添加成功 id=" + id);
        }

        void list() throws Exception {
            var all = dao.findAll();
            if (all.isEmpty()) System.out.println("(空)");
            else all.forEach(System.out::println);
        }

        void findById(Scanner sc) throws Exception {
            System.out.print("ID: "); long id = Long.parseLong(sc.nextLine().trim());
            dao.findById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("未找到"));
        }

        void search(Scanner sc) throws Exception {
            System.out.print("姓名包含: "); String kw = sc.nextLine().trim();
            dao.findByName(kw).forEach(System.out::println);
        }

        void update(Scanner sc) throws Exception {
            System.out.print("要改的 ID: "); long id = Long.parseLong(sc.nextLine().trim());
            var opt = dao.findById(id);
            if (opt.isEmpty()) { System.out.println("未找到"); return; }
            Contact c = opt.get();
            System.out.print("新姓名 (回车保持 " + c.getName() + "): ");
            String n = sc.nextLine().trim();
            if (!n.isEmpty()) c.setName(n);
            System.out.print("新手机 (回车保持 " + c.getPhone() + "): ");
            String p = sc.nextLine().trim();
            if (!p.isEmpty()) c.setPhone(p);
            System.out.print("新邮箱 (回车保持 " + c.getEmail() + "): ");
            String e = sc.nextLine().trim();
            if (!e.isEmpty()) c.setEmail(e);
            dao.update(c);
            System.out.println("✅ 已更新");
        }

        void delete(Scanner sc) throws Exception {
            System.out.print("要删的 ID: "); long id = Long.parseLong(sc.nextLine().trim());
            if (dao.delete(id)) System.out.println("✅ 已删除");
            else System.out.println("未找到");
        }
    }
}
