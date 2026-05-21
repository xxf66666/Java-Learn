package com.learning.contact;

// HikariCP 高性能连接池
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
        // ---- 配置连接池 ----
        // HikariConfig 是配置对象，链式 set 各项参数
        HikariConfig cfg = new HikariConfig();
        // JDBC URL 格式：jdbc:mysql://host:port/dbname?查询参数
        // serverTimezone 让 MySQL 知道时区
        // useSSL=false 本地学习不用 SSL
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306/learning?serverTimezone=Asia/Shanghai&useSSL=false");
        cfg.setUsername("root");
        cfg.setPassword("root");
        // 最大连接数：连接池里最多 5 个连接，超过的请求等
        cfg.setMaximumPoolSize(5);

        // try-with-resources：HikariDataSource 实现 AutoCloseable
        // 出 try 块时自动关闭连接池
        try (HikariDataSource ds = new HikariDataSource(cfg)) {
            // 把 DataSource 注入 DAO
            ContactDao dao = new ContactDao(ds);

            // 启动 CLI 循环
            new Cli(dao).run();
        }
    }

    // 内部静态类：CLI 交互逻辑
    // static 表示不依赖外部类实例
    static class Cli {

        private final ContactDao dao;

        // 构造器接收 DAO
        Cli(ContactDao dao) { this.dao = dao; }

        // 主循环
        void run() throws Exception {

            // Scanner 也是资源，要关
            try (Scanner sc = new Scanner(System.in)) {
                while (true) {
                    menu();
                    System.out.print("> ");
                    String op = sc.nextLine().trim();

                    // 输入 q 退出
                    if ("q".equalsIgnoreCase(op)) break;

                    // 单次操作出错只打日志，不挂掉
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
                        // 第二个参数是异常对象，logger 会自动打印堆栈
                        log.error("操作失败", e);
                    }
                }
            }
        }

        void menu() {
            System.out.println("\n1.添加 2.列表 3.按 ID 查 4.按名搜 5.改 6.删 q.退出");
        }

        void add(Scanner sc) throws Exception {
            // 多个变量同行声明 + 读取
            System.out.print("姓名: "); String n = sc.nextLine().trim();
            System.out.print("手机: "); String p = sc.nextLine().trim();
            System.out.print("邮箱: "); String e = sc.nextLine().trim();

            // 调 DAO 插入；自增 id 直接返回
            Long id = dao.insert(new Contact(n, p, e));
            System.out.println("✅ 添加成功 id=" + id);
        }

        void list() throws Exception {
            // 拿全部
            var all = dao.findAll();       // var 让编译器推断类型（Java 10+）
            if (all.isEmpty()) System.out.println("(空)");
            // forEach + 方法引用 = 简洁打印
            else all.forEach(System.out::println);
        }

        void findById(Scanner sc) throws Exception {
            System.out.print("ID: "); long id = Long.parseLong(sc.nextLine().trim());

            // Optional 链式处理：找到打印，没找到提示
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

            // 先查出现状
            var opt = dao.findById(id);
            if (opt.isEmpty()) { System.out.println("未找到"); return; }

            Contact c = opt.get();

            // 允许用户回车保持原值（增量更新）
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
