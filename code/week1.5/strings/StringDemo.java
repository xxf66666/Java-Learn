package strings;

/**
 * Week 1.5 §02 配套示例：字符串深入
 */
public class StringDemo {

    public static void main(String[] args) {
        immutability();
        constantPool();
        builderPerf();
        commonMethods();
        textBlock();
        nullSafeCompare();
    }

    /** String 不可变 */
    static void immutability() {
        System.out.println("\n=== 不可变 ===");

        String s = "hello";
        // toUpperCase 返回新对象，s 本身不变
        s.toUpperCase();
        System.out.println("s = " + s);                  // hello

        // 接住返回值
        String upper = s.toUpperCase();
        System.out.println("upper = " + upper);          // HELLO
    }

    /** 字符串常量池 */
    static void constantPool() {
        System.out.println("\n=== 字符串常量池 ===");

        // 字面量进入常量池，相同字面量复用同一对象
        String a = "hello";
        String b = "hello";
        // == 比较引用
        System.out.println("\"hello\" == \"hello\": " + (a == b));     // true

        // new String 强制新建对象
        String c = new String("hello");
        System.out.println("a == new String: " + (a == c));           // false
        System.out.println("a.equals(new String): " + a.equals(c));    // true

        // 铁律：永远 .equals 比较
    }

    /** StringBuilder 性能演示 */
    static void builderPerf() {
        System.out.println("\n=== StringBuilder 性能 ===");

        int n = 50_000;     // 数量大才能看出差距

        // ❌ + 拼接：循环里是 O(n²)
        long t1 = System.currentTimeMillis();
        String s = "";
        for (int i = 0; i < n; i++) s = s + i;
        long d1 = System.currentTimeMillis() - t1;
        System.out.println("+ 拼接 " + n + " 次: " + d1 + "ms 长度=" + s.length());

        // ✅ StringBuilder：O(n)
        long t2 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(i);
        String s2 = sb.toString();
        long d2 = System.currentTimeMillis() - t2;
        System.out.println("StringBuilder " + n + " 次: " + d2 + "ms 长度=" + s2.length());
    }

    /** 常用方法 */
    static void commonMethods() {
        System.out.println("\n=== 常用方法 ===");

        String s = "Hello, World!";

        // 长度（方法不是属性）
        System.out.println("length = " + s.length());

        // 大小写
        System.out.println("toUpperCase = " + s.toUpperCase());

        // 查找
        System.out.println("indexOf(World) = " + s.indexOf("World"));
        System.out.println("contains(World) = " + s.contains("World"));

        // 截取：[start, end)
        System.out.println("substring(7) = " + s.substring(7));
        System.out.println("substring(7, 12) = " + s.substring(7, 12));

        // 替换
        System.out.println("replace = " + s.replace("World", "Java"));

        // 切分（参数是正则）
        String csv = "a,b,,c";
        String[] parts = csv.split(",");
        System.out.println("split: " + java.util.Arrays.toString(parts));

        // 拼接
        String joined = String.join("-", "a", "b", "c");
        System.out.println("join: " + joined);

        // 去空白
        System.out.println("trim = [" + "  hello  ".trim() + "]");

        // 空判断
        System.out.println("\"\".isEmpty = " + "".isEmpty());
        System.out.println("\"  \".isEmpty = " + "  ".isEmpty());      // false
        System.out.println("\"  \".isBlank = " + "  ".isBlank());      // true (Java 11+)

        // 数字 ↔ 字符串
        System.out.println("valueOf 42 = " + String.valueOf(42));
        System.out.println("parseInt = " + Integer.parseInt("42"));

        // formatted（Java 15+）
        System.out.println("Hello, %s! You are %d.".formatted("Alice", 20));
    }

    /** 文本块（Java 15+） */
    static void textBlock() {
        System.out.println("\n=== 文本块 ===");

        // """ 开始，必须另起一行；缩进自动处理
        String json = """
                {
                  "name": "Alice",
                  "age": 20
                }
                """;
        System.out.println(json);

        // 配合 formatted 占位
        String html = """
                <h1>%s</h1>
                <p>%s</p>
                """.formatted("标题", "内容");
        System.out.println(html);
    }

    /** null 安全的字符串比较：字面量在前 */
    static void nullSafeCompare() {
        System.out.println("\n=== null 安全比较 ===");

        String s = null;

        // ❌ s.equals(...) 当 s 为 null 时抛 NPE
        try {
            if (s.equals("hello")) System.out.println("匹配");
        } catch (NullPointerException e) {
            System.out.println("NPE: " + e);
        }

        // ✅ 字面量在前：因为字面量保证非 null，equals 内部会判 other 是 null 返回 false
        if ("hello".equals(s)) {
            System.out.println("匹配");
        } else {
            System.out.println("不匹配（s 是 null 也不会 NPE）");
        }
    }
}
