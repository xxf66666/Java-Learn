package primitives;

// BigDecimal 是精确十进制数，用于钱 / 重要计算
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Week 1.5 §00 配套示例：基本类型、装箱拆箱、精度
 *
 * 跑这个 main 看每个 section 的输出，配合笔记理解
 */
public class PrimitiveDemo {

    public static void main(String[] args) {
        // 一段一段调用，方便单独跑某节
        overflow();             // 整数溢出
        floatingPoint();        // 浮点精度
        bigDecimal();           // BigDecimal 算钱
        integerCache();         // Integer 缓存
        boxingPerf();            // 装箱性能
    }

    /** 整数溢出：Integer.MAX_VALUE + 1 = MIN_VALUE */
    static void overflow() {
        System.out.println("\n=== 整数溢出 ===");

        int max = Integer.MAX_VALUE;          // 2147483647
        // max + 1 在 int 范围内会溢出
        int overflow = max + 1;
        System.out.println("max     = " + max);
        System.out.println("max + 1 = " + overflow);     // -2147483648（绕回）

        // 想算大数：先转 long
        long bigSafe = (long) Integer.MAX_VALUE * 2;
        // 错误的写法：会先用 int 算出溢出的值再转 long
        long bigWrong = Integer.MAX_VALUE * 2;
        System.out.println("safe: " + bigSafe);          // 4294967294
        System.out.println("wrong: " + bigWrong);        // -2
    }

    /** 浮点精度坑：0.1 + 0.2 != 0.3 */
    static void floatingPoint() {
        System.out.println("\n=== 浮点精度 ===");

        // double 是 IEEE 754 二进制浮点，无法精确表示十进制小数
        double a = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 = " + a);          // 0.30000000000000004

        // 算金额：又一个例子
        double price = 19.99;
        double total = price * 3;
        System.out.println("19.99 × 3 = " + total);      // 59.970000000000006
    }

    /** BigDecimal：精确十进制运算 */
    static void bigDecimal() {
        System.out.println("\n=== BigDecimal ===");

        // ⚠️ 必须用 String 构造，不要用 double（否则浮点误差进来了）
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");
        // add 返回新对象（BigDecimal 是不可变类）
        BigDecimal sum = a.add(b);
        System.out.println("0.1 + 0.2 = " + sum);        // 精确的 0.3

        // 算总价
        BigDecimal price = new BigDecimal("19.99");
        BigDecimal qty = new BigDecimal("3");
        BigDecimal total = price.multiply(qty);
        System.out.println("19.99 × 3 = " + total);       // 59.97

        // 除法可能除不尽，必须指定精度
        BigDecimal x = new BigDecimal("10");
        BigDecimal y = new BigDecimal("3");
        // setScale(保留位数, 舍入模式)
        BigDecimal div = x.divide(y, 4, RoundingMode.HALF_UP);
        System.out.println("10 ÷ 3 (4位四舍五入) = " + div);   // 3.3333

        // 比较大小：用 compareTo，不要用 equals
        // equals 还比较 scale："1.0" 和 "1.00" equals 是 false
        BigDecimal m = new BigDecimal("1.0");
        BigDecimal n = new BigDecimal("1.00");
        System.out.println("1.0.equals(1.00)? " + m.equals(n));     // false
        System.out.println("1.0.compareTo(1.00)? " + m.compareTo(n)); // 0（相等）
    }

    /** Integer 缓存的经典坑 */
    static void integerCache() {
        System.out.println("\n=== Integer 缓存 ===");

        // JVM 缓存 [-128, 127] 范围内的 Integer
        Integer a = 100;
        Integer b = 100;
        // == 比较引用：缓存命中，是同一对象 → true
        System.out.println("100 == 100: " + (a == b));        // true

        Integer c = 200;
        Integer d = 200;
        // 200 超出缓存范围，是两个不同对象 → false
        System.out.println("200 == 200: " + (c == d));        // false

        // 永远用 .equals 比较内容
        System.out.println("200.equals(200): " + c.equals(d)); // true

        // int 永远是值比较，没这个坑
        int x = 200;
        int y = 200;
        System.out.println("int 200 == 200: " + (x == y));    // true
    }

    /** 包装类型循环的性能问题 */
    static void boxingPerf() {
        System.out.println("\n=== 装箱性能 ===");

        int n = 1_000_000;

        // ❌ Long sum：每次 += 都拆箱-加-装箱，创建百万 Long 对象
        long t1 = System.currentTimeMillis();
        Long boxedSum = 0L;
        for (long i = 0; i < n; i++) boxedSum += i;
        long d1 = System.currentTimeMillis() - t1;
        System.out.println("Long sum (慢): " + boxedSum + " 耗时 " + d1 + "ms");

        // ✅ long sum：基本类型，无装箱
        long t2 = System.currentTimeMillis();
        long primSum = 0L;
        for (long i = 0; i < n; i++) primSum += i;
        long d2 = System.currentTimeMillis() - t2;
        System.out.println("long sum (快): " + primSum + " 耗时 " + d2 + "ms");
    }
}
