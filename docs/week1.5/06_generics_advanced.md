# Week 1.5 §06 · 泛型进阶

> Week 2 §02 讲了泛型基础。本节深入：通配符、PECS、类型擦除细节。这些是面试和工业代码都会用到的。

---

## 1. 回顾：为什么有泛型

```java
// 没泛型
List list = new ArrayList();
list.add("hello");
list.add(123);
String s = (String) list.get(1);    // 运行时崩 ClassCastException

// 有泛型
List<String> list = new ArrayList<>();
list.add("hello");
list.add(123);                       // ❌ 编译错误
```

**核心价值**：把运行时类型错误提前到**编译期**。

---

## 2. 类型参数 vs 通配符

```java
// 类型参数（声明时用）：T 是占位符
public class Box<T> {
    private T value;
    public T get() { return value; }
}

// 通配符（使用时用）：? 表示"某个未知类型"
List<?> any = ...;
```

---

## 3. 三种通配符

### 3.1 `?` 无界通配符

"任何类型都行，但我不知道具体是什么"。

```java
public void printAll(List<?> list) {
    for (Object o : list) {            // 只能当 Object 用
        System.out.println(o);
    }
    // list.add(...);    // ❌ 不能 add（除了 null）
}

printAll(List.of(1, 2, 3));            // List<Integer> ✅
printAll(List.of("a", "b"));            // List<String> ✅
```

适合**只读**的方法。

### 3.2 `? extends T` 上界

"T 或 T 的子类"。

```java
public double sum(List<? extends Number> nums) {
    double total = 0;
    for (Number n : nums) total += n.doubleValue();
    return total;
    // nums.add(1);    // ❌ 不能 add（编译器不知道具体是 Integer 还是 Double）
}

sum(List.of(1, 2, 3));           // List<Integer> 是 List<? extends Number> ✅
sum(List.of(1.5, 2.5));          // List<Double> ✅
```

**用于"读"**：能拿出来当 T 用。

### 3.3 `? super T` 下界

"T 或 T 的父类"。

```java
public void addInts(List<? super Integer> list) {
    list.add(1);          // ✅ Integer 一定能放进"Integer 或父类"的 List
    list.add(2);
    // Integer x = list.get(0);    // ❌ 取出来时类型是 ? super Integer，可能是 Object
}

addInts(new ArrayList<Integer>());   // ✅
addInts(new ArrayList<Number>());    // ✅（Number 是 Integer 的父类）
addInts(new ArrayList<Object>());    // ✅
```

**用于"写"**：能往里塞 T。

---

## 4. PECS 法则（必背）

**P**roducer **E**xtends, **C**onsumer **S**uper

- 你**从集合读**（集合是 Producer 提供数据）→ 用 `extends`
- 你**往集合写**（集合是 Consumer 接收数据）→ 用 `super`

经典例子：`Collections.copy`

```java
public static <T> void copy(List<? super T> dest, List<? extends T> src) {
    // src 是 Producer：从里面读元素 → extends
    // dest 是 Consumer：往里面写元素 → super
    for (int i = 0; i < src.size(); i++) {
        dest.set(i, src.get(i));
    }
}
```

---

## 5. 泛型方法

类不一定要泛型，单个方法也可以：

```java
public class Util {
    // <T> 写在返回类型前
    public static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    // 多个类型参数
    public static <K, V> Map<V, K> reverse(Map<K, V> map) {
        Map<V, K> result = new HashMap<>();
        for (Map.Entry<K, V> e : map.entrySet()) {
            result.put(e.getValue(), e.getKey());
        }
        return result;
    }
}

// 使用
String s = Util.firstOrNull(List.of("a", "b"));      // T = String
Integer i = Util.firstOrNull(List.of(1, 2, 3));      // T = Integer
```

---

## 6. 上界泛型 + 多接口

类型参数也能加 extends，要求 T 必须**继承某类 + 实现某接口**：

```java
// T 必须实现 Comparable<T>
public static <T extends Comparable<T>> T max(List<T> list) {
    T best = list.get(0);
    for (T x : list) {
        if (x.compareTo(best) > 0) best = x;
    }
    return best;
}

max(List.of(3, 1, 4, 1, 5));     // ✅ Integer 实现 Comparable
max(List.of("a", "b", "c"));     // ✅ String 实现 Comparable

// 多个边界：用 & 分隔（类必须放第一位）
public static <T extends Number & Comparable<T>> T xx(T a, T b) { ... }
```

---

## 7. 类型擦除

Java 泛型是**编译期检查**，**编译后类型信息会被擦除**。

```java
List<String> a = new ArrayList<>();
List<Integer> b = new ArrayList<>();
a.getClass() == b.getClass()         // true ！运行时都是 ArrayList
```

### 擦除规则

- 无界 `T` → `Object`
- `T extends X` → `X`

```java
public class Container<T extends Number> {
    private T value;
}
// 擦除后：private Number value;
```

### 限制

```java
public class Box<T> {
    private T value;

    public void doSomething(Object o) {
        if (o instanceof T) { ... }       // ❌ 运行时不知道 T 是什么
        T newOne = new T();                // ❌ 同理，没法 new
        T[] arr = new T[10];               // ❌ 不能 new 泛型数组
    }
}
```

### 绕开擦除：传 Class 对象

```java
public class Box<T> {
    private final Class<T> type;
    public Box(Class<T> type) { this.type = type; }

    public T create() throws Exception {
        return type.getDeclaredConstructor().newInstance();    // 反射 new
    }
}

Box<User> box = new Box<>(User.class);
User u = box.create();
```

---

## 8. 常见坑 / 易错

### 不能重载只参数化不同的方法

```java
class X {
    void f(List<String> a) {}
    void f(List<Integer> b) {}    // ❌ 编译错误：擦除后签名一样
}
```

### 静态字段不能用类的类型参数

```java
public class Box<T> {
    private static T value;       // ❌ static 字段是类共享的，T 是每个实例的
    // 改成 static <T> T 方法是可以的
}
```

### 父子类泛型不构成继承

```java
List<Object> a = new ArrayList<String>();    // ❌ 不行！
// 即使 String 是 Object 的子类
// 想要类似行为得用 List<? extends Object>
```

---

## 9. 自查

- [ ] 写一个 `Container<T>` 类，方法 `T get() / void set(T v)`
- [ ] 写一个 `<T extends Comparable<T>>` 的 `min(List<T>)` 方法
- [ ] 用 PECS 写一个把一个 `List<Integer>` 内容拷到 `List<Number>` 的方法
- [ ] 解释为什么 `new T()` 不可以
- [ ] 解释为什么 `List<Object>` 不能装 `List<String>`

## 代码示例

→ [`code/week1.5/generics/GenericsDemo.java`](../../code/week1.5/generics/GenericsDemo.java)
