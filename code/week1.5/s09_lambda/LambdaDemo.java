package s09_lambda;

import java.util.List;
import java.util.function.*;

public class LambdaDemo {

    public static void main(String[] args) throws InterruptedException {
        threadLambda();
        functionalInterfaces();
        methodReferences();
        streamIntro();
        captureDemo();
    }

    /** Lambda 启动线程：和老的匿名内部类对比 */
    static void threadLambda() throws InterruptedException {
        System.out.println("\n=== Lambda 启动线程 ===");

        // 老写法：匿名内部类
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello from t1 (匿名内部类)");
            }
        });
        t1.start();
        t1.join();      // 等 t1 跑完

        // 新写法：Lambda（一行解决）
        Thread t2 = new Thread(() -> System.out.println("hello from t2 (Lambda)"));
        t2.start();
        t2.join();
    }

    /** JDK 四个常用函数式接口 */
    static void functionalInterfaces() {
        System.out.println("\n=== 4 个函数式接口 ===");

        // Function<T, R>：输入 T 输出 R
        Function<String, Integer> length = s -> s.length();
        System.out.println("length(hello) = " + length.apply("hello"));    // 5

        // Predicate<T>：输入 T 输出 boolean
        Predicate<String> nonEmpty = s -> !s.isEmpty();
        System.out.println("nonEmpty(\"\") = " + nonEmpty.test(""));       // false

        // Consumer<T>：输入 T 无返回（消费）
        Consumer<String> printer = s -> System.out.println("消费: " + s);
        printer.accept("hi");

        // Supplier<T>：无输入产出 T
        Supplier<Double> random = () -> Math.random();
        System.out.println("random = " + random.get());
    }

    /** 方法引用四种形态 */
    static void methodReferences() {
        System.out.println("\n=== 方法引用 ===");

        // 1) 类::静态方法：Integer.parseInt(s)
        Function<String, Integer> parser = Integer::parseInt;
        System.out.println("parser(\"42\") = " + parser.apply("42"));

        // 2) 类::实例方法：自动把第一个参数作为 receiver
        // String::length 等价于 s -> s.length()
        Function<String, Integer> lengthOf = String::length;
        System.out.println("lengthOf(\"hi\") = " + lengthOf.apply("hi"));

        // 3) 对象::方法
        Consumer<String> printer = System.out::println;
        printer.accept("方法引用消费");

        // 4) 类::new（构造器引用）
        Function<String, StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.apply("初始");
        System.out.println("sb = " + sb);
    }

    /** Stream 入门：链式处理集合 */
    static void streamIntro() {
        System.out.println("\n=== Stream 初见 ===");

        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 找偶数 → 平方 → 收集
        // .stream() 把集合转成流（流是懒求值的）
        // .filter / .map 是"中间操作"，不触发执行
        // .toList() 是"终止操作"，触发实际计算
        List<Integer> evenSquares = nums.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .toList();
        System.out.println("偶数平方: " + evenSquares);    // [4, 16, 36, 64, 100]

        // 求和
        int sum = nums.stream().reduce(0, Integer::sum);
        System.out.println("总和: " + sum);                  // 55

        // 数 > 5 的有几个
        long count = nums.stream().filter(n -> n > 5).count();
        System.out.println("> 5 的个数: " + count);          // 5
    }

    /** Lambda 捕获变量的"事实上 final" 规则 */
    static void captureDemo() {
        System.out.println("\n=== Lambda 捕获 ===");

        // 局部变量被 Lambda 捕获后不能再改
        int factor = 10;
        Function<Integer, Integer> times = x -> x * factor;
        System.out.println("5 * factor = " + times.apply(5));

        // factor = 20;    // ❌ 编译错误：被 Lambda 捕获后不能改

        // 循环变量也一样：每轮要拷一份到 final 局部
        for (int i = 0; i < 3; i++) {
            int id = i;     // id 在每轮里都是"新变量"，effectively final
            Runnable r = () -> System.out.println("task " + id);
            r.run();
        }
    }
}
