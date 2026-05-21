# Week 3 §00 · Lambda + Stream

> Java 8+ 引入的两大杀器。看完这章，你写的 Java 代码会从"啰嗦"变得"现代"。

---

## 1. Lambda 表达式：把"函数当参数传"

### 没有 Lambda 之前

要让线程跑一段代码，需要写一个完整的匿名类：

```java
new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("hello from thread");
    }
}).start();
```

### 有了 Lambda

```java
new Thread(() -> System.out.println("hello from thread")).start();
```

Lambda 的本质：**用一行代码定义"一个方法"**，然后当成参数传出去。

### Lambda 语法三种形态

```java
// 1. 无参
Runnable r = () -> System.out.println("hi");

// 2. 单参，可省略括号
Consumer<String> c = s -> System.out.println(s);

// 3. 多参 / 多行
BiFunction<Integer, Integer, Integer> add = (a, b) -> {
    int sum = a + b;
    return sum;
};
```

---

## 2. 函数式接口：Lambda 的"承接容器"

Lambda 不是凭空存在的，它必须被赋值给一个"只有一个抽象方法的接口"，这种接口叫**函数式接口**。

```java
@FunctionalInterface
interface MyFunc {
    int apply(int x);
}

MyFunc square = x -> x * x;
int r = square.apply(5);          // 25
```

### JDK 提供的 4 个常用函数式接口

| 接口 | 签名 | 用途 |
|------|------|------|
| `Function<T, R>` | `R apply(T t)` | 输入 T 输出 R（最通用） |
| `Predicate<T>` | `boolean test(T t)` | 判断 / 过滤 |
| `Consumer<T>` | `void accept(T t)` | 消费（如打印） |
| `Supplier<T>` | `T get()` | 凭空产出（如随机数生成器） |

```java
Function<String, Integer> len = s -> s.length();
Predicate<String> nonEmpty = s -> !s.isEmpty();
Consumer<String> printer = System.out::println;
Supplier<Double> random = Math::random;
```

### 方法引用：`::`

```java
// Lambda
list.forEach(s -> System.out.println(s));

// 方法引用：上面那种 "调用方法的 Lambda" 可以简写为
list.forEach(System.out::println);
```

四种方法引用：

| 形态 | 例子 | 等价 Lambda |
|------|------|-------------|
| 类::静态方法 | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| 类::实例方法 | `String::length` | `s -> s.length()` |
| 对象::实例方法 | `System.out::println` | `s -> System.out.println(s)` |
| 类::new | `ArrayList::new` | `() -> new ArrayList()` |

---

## 3. Stream API：处理集合的"流水线"

```java
List<String> names = List.of("Alice", "Bob", "Carol", "Dave");

// 找出名字以 A/C 开头且长度 > 3 的，转大写，排序
List<String> result = names.stream()
    .filter(n -> n.startsWith("A") || n.startsWith("C"))
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase)
    .sorted()
    .toList();

System.out.println(result);  // [ALICE, CAROL]
```

### Stream 三段式

```
源（list.stream() / IntStream.range / Files.lines）
   ↓
中间操作（filter / map / sorted / distinct / limit / skip）—— 懒求值
   ↓
终止操作（toList / forEach / count / reduce / collect）—— 触发执行
```

只要不调终止操作，中间操作不会执行（懒求值）。

### 常用操作速查

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5);

// filter：过滤
nums.stream().filter(n -> n % 2 == 0).toList();      // [2, 4]

// map：变换
nums.stream().map(n -> n * n).toList();              // [1, 4, 9, 16, 25]

// reduce：归约
int sum = nums.stream().reduce(0, Integer::sum);     // 15
int max = nums.stream().reduce(Integer.MIN_VALUE, Math::max);   // 5

// count
long cnt = nums.stream().filter(n -> n > 2).count();   // 3

// sorted
List<Integer> sorted = nums.stream().sorted(Comparator.reverseOrder()).toList();

// distinct
List.of(1, 1, 2, 3, 3).stream().distinct().toList();   // [1, 2, 3]

// limit / skip
nums.stream().skip(1).limit(2).toList();             // [2, 3]

// findFirst / anyMatch / allMatch
Optional<Integer> first = nums.stream().filter(n -> n > 2).findFirst();
boolean any = nums.stream().anyMatch(n -> n > 4);
boolean all = nums.stream().allMatch(n -> n > 0);
```

### Collectors：把 Stream 收集成集合

```java
import static java.util.stream.Collectors.*;

record Employee(String name, String dept, double salary) {}

List<Employee> emps = List.of(
    new Employee("Alice", "ENG", 100),
    new Employee("Bob", "ENG", 80),
    new Employee("Carol", "HR", 60),
    new Employee("Dave", "HR", 70)
);

// 按部门分组
Map<String, List<Employee>> byDept = emps.stream()
    .collect(groupingBy(Employee::dept));

// 按部门统计人数
Map<String, Long> countByDept = emps.stream()
    .collect(groupingBy(Employee::dept, counting()));

// 按部门求平均薪资
Map<String, Double> avgSalary = emps.stream()
    .collect(groupingBy(Employee::dept, averagingDouble(Employee::salary)));

// 拼接所有姓名
String allNames = emps.stream().map(Employee::name).collect(joining(", "));

// 收集成 Map
Map<String, Double> nameToSalary = emps.stream()
    .collect(toMap(Employee::name, Employee::salary));
```

---

## 4. `Optional`：避免空指针

```java
// 旧写法
String city = null;
if (user != null) {
    Address addr = user.getAddress();
    if (addr != null) {
        city = addr.getCity();
    }
}

// 新写法
String city = Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("未知");
```

**常用方法**

```java
Optional<String> opt = Optional.of("hello");
Optional<String> empty = Optional.empty();
Optional<String> maybe = Optional.ofNullable(getOrNull());

opt.isPresent();
opt.ifPresent(s -> System.out.println(s));
opt.orElse("default");
opt.orElseGet(() -> computeDefault());
opt.orElseThrow(() -> new RuntimeException("not found"));
opt.map(String::toUpperCase);
```

> **不要**把 Optional 当成字段类型用（用 null 就够），它是为**方法返回类型**设计的。

---

## 5. `record`：简洁的数据类（Java 16+）

```java
public record Point(int x, int y) {}

// 自动生成：构造器、getter（x() / y()，不是 getX）、equals / hashCode / toString
Point p = new Point(3, 4);
System.out.println(p.x() + ", " + p.y());
System.out.println(p);    // Point[x=3, y=4]
```

`record` 适合做"不可变数据载体"（DTO、Value Object）。后面写 Controller 返回值常用。

---

## 6. 自查

- [ ] 用 Lambda 启动一个线程
- [ ] 用 Stream 在一个 `List<Employee>` 上求"每个部门的平均薪资"
- [ ] 把一段"嵌套 null 判断"改写成 Optional 链
- [ ] 用 `record` 写一个 `Money(BigDecimal amount, String currency)`
- [ ] 解释中间操作和终止操作的区别（懒求值）

## 代码示例

→ [`code/week3/stream_demo/StreamDemo.java`](../../code/week3/stream_demo/StreamDemo.java)
