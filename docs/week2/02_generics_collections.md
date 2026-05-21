# Week 2 §02 · 泛型 + 集合框架

> 目标：把 Java 工作中天天用的 `List` / `Map` / `Set` 用熟，理解泛型为什么需要、怎么写。

---

## 1. 泛型：为什么需要

没有泛型的代码：

```java
List list = new ArrayList();
list.add("hello");
list.add(123);                     // 编译器不会拦
String s = (String) list.get(1);   // 运行时崩：ClassCastException
```

有了泛型：

```java
List<String> list = new ArrayList<>();
list.add("hello");
list.add(123);                     // ❌ 编译错误：类型不匹配
String s = list.get(0);            // ✅ 不用强转
```

**泛型的核心价值：把运行时的类型错误提前到编译期。**

---

## 2. 怎么用泛型类

```java
// 标准库的泛型类
List<String> names = new ArrayList<>();         // <> 是"菱形语法"，编译器推断
Map<String, Integer> scores = new HashMap<>();
Optional<User> user = Optional.empty();

// JDK 9+ 简便创建（创建出的是不可变集合）
List<Integer> nums = List.of(1, 2, 3);
Map<String, Integer> m = Map.of("a", 1, "b", 2);
Set<String> tags = Set.of("java", "spring");
```

---

## 3. 自己写泛型类

```java
public class MyBox<T> {                          // T 是类型参数（type parameter）
    private T value;

    public void set(T value) { this.value = value; }
    public T get() { return value; }
}

MyBox<String> sb = new MyBox<>();
sb.set("hello");
String s = sb.get();

MyBox<Integer> ib = new MyBox<>();
ib.set(42);
```

**惯例**
- `T` —— Type
- `E` —— Element（集合元素）
- `K`, `V` —— Key, Value
- `R` —— Result

---

## 4. 泛型方法

```java
public class Util {
    // <T> 在返回类型前面声明
    public static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}

String s = Util.firstOrNull(List.of("a", "b"));
Integer i = Util.firstOrNull(List.of(1, 2, 3));
```

---

## 5. 类型擦除：泛型的真相

Java 的泛型是**编译期检查**，编译后**类型信息会被擦除**：

```java
List<String> a = new ArrayList<>();
List<Integer> b = new ArrayList<>();
a.getClass() == b.getClass()       // true ！运行时都是 ArrayList
```

**这导致几个限制**
- 不能 `new T()`（运行时不知道 T 是什么）
- 不能 `instanceof List<String>`（只能写 `instanceof List`）
- 一个类不能同时有 `void f(List<String>)` 和 `void f(List<Integer>)`（擦除后签名一样）

实操层面这些限制不太碰到，知道有这回事即可。

---

## 6. 集合框架全览

```
Collection                          ← 顶层接口
  ├── List      允许重复、有序     → ArrayList / LinkedList
  ├── Set       不允许重复          → HashSet / LinkedHashSet / TreeSet
  └── Queue     队列                → ArrayDeque / PriorityQueue

Map           键值对（独立体系）   → HashMap / LinkedHashMap / TreeMap
```

### 6.1 List：99% 用 ArrayList

```java
List<String> names = new ArrayList<>();
names.add("Alice");                       // 末尾追加
names.add(0, "Bob");                       // 指定位置插入
String first = names.get(0);
names.set(0, "Carol");                     // 替换
names.remove(0);                           // 按索引删
names.remove("Alice");                     // 按值删
boolean has = names.contains("Alice");
int size = names.size();
for (String n : names) { ... }             // 遍历
```

**ArrayList vs LinkedList**

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| `get(i)` | O(1) | O(n) |
| 末尾 `add` | 摊销 O(1) | O(1) |
| 中间 `add` / `remove` | O(n) | O(1) 已定位 |
| 内存 | 紧凑 | 每个节点带指针 |

**实操**：99% 选 `ArrayList`，除非确认要在头部/中间频繁插入删除。

### 6.2 Set：不允许重复

```java
Set<String> tags = new HashSet<>();
tags.add("java");
tags.add("java");              // 第二次 add 没效果
tags.contains("java");         // true

// 想保持插入顺序
Set<String> ordered = new LinkedHashSet<>();

// 想自动排序
Set<Integer> sorted = new TreeSet<>();
sorted.add(3); sorted.add(1); sorted.add(2);
// 遍历输出 1, 2, 3
```

### 6.3 Map：键值对

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 88);

Integer s = scores.get("Alice");           // 95
Integer x = scores.get("Charlie");          // null（不存在）
Integer y = scores.getOrDefault("Charlie", 0);   // 0

scores.containsKey("Alice");                // true
scores.size();

// 遍历方式 1：entrySet（最常用，最高效）
for (Map.Entry<String, Integer> e : scores.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}

// 遍历方式 2：forEach + lambda
scores.forEach((k, v) -> System.out.println(k + " -> " + v));
```

**三种 Map 实现的差异**

| 类 | 遍历顺序 | 内部结构 | 何时用 |
|----|----------|----------|--------|
| `HashMap` | 不定 | 哈希表 | **默认选这个** |
| `LinkedHashMap` | 插入顺序 | 哈希 + 链表 | 想保留 put 顺序 |
| `TreeMap` | 按 key 排序 | 红黑树 | key 需要有序遍历 |

---

## 7. 遍历集合：3 种姿势

```java
List<String> list = List.of("a", "b", "c");

// 1. for-each（最常用）
for (String s : list) { ... }

// 2. forEach + lambda（Java 8+）
list.forEach(s -> System.out.println(s));
list.forEach(System.out::println);          // 方法引用，更简洁

// 3. 经典 for（当需要索引）
for (int i = 0; i < list.size(); i++) {
    System.out.println(i + ": " + list.get(i));
}
```

---

## 8. 集合 → 数组、数组 → 集合

```java
// List → array
List<String> list = List.of("a", "b", "c");
String[] arr = list.toArray(new String[0]);

// array → List
String[] arr2 = {"a", "b", "c"};
List<String> list2 = Arrays.asList(arr2);          // 返回固定大小 List（不能 add）
List<String> list3 = new ArrayList<>(Arrays.asList(arr2));  // 可变
```

---

## 9. 遍历时修改：要么用 Iterator，要么用 removeIf

```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));

// ❌ 这样会抛 ConcurrentModificationException
for (String s : list) {
    if (s.equals("b")) list.remove(s);
}

// ✅ 用 Iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("b")) it.remove();
}

// ✅ 更简洁：removeIf（Java 8+）
list.removeIf(s -> s.equals("b"));
```

---

## 10. 自查

- [ ] 不查文档写一个泛型方法 `<T> T lastOf(List<T> list)`，返回最后一个元素
- [ ] 解释为什么 `List<String>` 不能 `add(123)`（编译期类型检查）
- [ ] 用 HashMap 完成"统计一段文字里每个单词出现次数"
- [ ] 用 LinkedHashSet 实现"按插入顺序的去重"
- [ ] 用 TreeMap 完成"按 key 升序遍历"
- [ ] 解释为什么 `remove` 在 for-each 里会抛异常（迭代器并发修改检测）

## 代码示例

→ [`code/week2/generic/MyStack.java`](../../code/week2/generic/MyStack.java)
→ [`code/week2/collections/CollectionsDemo.java`](../../code/week2/collections/CollectionsDemo.java)
