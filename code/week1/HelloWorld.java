/**
 * Week 1 · 第一个 Java 程序
 *
 * 这份文件没有 package 声明，所以它在"默认包"里，能直接 javac/java 跑。
 * 一旦学了 Maven（Week 4）就会把所有类放进 package。
 *
 * 在 IDEA 里：右键 → Run 'HelloWorld.main()'，或快捷键 ⌃R
 * 命令行：
 *   javac HelloWorld.java        编译，生成 HelloWorld.class（字节码）
 *   java HelloWorld              JVM 加载并运行
 *   java HelloWorld a b c        传 3 个命令行参数
 */
// public 修饰符：表示这个类对所有人可见
// class 关键字：定义一个类
// 类名 HelloWorld 必须和文件名一致（HelloWorld.java），否则编译报错
public class HelloWorld {

    // main 方法是 Java 程序入口，签名必须严格是：
    //   public          —— 公开，JVM 才能调用
    //   static          —— 静态，不需要先 new 一个对象
    //   void            —— 不返回任何值
    //   main            —— 名字固定
    //   String[] args   —— 命令行参数数组（每个元素一个参数）
    public static void main(String[] args) {

        // System.out 是 JDK 内置的标准输出对象
        // println = print + line（输出后换行），相当于 Python 的 print
        System.out.println("Hello, Java!");

        // 字符串拼接：Java 里 + 可以把 String 和其他类型拼起来
        // args.length 是数组的长度属性（不是方法，没有括号）
        System.out.println("收到 " + args.length + " 个参数");

        // 经典 for 循环：和 C / C++ 写法完全一样
        // int i = 0           初始化计数器
        // i < args.length     循环条件，i 等于 length 时退出
        // i++                 每轮结束后 i 加 1
        for (int i = 0; i < args.length; i++) {
            // args[i] 用方括号下标访问第 i 个元素，索引从 0 开始
            System.out.println("  args[" + i + "] = " + args[i]);
        }
    }
}
