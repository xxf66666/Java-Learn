package modifiers;

/**
 * Week 1.5 §03 配套示例：static / final / 抽象等修饰符
 */
public class ModifierDemo {

    public static void main(String[] args) {
        staticDemo();
        finalDemo();
        constants();
    }

    // ============ static ============

    /**
     * 演示静态字段是"全类共享"
     */
    static class Counter {
        // static = 类共享一份，所有实例看到同一个值
        static int totalCount = 0;

        // 实例字段：每个对象一份
        int myCount = 0;

        // 静态初始化块：类被加载时只执行一次
        static {
            System.out.println("[Counter 静态初始化块] 类加载");
            totalCount = 0;
        }

        // 实例初始化块：每次 new 时跑（构造器之前）
        {
            System.out.println("[Counter 实例初始化块] new 对象");
            myCount = 0;
        }

        Counter() {
            // 构造器最后执行
            totalCount++;          // 共享变量 +1
            myCount = totalCount;  // 当前实例的"序号"
        }
    }

    static void staticDemo() {
        System.out.println("\n=== static ===");

        // 还没 new 任何对象，类已加载
        // 直接通过类名访问静态字段
        System.out.println("访问 Counter.totalCount 触发类加载...");
        System.out.println("初始 totalCount = " + Counter.totalCount);

        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();

        // 三个对象的 myCount 是各自的；totalCount 是共享的
        System.out.println("c1.myCount = " + c1.myCount);    // 1
        System.out.println("c2.myCount = " + c2.myCount);    // 2
        System.out.println("c3.myCount = " + c3.myCount);    // 3
        System.out.println("总数 = " + Counter.totalCount);   // 3
    }

    // ============ final ============

    /** final 字段：一旦赋值不能再改 */
    static class Point {
        final int x;       // 必须在构造器里赋值
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
            // this.x = 99;   // ❌ 编译错误：final 字段不能改
        }
    }

    static void finalDemo() {
        System.out.println("\n=== final ===");

        Point p = new Point(3, 4);
        System.out.println("p = (" + p.x + ", " + p.y + ")");
        // p.x = 99;   // ❌ 编译错误

        // final 引用 vs final 内容
        final java.util.List<String> list = new java.util.ArrayList<>();
        list.add("a");          // ✅ 内容可改
        list.add("b");
        System.out.println("final list 内容: " + list);
        // list = new ArrayList<>();   // ❌ 不能换引用
    }

    // ============ 常量惯例 ============

    // public static final = 类常量；全大写下划线分隔命名
    public static final int MAX_RETRY = 3;
    public static final String SERVER_HOST = "localhost";
    public static final double PI = 3.14159;

    static void constants() {
        System.out.println("\n=== 常量 ===");
        System.out.println("MAX_RETRY = " + MAX_RETRY);
        System.out.println("SERVER_HOST = " + SERVER_HOST);
        System.out.println("PI = " + PI);
    }
}
