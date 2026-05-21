import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Week 1 · Java 语法对照速查（针对 C++ / Python 老兵）
 *
 * 把这份代码完整 debug 一遍，每段后面打断点，观察变量。
 * 看不懂的地方查阅 docs/week1/00_java_for_cpp_python_dev.md
 */
public class SyntaxCheatsheet {

    public static void main(String[] args) {
        section1_primitives();
        section2_strings();
        section3_controlFlow();
        section4_arraysAndCollections();
        section5_referenceVsValue();
        section6_equalsVsDoubleEquals();
        section7_methodOverload();
    }

    // ============ 1. 基本类型 ============
    static void section1_primitives() {
        System.out.println("\n=== 1. 基本类型 ===");

        int i = 10;
        long l = 100_000_000_000L;       // 加 L，否则编译错误（字面量默认 int）
        double d = 3.14;
        float f = 3.14f;                 // float 字面量加 f
        boolean flag = true;              // 不是 bool！
        char ch = '中';                   // char 是 16 位 Unicode，能装一个汉字

        // 自动类型提升（与 C++ 一致）
        long sum = i + l;
        double avg = (i + d) / 2;

        System.out.printf("i=%d, l=%d, d=%.2f, f=%.2f, flag=%b, ch=%c%n",
                          i, l, d, f, flag, ch);
        System.out.println("sum=" + sum + ", avg=" + avg);
    }

    // ============ 2. 字符串 ============
    static void section2_strings() {
        System.out.println("\n=== 2. 字符串 ===");

        String s1 = "hello";
        String s2 = "world";
        String s3 = s1 + " " + s2;           // + 拼接（编译器优化为 StringBuilder）
        String s4 = String.format("%s-%d", s1, 42);
        String s5 = "%s-%d".formatted(s1, 42); // Java 15+，更简洁

        // ⚠️ 循环里拼接：必须用 StringBuilder（否则 O(n²)）
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append("x");
        String s6 = sb.toString();

        System.out.println("s3 = " + s3);
        System.out.println("s4 = " + s4);
        System.out.println("s5 = " + s5);
        System.out.println("s6 = " + s6);
        System.out.println("s1.length() = " + s1.length()); // String.length() 是方法
        System.out.println("s1.toUpperCase() = " + s1.toUpperCase());
    }

    // ============ 3. 控制流 ============
    static void section3_controlFlow() {
        System.out.println("\n=== 3. 控制流 ===");

        int score = 85;
        if (score >= 90) System.out.println("A");
        else if (score >= 80) System.out.println("B");
        else System.out.println("C");

        // 经典 for（C++ 风格）
        int sum = 0;
        for (int i = 1; i <= 10; i++) sum += i;
        System.out.println("1..10 求和 = " + sum);

        // 增强 for（Python for x in xs）
        int[] arr = {1, 2, 3, 4, 5};
        int product = 1;
        for (int n : arr) product *= n;
        System.out.println("阶乘 = " + product);

        // switch（Java 14+ 表达式版，强烈推荐）
        String grade = switch (score / 10) {
            case 10, 9 -> "A";
            case 8 -> "B";
            case 7 -> "C";
            default -> "D";
        };
        System.out.println("grade = " + grade);
    }

    // ============ 4. 数组和集合 ============
    static void section4_arraysAndCollections() {
        System.out.println("\n=== 4. 数组和集合 ===");

        // 原生数组（固定长度）
        int[] arr = new int[5];           // 默认全 0
        int[] arr2 = {1, 2, 3, 4, 5};
        System.out.println("arr2.length = " + arr2.length);  // 数组是 .length（无括号）

        // ArrayList（动态数组，工作中 99% 用这个）
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");
        System.out.println("names.size() = " + names.size());   // 集合是 .size()
        System.out.println("names.get(1) = " + names.get(1));

        // HashMap
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 88);
        System.out.println("Alice 的分数 = " + scores.get("Alice"));

        // 遍历 Map
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
    }

    // ============ 5. 引用语义 ============
    static void section5_referenceVsValue() {
        System.out.println("\n=== 5. 引用语义（Java 易踩坑）===");

        // 基本类型：值传递
        int a = 10;
        int b = a;
        b = 20;
        System.out.println("a=" + a + ", b=" + b);  // a=10, b=20

        // 引用类型：引用传递（指向同一对象）
        int[] arr1 = {1, 2, 3};
        int[] arr2 = arr1;
        arr2[0] = 999;
        System.out.println("arr1[0] = " + arr1[0]); // 999！

        // 字符串特殊：immutable，看起来像值传递
        String s1 = "hello";
        String s2 = s1;
        s2 = "world";                                // 这是 s2 重新指向，不是修改
        System.out.println("s1 = " + s1);            // 还是 hello
    }

    // ============ 6. == vs equals ============
    static void section6_equalsVsDoubleEquals() {
        System.out.println("\n=== 6. == 比较引用，.equals() 比较内容 ===");

        // 字符串常量池
        String a = "hello";
        String b = "hello";
        System.out.println("a == b: " + (a == b));         // true（常量池复用）
        System.out.println("a.equals(b): " + a.equals(b)); // true

        // new 出来的是新对象
        String c = new String("hello");
        String d = new String("hello");
        System.out.println("c == d: " + (c == d));         // false ！
        System.out.println("c.equals(d): " + c.equals(d)); // true

        // Integer 的坑：[-128, 127] 缓存
        Integer x = 100, y = 100;
        Integer p = 200, q = 200;
        System.out.println("Integer 100 == 100: " + (x == y));   // true
        System.out.println("Integer 200 == 200: " + (p == q));   // false ！
        // 规则：对象比较永远用 .equals()
    }

    // ============ 7. 方法重载 ============
    static int add(int a, int b) { return a + b; }
    static double add(double a, double b) { return a + b; }
    static int add(int a, int b, int c) { return a + b + c; }

    static void section7_methodOverload() {
        System.out.println("\n=== 7. 方法重载 ===");
        System.out.println(add(1, 2));         // 调 int 版
        System.out.println(add(1.5, 2.5));     // 调 double 版
        System.out.println(add(1, 2, 3));      // 调三参版
    }
}
