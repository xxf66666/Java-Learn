# Week 1.5 §01 · 数组与可变参数

> 数组是 Java 最底层的容器；`Arrays` 工具类和 `varargs` 是日常用得到的常识。

---

## 1. 一维数组

### 创建数组的 3 种方式

```java
// 方式 1：声明 + new + 默认值
int[] a = new int[5];               // 长度 5，元素默认 0
String[] s = new String[3];          // 默认 null
boolean[] b = new boolean[3];        // 默认 false

// 方式 2：花括号初始化
int[] arr = {1, 2, 3, 4, 5};

// 方式 3：new + 花括号（必须显式写，不能省 new int[]）
int[] arr2 = new int[]{1, 2, 3};

// 错误写法对照
int[] x;
x = {1, 2, 3};                       // ❌ 编译错误：不能用 {} 给已声明的变量赋值
x = new int[]{1, 2, 3};              // ✅
```

### 基本操作

```java
int[] arr = {10, 20, 30};
arr.length          // 长度（属性，无括号！不是 length()）
arr[0]              // 取第 0 个
arr[0] = 99;        // 赋值

// 越界抛 ArrayIndexOutOfBoundsException
arr[10]             // ❌ 运行时崩
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

---

## 2. 多维数组（数组的数组）

```java
// 二维数组：3 行 4 列
int[][] grid = new int[3][4];
grid[1][2] = 99;

// 字面量
int[][] m = {
    {1, 2, 3},
    {4, 5, 6}
};

// "锯齿数组"（每行长度不同）
int[][] jagged = new int[3][];       // 只指定行数
jagged[0] = new int[]{1};
jagged[1] = new int[]{1, 2};
jagged[2] = new int[]{1, 2, 3};

// 二维遍历
for (int i = 0; i < m.length; i++) {
    for (int j = 0; j < m[i].length; j++) {
        System.out.print(m[i][j] + " ");
    }
    System.out.println();
}
```

---

## 3. Arrays 工具类

`java.util.Arrays` 提供常用数组操作：

```java
import java.util.Arrays;

int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};

// 排序（升序）
Arrays.sort(arr);                            // 原地排，arr 变成 [1,1,2,3,4,5,6,9]

// 复制
int[] copy = Arrays.copyOf(arr, 5);          // 截前 5 个
int[] slice = Arrays.copyOfRange(arr, 2, 5); // 索引 [2, 5)

// 填充
int[] zeros = new int[10];
Arrays.fill(zeros, 7);                       // 全部填 7

// 二分查找（必须先排序）
int idx = Arrays.binarySearch(arr, 4);       // 返回 4 的索引

// 比较
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};
a == b                       // false（不同对象）
Arrays.equals(a, b)          // true ✅

// 转字符串（用来 print 数组）
System.out.println(arr);                     // [I@1234abcd 这种鬼东西
System.out.println(Arrays.toString(arr));    // [1, 1, 2, 3, 4, 5, 6, 9] ✅

// 二维数组用 deepToString
int[][] m = {{1,2},{3,4}};
System.out.println(Arrays.deepToString(m));  // [[1, 2], [3, 4]]

// 数组转 List
List<String> list = Arrays.asList("a", "b", "c");
// ⚠️ 返回的 List 是固定大小，不能 add / remove

// 数组转 Stream
IntStream.of(arr).sum();                     // 求和
```

---

## 4. 可变参数 varargs

让方法接受"任意多个同类型参数"：

```java
public static int sum(int... nums) {       // 三个点 ...
    // nums 在方法体内就是 int[] 数组
    int total = 0;
    for (int n : nums) total += n;
    return total;
}

// 调用
sum(1, 2, 3);                  // 6
sum();                          // 0
sum(1);                         // 1
sum(new int[]{1, 2, 3});       // 6（也可以传数组）
```

### 规则

- 一个方法**最多一个** varargs 参数
- varargs **必须放在参数列表最后**

```java
void f(String prefix, int... nums) { ... }   // ✅
void f(int... nums, String suffix) { ... }   // ❌
```

### 调用时类型推断的坑

```java
public static void log(Object... args) { ... }

String[] arr = {"a", "b"};
log(arr);            // 实际 args = arr（String[] 是 Object[]，被当成 varargs 解开）
log((Object) arr);   // 强制转 Object，args 长度 1，元素是 arr
```

写 SLF4J 日志时如果只想传一个数组，要 `(Object) arr` 包一下。

---

## 5. 数组 vs ArrayList 对比

| 特性 | 数组 `int[]` | `ArrayList<Integer>` |
|------|--------------|---------------------|
| 大小 | 固定 | 动态 |
| 类型 | 支持基本类型 | 只支持引用类型（int → Integer） |
| 长度 | `.length` | `.size()` |
| 取值 | `arr[i]` | `list.get(i)` |
| 赋值 | `arr[i] = x` | `list.set(i, x)` |
| 末尾追加 | 不支持 | `list.add(x)` |
| 删除 | 不支持 | `list.remove(i)` |
| 内存紧凑 | ✅ | 比数组慢 |
| 工作中常用 | 偶尔 | **几乎都用这个** |

**实操原则**：90% 场景用 `ArrayList`，只有性能极敏感或 API 要求时用数组。

---

## 6. 自查

- [ ] 创建并遍历一个 3x4 的二维数组
- [ ] 用 `Arrays.sort` 排序、`Arrays.toString` 打印
- [ ] 写一个接受 varargs 的 `max(int...)` 方法
- [ ] 解释为什么 `Arrays.asList(...)` 返回的 List 不能 add
- [ ] 把 `int[]` 和 `Integer[]` 各打印一遍，看 `Arrays.toString` 的输出

## 代码示例

→ [`code/week1.5/arrays/ArrayDemo.java`](../../code/week1.5/arrays/ArrayDemo.java)
