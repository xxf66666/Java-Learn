// import 把别的包里的类引进来，写在 package 声明之后、class 之前
// java.util 是 JDK 自带的工具包，集合类都在这里
import java.util.ArrayList;     // 动态数组
import java.util.HashMap;       // 哈希字典
import java.util.List;          // List 接口
import java.util.Map;           // Map 接口

/**
 * Week 1 · Java 语法对照速查（Python 视角）
 *
 * 把这份代码完整 debug 一遍，每段后面打断点，观察变量。
 * 看不懂的地方查阅 docs/week1/00_java_from_python.md
 */
public class SyntaxCheatsheet {

    // main 是程序入口，所有演示都从这里依次调用
    public static void main(String[] args) {
        // 一段一段 demo，每个函数演示一个主题
        section1_primitives();              // 基本类型
        section2_strings();                 // 字符串
        section3_controlFlow();             // 控制流
        section4_arraysAndCollections();    // 数组和集合
        section5_referenceVsValue();        // 引用 vs 值
        section6_equalsVsDoubleEquals();    // == 和 equals
        section7_methodOverload();          // 方法重载
    }

    // ============ 1. 基本类型 ============
    // static 方法：用类名直接调，不需要先 new 对象
    // void：没有返回值
    static void section1_primitives() {
        // \n 是换行符；"===" 是普通字符串
        System.out.println("\n=== 1. 基本类型 ===");

        // int = 32 位整数，最常用的整数类型
        int i = 10;

        // long = 64 位整数。字面量后面加 L 表示这是 long 类型
        // 数字中的 _ 是分隔符（Java 7+），仅为可读性，编译时会被忽略
        long l = 100_000_000_000L;

        // double = 64 位浮点数（默认浮点类型）
        double d = 3.14;

        // float = 32 位浮点数。字面量后面加 f 才是 float
        // 不加 f 会被当作 double，赋值给 float 编译报错
        float f = 3.14f;

        // boolean = 布尔类型，只有 true / false 两个值
        // 注意名字叫 boolean，不是 bool
        boolean flag = true;

        // char = 16 位 Unicode 字符。**单引号**包一个字符
        // 双引号 "中" 是字符串 String，不是 char
        char ch = '中';

        // 自动类型提升：i 是 int，l 是 long，i + l 自动变 long
        long sum = i + l;

        // 整数和浮点混算时整数会先转 double
        // (i + d) 是 double；除以 int 2 时 2 自动变 double
        double avg = (i + d) / 2;

        // printf：格式化输出。%d 整数、%f 浮点、%b 布尔、%c 字符
        // %.2f 表示保留 2 位小数；%n 是跨平台换行
        System.out.printf("i=%d, l=%d, d=%.2f, f=%.2f, flag=%b, ch=%c%n",
                          i, l, d, f, flag, ch);
        System.out.println("sum=" + sum + ", avg=" + avg);
    }

    // ============ 2. 字符串 ============
    static void section2_strings() {
        System.out.println("\n=== 2. 字符串 ===");

        // String 是引用类型（首字母大写）；字符串字面量用双引号
        String s1 = "hello";
        String s2 = "world";

        // + 拼接字符串。编译器实际把它优化成 StringBuilder
        String s3 = s1 + " " + s2;

        // String.format 是静态方法，类似 Python 的 % 格式化
        //   %s 字符串，%d 整数
        String s4 = String.format("%s-%d", s1, 42);

        // Java 15+ 提供的实例方法 "格式串".formatted(参数)
        // 写起来比 String.format() 更短
        String s5 = "%s-%d".formatted(s1, 42);

        // ⚠️ 循环里拼字符串必须用 StringBuilder
        // 否则每次 + 都创建一个新 String 对象，O(n²) 复杂度
        StringBuilder sb = new StringBuilder();                  // new 关键字创建对象
        for (int i = 0; i < 5; i++) sb.append("x");              // append 追加内容
        String s6 = sb.toString();                                // 最后转回 String

        System.out.println("s3 = " + s3);
        System.out.println("s4 = " + s4);
        System.out.println("s5 = " + s5);
        System.out.println("s6 = " + s6);

        // String 的常用方法：
        //   length()      字符串长度（带括号，方法调用）
        //   toUpperCase() 转大写
        // 注意数组的长度是 length（无括号），但字符串和集合是 length() / size()
        System.out.println("s1.length() = " + s1.length());
        System.out.println("s1.toUpperCase() = " + s1.toUpperCase());
    }

