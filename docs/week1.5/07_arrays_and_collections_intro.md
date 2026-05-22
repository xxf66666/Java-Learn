# Week 1.5 §07 · 数组 + 集合初见

> 装数据的容器。数组是最底层的；List 和 Map 是日常 99% 用的。
>
> 本篇是初见，**Week 2 会深入展开**。

---

## 1. 数组：定长容器

### 创建数组的两种写法

```java
// 写法 1：new + 默认值
int[] a = new int[5];               // 长度 5，元素默认 0
String[] s = new String[3];          // String 默认 null

// 写法 2：花括号字面量
int[] arr = {1, 2, 3, 4, 5};
String[] names = {"Alice", "Bob"};
```

### 基本操作

```java
int[] arr = {10, 20, 30};

arr.length              // 3（**属性**不是方法，没有括号！）
arr[0]                  // 10（下标从 0 开始）
arr[0] = 99;             // 赋值
arr[100]                // ❌ 运行时崩：ArrayIndexOutOfBoundsException
```

### 遍历

```java
// 经典 for（需要索引）
for (int i = 0; i < arr.length; i++) {
    System.out.println(i + ": " + arr[i]);
}

// 增强 for（不需要索引）
for (int n : arr) {
    System.out.println(n);
}
```

### 数组的"长度"与"内容"

**数组一旦创建，长度就锁死了**。想"加一个元素"必须新建一个更长的数组。这就是为什么日常用 `ArrayList` 而不是数组。

---

## 2. 二维数组

```java
// 3 行 4 列
int[][] grid = new int[3][4];
grid[1][2] = 99;

// 字面量
int[][] m = {
    {1, 2, 3},
    {4, 5, 6}
};

// 遍历
for (int i = 0; i < m.length; i++) {       // 行数
    for (int j = 0; j < m[i].length; j++) { // 每行的列数
        System.out.print(m[i][j] + " ");
    }
    System.out.println();
}
```

实际上 Java 的"二维数组"是"数组的数组"，每一行可以不一样长（叫"锯齿数组"）。日常不太用，知道有就行。

---

## 3. Arrays 工具类

打印数组用 `println` 会输出像 `[I@1234abcd` 这种乱码。要用 `java.util.Arrays`：

```java
import java.util.Arrays;

int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};

Arrays.toString(arr)                 // "[3, 1, 4, 1, 5, 9, 2, 6]"
Arrays.sort(arr);                    // 原地排序（升序）
Arrays.copyOf(arr, 5);                // 截前 5 个
Arrays.fill(arr, 7);                  // 全部填 7
Arrays.equals(a, b);                  // 比较两个数组内容是否相等

// 二维数组
Arrays.deepToString(matrix);          // 用这个，不要用 toString
```

---

## 4. 可变参数（varargs）

让方法接受"任意多个同类型参数"：

```java
public static int sum(int... nums) {        // 三个点
    // 在方法体内 nums 就是 int[]
    int total = 0;
    for (int n : nums) total += n;
    return total;
}

sum();              // 0
sum(1);             // 1
sum(1, 2, 3);       // 6
sum(new int[]{4, 5, 6});    // 15（也能直接传数组）
```

**规则**
- 一个方法最多一个 varargs 参数
- varargs 必须放在参数列表最后

---

## 5. ArrayList：动态数组（最常用）

数组有定长限制，**实际工作中 90% 场景用 `ArrayList`**：

```java
import java.util.ArrayList;
import java.util.List;        // List 是接口

List<String> names = new ArrayList<>();   // 左用接口、右用实现

names.add("Alice");           // 末尾追加
names.add("Bob");
names.add(0, "Carol");         // 在指定位置插入

names.size()                   // 3（**方法**不是属性！注意和数组的 .length 区别）
names.get(0)                   // "Carol"
names.set(0, "Dave");          // 替换
names.remove(0);                // 按索引删
names.remove("Bob");            // 按值删
names.contains("Alice");        // true / false

// 遍历
for (String name : names) {
    System.out.println(name);
}
```

### `<String>` 是什么

`List<String>` 表示"装 String 的 List"。`<>` 里写元素类型，叫**泛型**（§10 详讲）。

```java
List<Integer> nums = new ArrayList<>();        // 装整数
List<Dog> dogs = new ArrayList<>();            // 装狗
```

**集合只能装对象**：基本类型要装会自动装箱成包装类。所以是 `List<Integer>` 不是 `List<int>`（后者编译报错）。

### List.of(...)：快捷创建不可变 List

```java
List<String> names = List.of("Alice", "Bob", "Carol");
// 注意：返回的 List 是**不可变**的，不能 add / remove，否则抛异常
```

---

## 6. HashMap：键值对（第二常用）

```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> scores = new HashMap<>();    // key 是 String, value 是 Integer

scores.put("Alice", 95);
scores.put("Bob", 88);

scores.get("Alice")                  // 95
scores.get("Carol")                   // null（不存在）
scores.getOrDefault("Carol", 0)       // 0（不存在时给默认值）

scores.containsKey("Alice")           // true
scores.size()                          // 2
scores.remove("Bob");

// 遍历（用 entrySet 性能最好）
for (Map.Entry<String, Integer> e : scores.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}
```

---

## 7. List vs 数组 一表对比

| 特性 | `int[]` | `List<Integer>` |
|------|---------|-----------------|
| 大小 | 固定 | 动态 |
| 元素类型 | 支持基本类型 | 只支持对象（int 要变 Integer） |
| 长度 | `.length` | `.size()` |
| 取元素 | `arr[i]` | `list.get(i)` |
| 末尾追加 | ❌ | `list.add(x)` |
| 工作中用谁 | 偶尔 | **几乎都用这个** |

---

## 8. 配套代码

[`code/week1.5/s07_arrays_collections/CollectionsDemo.java`](../../code/week1.5/s07_arrays_collections/CollectionsDemo.java)

跑一遍看：
- 一维 / 二维数组创建和遍历
- Arrays 工具方法
- varargs 调用
- ArrayList 增删改查
- HashMap 增删改查 + 遍历
- List.of 不可变 List

---

## 9. 自查

- [ ] 写一个二维数组（3×3 乘法表）并打印
- [ ] 用 `Arrays.sort` 排序、`Arrays.toString` 打印
- [ ] 写一个接受 varargs 的 `max(int...)` 方法
- [ ] 用 `ArrayList<String>` 完成"加 3 个名字、删一个、打印"
- [ ] 用 `HashMap<String, Integer>` 统计一句话里每个词出现次数
- [ ] 解释 `List.of(...)` 返回的 List 为什么不能 add
