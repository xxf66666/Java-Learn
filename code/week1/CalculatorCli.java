// Scanner 用来读控制台输入
import java.util.Scanner;

/**
 * Week 1 · 命令行计算器
 *
 * 学到的点：
 *  - Scanner 读输入（类似 Python input、C++ cin）
 *  - try-catch-finally：异常处理
 *  - switch 表达式（Java 14+）
 *  - while + break：循环交互
 *
 * 自己练习的 TODO：
 *  1. 加上 "支持连续运算"：把上一次结果保留，下一次输入 "= 5" 表示用上次结果 + 5
 *  2. 把异常处理抽成一个独立方法
 *  3. 加上 history（用 ArrayList<String> 存每一次运算）
 */
public class CalculatorCli {

    public static void main(String[] args) {
        // new Scanner(System.in) 创建一个绑定到标准输入的扫描器
        // System.in 是 JDK 内置的标准输入流
        Scanner sc = new Scanner(System.in);

        System.out.println("===== Java 命令行计算器 =====");
        System.out.println("输入 q 退出");

        // while (true) 死循环；通过 break 退出
        while (true) {
            // try-catch：把可能抛异常的代码包起来
            // 任意 catch 块捕到异常，整段 try 终止，跳到对应 catch
            try {
                System.out.print("\n第一个数字：");
                // nextLine() 读一整行，返回 String
                // trim() 去掉首尾空格
                String line = sc.nextLine().trim();

                // equalsIgnoreCase：忽略大小写比较字符串
                // 字符串字面量在前可以避免 line 为 null 时的空指针异常（最佳实践）
                if ("q".equalsIgnoreCase(line)) {
                    System.out.println("再见！");
                    break;       // 跳出 while 循环
                }

                // Double.parseDouble 把字符串转 double
                // 转不动会抛 NumberFormatException，被下面 catch 捕获
                double a = Double.parseDouble(line);

                System.out.print("运算符 (+ - * /)：");
                String op = sc.nextLine().trim();

                System.out.print("第二个数字：");
                double b = Double.parseDouble(sc.nextLine().trim());

                // 调用我们自己定义的 compute 方法
                double result = compute(a, op, b);

                // printf 格式化输出；%s 字符串、%n 换行
                System.out.printf("%s %s %s = %s%n", a, op, b, result);

            // 多个 catch 顺序排，从具体到一般匹配
            } catch (NumberFormatException e) {
                // System.err 是标准错误流（IDE 里通常显示为红色）
                System.err.println("❌ 输入不是合法数字，请重试");
            } catch (ArithmeticException e) {
                // getMessage() 取异常时附带的描述文本
                System.err.println("❌ " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("❌ " + e.getMessage());
            }
        }

        // close 释放 Scanner 占用的资源
        // 这里关 Scanner 会顺带关掉 System.in；学到 try-with-resources（Week 2）后会更自动化
        sc.close();
    }

    /**
     * 核心运算逻辑（独立成方法，方便单测，Week 4 学 JUnit 后回头加测试）
     *
     * static 方法可以直接通过类名调用，不依赖具体对象
     */
    static double compute(double a, String op, double b) {
        // switch 表达式：每个分支用 -> 返回值；最终整个 switch 是表达式
        return switch (op) {
            // -> 后面直接是表达式，作为返回值
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;

            // 多行逻辑用 { ... yield 值; } 块
            case "/" -> {
                // 显式判 0，否则会拿到 Infinity（double 除 0 不抛异常，但业务上不接受）
                if (b == 0) throw new ArithmeticException("除数不能为 0");
                yield a / b;     // yield 关键字：从代码块返回值（switch 表达式专用）
            }

            // default 兜底：传入了不认识的运算符就抛异常
            // throw 主动抛出异常，会被外层 try-catch 捕获
            default -> throw new IllegalArgumentException("不支持的运算符：" + op);
        };
    }
}
