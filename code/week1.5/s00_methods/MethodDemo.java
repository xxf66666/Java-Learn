package s00_methods;

/**
 * Week 1.5 §00 配套示例：方法
 *
 * 跑这个 main，看每个 section 的输出。
 * 强烈建议在 IDEA 里打断点单步走一遍，体会"方法调用 → 跳进去 → 返回"。
 */
public class MethodDemo {

    // ====== 1) 最基本的方法 ======
    // public static = 修饰符（先照写，后面 §03 会讲）
    // int = 返回类型（方法返回一个整数）
    // add = 方法名
    // (int a, int b) = 参数列表：要求传两个整数进来
    public static int add(int a, int b) {
        int result = a + b;        // 算结果
        return result;              // return：把 result 的值"还"给调用方
    }

    // ====== 2) void 方法（不返回任何东西）======
    // void：这个方法做完就完了，不还任何值
    public static void greet(String name) {
        // String + : 字符串拼接
        System.out.println("Hello, " + name);
        // 不写 return 也可以；要中途结束就写一句 return;（不带值）
    }

    // ====== 3) return 提前结束 ======
    // 求绝对值：负数变正、正数不变
    public static int abs(int x) {
        if (x < 0) {
            return -x;             // 一旦 return，方法立刻结束，下面的不执行
        }
        return x;
    }

    // ====== 4) 参数传递：基本类型是"值的副本" ======
    // 改 x 不影响调用方的变量
    public static void tryChangeInt(int x) {
        x = 999;                   // 这个 x 是 main 里 a 的副本，互不相干
    }

    // ====== 5) 参数传递：数组等引用类型可以"看见"修改 ======
    // 因为 arr 和调用方的引用指向同一块数组
    public static void tryChangeArray(int[] arr) {
        arr[0] = 999;              // 改的是这块数组本身
    }

    // ====== 6) 方法重载：同名不同参数 ======
    // 编译器看参数类型/个数自动选哪个版本

    // 两个 int 的最大值
    public static int max(int a, int b) {
        return a > b ? a : b;      // 三元运算符 ? : 类似 "if a > b then a else b"
    }

    // 三个 int 的最大值
    public static int max(int a, int b, int c) {
        // 复用上面的两参版本
        return max(max(a, b), c);
    }

    // 两个 double 的最大值（参数类型不同也算重载）
    public static double max(double a, double b) {
        return a > b ? a : b;
    }

    // ====== main 入口 ======
    public static void main(String[] args) {
        // === 调用有返回值的方法 ===
        int sum = add(3, 5);                     // 把 3 给 a，5 给 b，返回 8
        System.out.println("3 + 5 = " + sum);    // 8

        // === 调用 void 方法 ===
        greet("Alice");                          // 不需要接住返回值
        greet("Bob");

        // === abs ===
        System.out.println("abs(-7) = " + abs(-7));    // 7
        System.out.println("abs(3) = " + abs(3));       // 3

        // === 基本类型参数：传的是值副本 ===
        int a = 1;
        tryChangeInt(a);                          // 方法里把 x 改成 999
        System.out.println("a = " + a);          // 还是 1！方法里改的是副本

        // === 数组参数：传的是引用，能改到原对象 ===
        int[] nums = {1, 2, 3};
        tryChangeArray(nums);
        System.out.println("nums[0] = " + nums[0]);   // 999！

        // === 方法重载：调用时自动选 ===
        System.out.println("max(3, 5) = " + max(3, 5));            // int 版
        System.out.println("max(3, 5, 7) = " + max(3, 5, 7));      // int 三参版
        System.out.println("max(3.14, 2.71) = " + max(3.14, 2.71));// double 版
    }
}
