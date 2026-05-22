# Week 1.5 §09 · Lambda 入门

> 现代 Java 代码里到处是 `->` 这种箭头符号——那就是 Lambda。
>
> 本篇讲它**是什么、为什么存在、怎么写**。Week 3 会做完整的 Stream 实战。

---

## 1. 为什么需要 Lambda

Java 8 之前，要把"一段代码"传给方法，得包成一个对象——叫**匿名内部类**：

```java
// 老写法：让一个新线程跑一段代码
Thread t = new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("hello");
    }
});
t.start();
```

写了一堆模板，**真正的逻辑只有一行** `System.out.println("hello")`。

Java 8 引入 Lambda：

```java
Thread t = new Thread(() -> System.out.println("hello"));
t.start();
```

**Lambda 就是"一段代码"的简洁写法**。

---

## 2. Lambda 的三种形态

```java
// 1) 无参
Runnable r = () -> System.out.println("hi");

// 2) 单参，类型可省略
Consumer<String> c = s -> System.out.println(s);

// 3) 多参 + 多行（要花括号 + return）
BiFunction<Integer, Integer, Integer> add = (a, b) -> {
    int sum = a + b;
    return sum;
};
```

**怎么读 `(a, b) -> a + b`**？
- 左边 `(a, b)` 是参数列表
- `->` 是固定语法
- 右边是表达式或代码块

---

## 3. Lambda 必须配"函数式接口"

Lambda 不是凭空存在的，它**必须赋给一个变量**，且变量类型必须是**"函数式接口"**——只有**一个抽象方法**的接口。

```java
// 自定义函数式接口
@FunctionalInterface
interface MyFunc {
    int apply(int x);          // 只有一个抽象方法
}

// Lambda 实现这个接口
MyFunc square = x -> x * x;
int r = square.apply(5);        // 25
```

`@FunctionalInterface` 注解可加可不加，加了让编译器帮你检查"只有一个抽象方法"这个规则。

---

## 4. JDK 自带的 4 个常用函数式接口

不用自己写，JDK 提供了通用接口：

| 接口 | 唯一方法 | 用途 | 例子 |
|------|---------|------|------|
| `Function<T, R>` | `R apply(T)` | 输入 T 输出 R | `s -> s.length()` |
| `Predicate<T>` | `boolean test(T)` | 判断 / 过滤 | `s -> !s.isEmpty()` |
| `Consumer<T>` | `void accept(T)` | 消费（打印 / 写日志） | `System.out::println` |
| `Supplier<T>` | `T get()` | 凭空产出 | `Math::random` |

```java
import java.util.function.*;

Function<String, Integer> length = s -> s.length();
Predicate<String> nonEmpty = s -> !s.isEmpty();
Consumer<String> printer = s -> System.out.println(s);
Supplier<Double> random = () -> Math.random();

length.apply("hello");           // 5
nonEmpty.test("");                // false
printer.accept("hi");             // 输出 hi
random.get();                      // 某个随机数
```

---

## 5. 方法引用 `::`

如果 Lambda **只是简单地调用某个已有方法**，可以用更简洁的"方法引用"语法：

```java
// Lambda
list.forEach(s -> System.out.println(s));

// 方法引用：::
list.forEach(System.out::println);
```

`System.out::println` 等价于 `s -> System.out.println(s)`。

四种方法引用：

| 形态 | 例子 | 等价 Lambda |
|------|------|-------------|
| `类名::静态方法` | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| `类名::实例方法` | `String::length` | `s -> s.length()` |
| `对象::方法` | `System.out::println` | `s -> System.out.println(s)` |
| `类名::new` | `ArrayList::new` | `() -> new ArrayList<>()` |

---

## 6. Stream 入门（最常见的 Lambda 场景）

`Stream` 让你用 Lambda 链式处理集合：

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// 找偶数 → 平方 → 收集成 List
List<Integer> result = nums.stream()
    .filter(n -> n % 2 == 0)        // 留偶数
    .map(n -> n * n)                  // 每个平方
    .toList();                         // 收集
// result = [4, 16, 36, 64, 100]
```

Stream 的"三段式"：
- **源**：`集合.stream()` / `IntStream.range(...)` / ...
- **中间操作**：`filter` / `map` / `sorted` / ... 返回新 Stream，懒求值
- **终止操作**：`toList` / `count` / `forEach` / `sum` / ... 触发实际执行

Week 3 会详讲。本节只是先尝个鲜。

---

## 7. 一个易踩的坑：Lambda 里的变量

Lambda 可以引用外面的局部变量，但这些变量**必须是 final 或事实上 final**（一旦给定不能再改）：

```java
int factor = 10;
Function<Integer, Integer> times = x -> x * factor;     // ✅ 捕获 factor

factor = 20;       // ❌ 编译错误：factor 被 Lambda 捕获了，不能再改
```

循环里的坑：

```java
// ❌ i 在循环里在变
for (int i = 0; i < 3; i++) {
    pool.submit(() -> System.out.println(i));     // 编译错误
}

// ✅ 拷一份
for (int i = 0; i < 3; i++) {
    int id = i;                  // id 每轮是新变量，effectively final
    pool.submit(() -> System.out.println(id));
}
```

---

## 8. 配套代码

[`code/week1.5/s09_lambda/LambdaDemo.java`](../../code/week1.5/s09_lambda/LambdaDemo.java)

跑一遍看：
- 用 Lambda 启动线程
- 四个 JDK 函数式接口
- 方法引用四种形态
- Stream filter / map / collect 链式
- Lambda 捕获变量的坑

---

## 9. 自查

- [ ] 用 Lambda 启动一个线程，打印 "hello from thread"
- [ ] 用 `Function<String, Integer>` 写一个 Lambda 返回字符串长度
- [ ] 用 `Predicate<Integer>` 写一个判奇偶的 Lambda
- [ ] 用 Stream 把 `[1..10]` 过滤出 > 5 的、平方、收集成 List
- [ ] 把 `s -> s.length()` 改成方法引用
- [ ] 解释"事实上 final" —— 写一段循环里 Lambda 引用循环变量的例子，触发编译错并修复
