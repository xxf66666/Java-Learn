package inner_classes;

// 函数式接口的"标准库"
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Week 1.5 §05 配套示例：内部类 / 匿名类 / Lambda
 */
public class InnerClassDemo {

    // ============ 静态内部类 ============
    // static 让 Builder 不依赖 Outer 实例
    // 通过 new Outer.Builder() 直接创建

    /** 外层类：HttpRequest */
    static class HttpRequest {
        private String url;
        private Map<String, String> headers;

        // 私有构造器：强制走 Builder
        private HttpRequest() {}

        // 静态内部类：标准 Builder 模式
        public static class Builder {
            private final HttpRequest req = new HttpRequest();

            public Builder() {
                req.headers = new HashMap<>();
            }

            // 链式调用：每个方法返回 this
            public Builder url(String url) {
                req.url = url;
                return this;
            }

            public Builder header(String k, String v) {
                req.headers.put(k, v);
                return this;
            }

            public HttpRequest build() {
                return req;
            }
        }

        @Override
        public String toString() {
            return "HttpRequest{url='" + url + "', headers=" + headers + "}";
        }
    }

    public static void main(String[] args) throws Exception {
        staticInnerDemo();
        anonymousVsLambda();
        methodReferences();
        lambdaCapture();
    }

    /** 静态内部类的典型用法：Builder 模式 */
    static void staticInnerDemo() {
        System.out.println("\n=== 静态内部类 / Builder ===");

        // 链式构造，可读性很高
        HttpRequest req = new HttpRequest.Builder()
            .url("https://example.com/api")
            .header("Authorization", "Bearer xxx")
            .header("Content-Type", "application/json")
            .build();

        System.out.println(req);
    }

    /** 匿名内部类 vs Lambda */
    static void anonymousVsLambda() throws InterruptedException {
        System.out.println("\n=== 匿名内部类 vs Lambda ===");

        // 老写法：匿名内部类（Java 8 之前唯一选项）
        // 写 new SomeInterface() { ... } 表示"实现接口并立即新建实例"
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("匿名内部类: hello");
            }
        };

        // 新写法：Lambda（Java 8+，只能用于"函数式接口"）
        // Runnable 是函数式接口（只有一个抽象方法）
        Runnable r2 = () -> System.out.println("Lambda: hello");

        // 都能传给线程
        new Thread(r1).start();
        new Thread(r2).start();
        Thread.sleep(100);     // 等线程跑完
    }

    /** 四种方法引用 */
    static void methodReferences() {
        System.out.println("\n=== 方法引用 ===");

        // 1) 类::静态方法
        // Integer::parseInt 等价于 s -> Integer.parseInt(s)
        Function<String, Integer> parser = Integer::parseInt;
        System.out.println("parser.apply(\"42\") = " + parser.apply("42"));

        // 2) 类::实例方法
        // String::length 等价于 s -> s.length()
        Function<String, Integer> lengthOf = String::length;
        System.out.println("lengthOf(\"hello\") = " + lengthOf.apply("hello"));

        // 3) 对象::实例方法
        // System.out::println 等价于 s -> System.out.println(s)
        Consumer<String> printer = System.out::println;
        printer.accept("方法引用");

        // 4) 类::new（构造器引用）
        Function<String, StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.apply("初始内容");
        System.out.println("sb = " + sb);
    }

    /** Lambda 捕获变量 */
    static void lambdaCapture() throws Exception {
        System.out.println("\n=== Lambda 捕获 ===");

        // 捕获 final / 事实上 final 的局部变量
        int factor = 10;
        Function<Integer, Integer> times = x -> x * factor;
        System.out.println("5 * factor = " + times.apply(5));

        // factor = 20;    // ❌ 一旦被 Lambda 捕获，就不能再改

        // 循环里的 final 拷贝
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            // i 是循环变量，每轮都变 → Lambda 不能直接捕获
            // 拷一份到 final 的 id
            int id = i;
            pool.submit(() -> System.out.println("task " + id));
        }
        pool.shutdown();
        pool.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS);
    }
}
