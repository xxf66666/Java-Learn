package s06_primitives_strings;

import java.math.BigDecimal;

/**
 * §06 配套示例：基本类型 + 字符串
 */
public class PrimitiveStringDemo {

    public static void main(String[] args) {
        integerCache();
        floatPrecision();
        overflow();
        stringEquals();
        builderPerf();
        commonMethods();
        textBlock();
    }

    /** Integer 缓存的坑 */
    static void integerCache() {
        System.out.println("\n=== Integer 缓存 ===");

        // [-128, 127] 范围内，JVM 缓存：相同值复用同一对象
        Integer a = 100;
        Integer b = 100;
        System.out.println("100 == 100: " + (a == b));       // true

        // 超出范围：每次都是新对象
        Integer c = 200;
        Integer d = 200;
        System.out.println("200 == 200: " + (c == d));       // false ！

        // 永远用 .equals
        System.out.println("200.equals(200): " + c.equals(d)); // true

        // 基本类型 int 没这个坑
        int x = 200, y = 200;
        System.out.println("int 200 == 200: " + (x == y));    // true
    }

    /** 浮点不精确 */
    static void floatPrecision() {
        System.out.println("\n=== 浮点精度 ===");

        double a = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 = " + a);    // 0.30000000000000004

        // 算钱必须用 BigDecimal
        BigDecimal x = new BigDecimal("0.1");      // ⚠️ 用 String 构造
        BigDecimal y = new BigDecimal("0.2");
        BigDecimal sum = x.add(y);
        System.out.println("BigDecimal 0.1 + 0.2 = " + sum);  // 精确 0.3

        BigDecimal price = new BigDecimal("19.99");
        BigDecimal total = price.multiply(new BigDecimal("3"));
        System.out.println("19.99 × 3 = " + total);             // 59.97
    }

    /** 整数溢出 */
    static void overflow() {
        System.out.println("\n=== 整数溢出 ===");

        int max = Integer.MAX_VALUE;
        // max + 1 在 int 范围内溢出 → 绕回最小值
        int wrap = max + 1;
        System.out.println("MAX_VALUE + 1 = " + wrap);   // -2147483648

        // 错误写法：先算溢出再转 long
        long bad = Integer.MAX_VALUE * 2;
        System.out.println("错误: " + bad);              // -2

        // 正确写法：先转 long 再算
        long good = (long) Integer.MAX_VALUE * 2;
        System.out.println("正确: " + good);             // 4294967294
    }

    /** == vs equals + null 安全比较 */
    static void stringEquals() {
        System.out.println("\n=== 字符串比较 ===");

        // 字面量进常量池，相同字面量复用同一对象
        String a = "hello";
        String b = "hello";
        System.out.println("\"hello\" == \"hello\": " + (a == b));   // true

        // new 强制新建对象
        String c = new String("hello");
        System.out.println("a == new String: " + (a == c));         // false
        System.out.println("a.equals(c): " + a.equals(c));           // true

        // null 安全比较：字面量在前
        String s = null;
        // s.equals("abc")        // 会 NPE
        boolean eq = "abc".equals(s);     // ✅ 安全
        System.out.println("\"abc\".equals(null): " + eq);
    }

    /** StringBuilder 性能 */
    static void builderPerf() {
        System.out.println("\n=== StringBuilder vs + ===");

        int n = 50_000;

        // 慢：+ 拼接
        long t1 = System.currentTimeMillis();
        String s = "";
        for (int i = 0; i < n; i++) s = s + i;
        long d1 = System.currentTimeMillis() - t1;
        System.out.println("+ 拼接 " + n + " 次: " + d1 + "ms");

        // 快：StringBuilder
        long t2 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(i);
        long d2 = System.currentTimeMillis() - t2;
        System.out.println("StringBuilder " + n + " 次: " + d2 + "ms");
    }

    /** 常用 String 方法 */
    static void commonMethods() {
        System.out.println("\n=== String 常用方法 ===");

        String s = "Hello, World!";
        System.out.println("length = " + s.length());
        System.out.println("upper = " + s.toUpperCase());
        System.out.println("indexOf(World) = " + s.indexOf("World"));
        System.out.println("contains(World)? " + s.contains("World"));
        System.out.println("substring(7) = " + s.substring(7));
        System.out.println("substring(7,12) = " + s.substring(7, 12));
        System.out.println("replace = " + s.replace("World", "Java"));

        // 切分
        String csv = "a,b,c";
        String[] parts = csv.split(",");
        System.out.println("split: " + java.util.Arrays.toString(parts));

        // 拼接
        System.out.println("join: " + String.join("-", "a", "b", "c"));

        // 格式化
        System.out.println("%s is %d".formatted("Alice", 20));
    }

    /** 文本块（Java 15+）*/
    static void textBlock() {
        System.out.println("\n=== 文本块 ===");
        String json = """
                {
                  "name": "Alice",
                  "age": 20
                }
                """;
        System.out.println(json);
    }
}
