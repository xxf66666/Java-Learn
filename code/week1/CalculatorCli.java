import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Week 1 · 命令行计算器
 *
 * 学到的点：
 *  - Scanner 读输入（类似 Python input、C++ cin）
 *  - try-catch-finally
 *  - switch 表达式（Java 14+）
 *  - while + break 循环交互
 *
 * 自己练习的 TODO：
 *  1. 加上 "支持连续运算"：把上一次结果保留，下一次输入 "= 5" 表示用上次结果 + 5
 *  2. 把异常处理抽成一个独立方法
 *  3. 加上 history（用 ArrayList<String> 存每一次运算）
 */
public class CalculatorCli {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== Java 命令行计算器 =====");
        System.out.println("输入 q 退出");

        while (true) {
            try {
                System.out.print("\n第一个数字：");
                String line = sc.nextLine().trim();
                if ("q".equalsIgnoreCase(line)) {
                    System.out.println("再见！");
                    break;
                }
                double a = Double.parseDouble(line);

                System.out.print("运算符 (+ - * /)：");
                String op = sc.nextLine().trim();

                System.out.print("第二个数字：");
                double b = Double.parseDouble(sc.nextLine().trim());

                double result = compute(a, op, b);
                System.out.printf("%s %s %s = %s%n", a, op, b, result);

            } catch (NumberFormatException e) {
                System.err.println("❌ 输入不是合法数字，请重试");
            } catch (ArithmeticException e) {
                System.err.println("❌ " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("❌ " + e.getMessage());
            }
        }

        sc.close();
    }

    /**
     * 核心运算逻辑（独立成方法，方便单测，Week 4 学 JUnit 后回头加测试）
     */
    static double compute(double a, String op, double b) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new ArithmeticException("除数不能为 0");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("不支持的运算符：" + op);
        };
    }
}
