/**
 * Week 1 · 第一个 Java 程序
 *
 * 在 IDEA 里：右键 → Run 'HelloWorld.main()'，或快捷键 ⌃R
 * 命令行：
 *   javac HelloWorld.java
 *   java HelloWorld
 */
public class HelloWorld {

    public static void main(String[] args) {
        System.out.println("Hello, Java!");

        // 命令行参数（类似 Python sys.argv，C++ argv）
        System.out.println("收到 " + args.length + " 个参数");
        for (int i = 0; i < args.length; i++) {
            System.out.println("  args[" + i + "] = " + args[i]);
        }
    }
}
