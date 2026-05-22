package s03_encapsulation;

/**
 * 纯静态工具类：所有方法都 static，调用时不用 new
 *
 * 这种类**通常不让 new**（构造器 private），因为 new 出来也没用
 */
public class StringUtils {

    // private 构造器：防止外界 new StringUtils()
    private StringUtils() {
        // 永远不应该被调用
    }

    /** 字符串是 null / 空 / 全空白 → true */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** 反转字符串 */
    public static String reverse(String s) {
        if (s == null) return null;
        return new StringBuilder(s).reverse().toString();
    }

    /** 整数转字符串然后判奇偶 */
    public static boolean isEven(int n) {
        return n % 2 == 0;
    }
}