    // ============ 3. 控制流 ============
    static void section3_controlFlow() {
        System.out.println("\n=== 3. 控制流 ===");

        int score = 85;

        // if-else if-else：条件必须是 boolean
        // Java 不像 Python 那样能写 `if (list)`（非空判断），必须明确写 boolean 表达式
        if (score >= 90) System.out.println("A");
        else if (score >= 80) System.out.println("B");
        else System.out.println("C");

        // 经典 for：i 从 1 到 10 累加
        int sum = 0;
        for (int i = 1; i <= 10; i++) sum += i;     // sum += i 等价于 sum = sum + i
        System.out.println("1..10 求和 = " + sum);

        // 数组字面量：用 { } 直接初始化
        // 类型 int[]：int 类型的数组
        int[] arr = {1, 2, 3, 4, 5};

        // 增强 for（for-each）：取数组每个元素，类似 Python 的 for x in xs
        // 不需要管索引，只能从前往后顺序读
        int product = 1;
        for (int n : arr) product *= n;
        System.out.println("阶乘 = " + product);

        // switch 表达式（Java 14+，比传统 switch 更简洁，无 break）
        //   case 10, 9 -> "A"   多个值可以并列
        //   default 是兜底分支，必须写否则编译报错（除非所有情况都列了）
        // 整个 switch 是一个表达式，可以直接赋值给 grade
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

        // 原生数组（固定长度，性能高，但 API 少）
        // new int[5] 创建长度为 5 的数组，元素默认值是 0
        int[] arr = new int[5];

        // 用 { } 初始化数组
        int[] arr2 = {1, 2, 3, 4, 5};
        // 数组的长度是 length（属性，无括号）
        System.out.println("arr2.length = " + arr2.length);

        // ArrayList = 动态数组，工作中 99% 用这个
        // <String> 是泛型参数：告诉编译器这个 List 只能装 String
        // 右侧 new ArrayList<>() 用菱形语法，编译器自动推断 String
        List<String> names = new ArrayList<>();
        names.add("Alice");                      // 末尾追加
        names.add("Bob");
        names.add("Charlie");

        // 集合的长度是 size()（方法，有括号），不是 length
        System.out.println("names.size() = " + names.size());

        // get(index) 按下标取元素
        System.out.println("names.get(1) = " + names.get(1));

        // HashMap = 哈希字典（无序）
        // 泛型有两个参数 <K, V>：K 是 key 的类型，V 是 value 的类型
        Map<String, Integer> scores = new HashMap<>();

        // put(key, value) 写入
        // Java 的 Map 不能装基本类型 int，必须用包装类型 Integer
        // 这里 95 是 int，会被自动装箱（auto-boxing）成 Integer
        scores.put("Alice", 95);
        scores.put("Bob", 88);

        // get(key) 取值；找不到返回 null
        System.out.println("Alice 的分数 = " + scores.get("Alice"));

        // 遍历 Map：entrySet() 返回每个 key-value 对
        // Map.Entry<K, V> 是嵌套类型，表示一个 key-value 对
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            // e.getKey() / e.getValue() 取 key 和 value
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
    }

    // ============ 5. 引用语义 ============
    static void section5_referenceVsValue() {
        System.out.println("\n=== 5. 引用语义（Java 易踩坑）===");

        // 基本类型：值传递（拷贝一份新数据）
        int a = 10;
        int b = a;          // b 拿到的是 a 的值的副本
        b = 20;             // 改 b 不影响 a
        System.out.println("a=" + a + ", b=" + b);   // a=10, b=20

        // 引用类型：引用传递（两个名字指向同一个对象）
        int[] arr1 = {1, 2, 3};
        int[] arr2 = arr1;       // arr2 和 arr1 指向**同一**数组（没拷贝）
        arr2[0] = 999;            // 改 arr2 等于改 arr1
        System.out.println("arr1[0] = " + arr1[0]); // 999

        // String 特殊：immutable（不可变）
        // 看起来像值传递，实际是 "改名字" 而不是 "改内容"
        String s1 = "hello";
        String s2 = s1;          // s2 指向 "hello"
        s2 = "world";             // s2 指向**新的** "world"，并不修改 "hello"
        System.out.println("s1 = " + s1);     // 还是 hello
    }

    // ============ 6. == vs equals ============
    static void section6_equalsVsDoubleEquals() {
        System.out.println("\n=== 6. == 比较引用，.equals() 比较内容 ===");

        // 字符串字面量在 "常量池" 里共享，所以两个 "hello" 是同一个对象
        String a = "hello";
        String b = "hello";
        System.out.println("a == b: " + (a == b));         // true（巧合，因为常量池）
        System.out.println("a.equals(b): " + a.equals(b)); // true（内容相同）

        // new String() 强制创建新对象，不走常量池
        String c = new String("hello");
        String d = new String("hello");
        System.out.println("c == d: " + (c == d));         // false（不同对象）
        System.out.println("c.equals(d): " + c.equals(d)); // true（内容相同）

        // Integer 缓存的坑：JVM 对 [-128, 127] 范围内的 Integer 做了缓存
        Integer x = 100, y = 100;        // 在缓存范围内：x 和 y 指向同一对象
        Integer p = 200, q = 200;        // 不在缓存范围：p 和 q 是不同对象
        System.out.println("Integer 100 == 100: " + (x == y));   // true（缓存命中）
        System.out.println("Integer 200 == 200: " + (p == q));   // false

        // 铁律：对象比较永远用 .equals()，不要用 ==
    }

    // ============ 7. 方法重载 ============
    // "方法重载" = 同名方法，不同参数列表
    // Java 通过参数类型和个数找到正确的方法
    // 因为没有 Python 的默认参数 / 关键字参数，重载用来代替它们

    // 第 1 个：两个 int 相加
    static int add(int a, int b) { return a + b; }

    // 第 2 个：两个 double 相加（参数类型不同，构成重载）
    static double add(double a, double b) { return a + b; }

    // 第 3 个：三个 int 相加（参数个数不同，构成重载）
    static int add(int a, int b, int c) { return a + b + c; }

    static void section7_methodOverload() {
        System.out.println("\n=== 7. 方法重载 ===");
        // 调用时编译器看实参类型自动选哪个版本
        System.out.println(add(1, 2));         // int 版
        System.out.println(add(1.5, 2.5));     // double 版
        System.out.println(add(1, 2, 3));      // 三参版
    }
}
