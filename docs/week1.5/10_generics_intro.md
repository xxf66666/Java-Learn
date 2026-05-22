# Week 1.5 §10 · 泛型入门

> 你已经见过 `List<String>`、`Map<String, Integer>` 这种 `<>` 语法很多次了。这就是**泛型**。
>
> 本篇讲它**为什么存在、怎么自己用、有哪些坑**。Week 2 会再深入展开。

---

## 1. 为什么需要泛型

假设没有泛型，我们写一个能装东西的 Box：

```java
public class Box {
    private Object content;     // 用 Object 接收"任何类型"

    public void set(Object o) { this.content = o; }
    public Object get() { return content; }
}

// 使用
Box box = new Box();
box.set("hello");
String s = (String) box.get();      // 必须强转

box.set("world");
Integer x = (Integer) box.get();    // ❌ 运行时崩 ClassCastException
```

**问题**
- 取出来必须**强转**
- 编译器**抓不到**塞错类型的 bug，直到运行时崩

**有了泛型**：

```java
public class Box<T> {           // T 是类型占位符
    private T content;

    public void set(T value) { this.content = value; }
    public T get() { return content; }
}

// 使用
Box<String> box = new Box<>();
box.set("hello");
String s = box.get();               // ✅ 不用强转

box.set(42);                         // ❌ 编译错误：42 不是 String
```

**泛型核心价值**：**把运行时的类型错误提前到编译期**。

---

## 2. 怎么定义一个泛型类

```java
public class Box<T> {
    private T value;

    public void set(T value) { this.value = value; }
    public T get() { return value; }
}
```

**`<T>` 写在类名后面**，表示这个类有一个类型参数。`T` 在类体内**作为类型用**。

使用时填具体类型：

```java
Box<String> a = new Box<>();          // T = String
Box<Integer> b = new Box<>();          // T = Integer
Box<Dog> c = new Box<>();              // T = Dog
```

`<>` 是"菱形语法"，右边的 `new Box<>()` 编译器自动推断 T。

### 命名惯例

| 字母 | 含义 |
|------|------|
| `T` | Type（通用） |
| `E` | Element（集合元素） |
| `K`, `V` | Key, Value |
| `R` | Result |

可以一个类有多个类型参数：

```java
public class Pair<K, V> {
    private K key;
    private V value;
    // ...
}

Pair<String, Integer> p = new Pair<>();
```

---

## 3. 泛型方法

类不一定要泛型，**单个方法**也可以：

```java
public class Util {
    // <T> 写在返回类型前
    public static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}

// 使用
String s = Util.firstOrNull(List.of("a", "b"));        // T 自动推断为 String
Integer i = Util.firstOrNull(List.of(1, 2, 3));        // T 自动推断为 Integer
```

---

## 4. 上界 `T extends X`

要求 T 必须是 X 或 X 的子类：

```java
// 求最大值的方法：T 必须能比较，所以要求实现 Comparable
public static <T extends Comparable<T>> T max(List<T> list) {
    T best = list.get(0);
    for (T x : list) {
        if (x.compareTo(best) > 0) best = x;
    }
    return best;
}

max(List.of(3, 1, 4, 1, 5));       // ✅ Integer 实现了 Comparable
max(List.of("a", "b", "c"));        // ✅ String 也实现了
```

`<T extends Comparable<T>>` 这一句保证了"T 一定有 `compareTo` 方法"。

---

## 5. 通配符 `?`

到这里都是"定义"，下面是"使用"层面的通配符：

### 5.1 `? extends T`：T 或 T 的子类

```java
// 接受 List<Number> 或 List<Integer> 或 List<Double> ...
public static double sum(List<? extends Number> nums) {
    double total = 0;
    for (Number n : nums) {
        total += n.doubleValue();
    }
    return total;
}

sum(List.of(1, 2, 3));            // ✅ Integer 是 Number 子类
sum(List.of(1.5, 2.5));            // ✅ Double 是 Number 子类
```

**用于"读"**：从集合里拿东西，能当 T 用。

### 5.2 `? super T`：T 或 T 的父类

```java
// 往 List 里塞 Integer：List<Integer> / List<Number> / List<Object> 都行
public static void addInts(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}
```

**用于"写"**：能往集合里塞 T。

### 5.3 PECS 法则（Producer Extends, Consumer Super）

- 你**从集合读** → 集合是 Producer → 用 `extends`
- 你**往集合写** → 集合是 Consumer → 用 `super`

实际工作中大量出现，**先理解 `? extends T` 就够**，`? super T` 用得没那么多。

---

## 6. 类型擦除（一句话）

Java 的泛型是**编译期检查**，**编译后类型信息会被擦除**。

```java
List<String> a = new ArrayList<>();
List<Integer> b = new ArrayList<>();
a.getClass() == b.getClass()        // true！运行时都是 ArrayList
```

这导致几个限制（**遇到再回来翻**）：

- 不能 `new T()` —— 运行时不知道 T 是什么
- 不能 `instanceof List<String>` —— 只能 `instanceof List`
- 不能重载只参数化不同的方法

记一句话就行：**泛型是编译期的事，运行时看不见**。

---

## 7. 配套代码

[`code/week1.5/s10_generics/GenericsDemo.java`](../../code/week1.5/s10_generics/GenericsDemo.java)

跑一遍看：
- 自定义泛型类 `Box<T>`、`Pair<K, V>`
- 泛型方法 `firstOrNull`、`max` (带 `extends Comparable`)
- 用 `? extends Number` 写 sum
- 类型擦除导致两个 List 运行时是同一个 class

---

## 8. 自查

- [ ] 写一个 `Box<T>` 类，能装任意类型
- [ ] 写一个 `Pair<K, V>` 类
- [ ] 写一个泛型方法 `<T> T lastOrNull(List<T> list)` 返回最后一个元素
- [ ] 写一个 `<T extends Comparable<T>>` 的 min 方法
- [ ] 解释 `Box<int>` 为什么编译错（基本类型不能做泛型参数）
- [ ] 解释 PECS 是什么意思
