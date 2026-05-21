# Week 1 §00 · 从 Python 视角学 Java（兼顾少量 C++ 类比）

> 目标：用 1-2 小时，把已经熟悉的 Python 思维"翻译"到 Java，知道哪些是新东西、哪些可以快速带过。
>
> 阅读说明：本文以 **Python 为主类比**，C++ 类比作为补充。遇到 C++ 概念会用一句话解释，不假定你懂。

---

## 1. 大局观：Java 是什么定位

| 维度 | Python | Java |
|------|--------|------|
| 类型系统 | 动态强类型（变量类型自动推断） | **静态强类型**（变量必须先声明类型） |
| 内存管理 | GC（自动回收） | **GC**（自动回收，和 Python 一样） |
| 编译执行 | 解释执行 | **编译成字节码 → JVM 执行**（速度比 Python 快很多） |
| 一切皆对象？ | 是 | 几乎是 —— 但有 8 个"基本类型"（`int` / `double` / `boolean` 等）是值，不是对象 |
| 缩进决定语法 | 是 | **否，用 `{ }` 包代码块，每行末加 `;`** |
| 入口 | 任意 .py 文件可以 `python xxx.py` 直接跑 | 必须有 `public static void main(String[] args)` 方法 |

一句话理解：**Java 像"必须先声明类型的 Python"**，加上一套企业级工程化习惯（强类型 + 编译 + JVM + 工厂/容器思想）。

**那个 C++ 是什么角色？** Java 的语法风格（`{ }` 块、`;` 结尾、`int x = 10;`）抄自 C/C++。Python 的语法（缩进、动态类型）则差异较大。所以本文偶尔说"和 C++ 相似"，意思是"长得像 C++"——你不用真懂 C++。

---

## 2. 第一段对照：变量和打印

```python
# Python
name = "Alice"
age = 25
height = 1.65
is_student = True

print(f"{name}, {age} 岁, {height}m, 学生={is_student}")
```

```java
// Java
String name = "Alice";          // 类型 String 必须写
int age = 25;                   // 整数用 int
double height = 1.65;           // 浮点用 double
boolean isStudent = true;       // 布尔用 boolean（注意名字，不是 bool）

System.out.printf("%s, %d 岁, %.2fm, 学生=%b%n", name, age, height, isStudent);
//   ^^^^^^^^^^^^^^^^ 相当于 Python 的 print，但是要写全
//   %n 是换行（跨平台），也可以用 \n
```

**关键差异**
- 每个变量前面要写类型（**编译器借此帮你抓 bug**）
- 每行末尾要 `;`
- 字符串字面量必须用**双引号** `"hello"`；单引号 `'A'` 是单个字符（`char`）
- Python `f"{name}"` 在 Java 是 `String.format()` 或 `"...".formatted()`

---

## 3. 基本类型：8 个特殊家伙

Python 里 `int` / `float` / `bool` 都是对象（`(1).bit_length()` 可以调方法）。Java 出于性能考虑，有 **8 个基本类型**，它们不是对象：

| 类型 | 含义 | 范围 | 对应的"包装对象" |
|------|------|------|------------------|
| `byte` | 8 位整数 | -128 ~ 127 | `Byte` |
| `short` | 16 位整数 | -32768 ~ 32767 | `Short` |
| `int` | **32 位整数（最常用）** | 约 ±21 亿 | `Integer` |
| `long` | 64 位整数 | 极大 | `Long` |
| `float` | 32 位浮点 | —— | `Float` |
| `double` | **64 位浮点（默认浮点用这个）** | —— | `Double` |
| `boolean` | true / false | —— | `Boolean` |
| `char` | 单个字符（UTF-16） | —— | `Character` |

**实操规则**
- 整数随便选 → 用 `int`（除非数特别大用 `long`）
- 浮点随便选 → 用 `double`
- 想存到集合（`List` / `Map`）里？ → 不能用基本类型，必须用包装类型 `Integer` / `Double` / `Boolean`
- `int` 不能为 `null`；`Integer` 可以为 `null`

---

## 4. 字符串：和 Python 类似，但有几个坑

```java
String s1 = "hello";
String s2 = "world";

// 拼接：用 +（编译器会自动优化）
String s3 = s1 + " " + s2;          // "hello world"

// 类似 Python f-string 的格式化
String s4 = String.format("%s is %d 岁", "Alice", 25);
String s5 = "%s is %d 岁".formatted("Alice", 25);   // Java 15+

// 长度：方法调用，要带括号
int len = s3.length();              // ⚠️ 不是 s3.length

// 是否包含
boolean ok = s3.contains("world");  // true

// 切片：substring(开始, 结束)，左闭右开（和 Python 切片规则一样）
String sub = s3.substring(0, 5);    // "hello"
```

### 坑 1：循环里拼字符串必须用 `StringBuilder`

```java
// ❌ 慢（每次 + 都新建一个 String 对象，O(n²)）
String s = "";
for (int i = 0; i < 1000; i++) s = s + i;

// ✅ 快
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i);
String s = sb.toString();
```

> Python 的 `''.join(...)` 对应 Java 的 `String.join(",", list)`。

### 坑 2：判断字符串相等用 `.equals()`，不能用 `==`

```java
String a = new String("hello");
String b = new String("hello");

a == b           // false ！（== 比较的是"是不是同一块内存"）
a.equals(b)      // true  ✅ 永远用这个比较内容
```

Python 里 `==` 默认比较值，Java 里 `==` 默认比较"是不是同一个对象"。**记住一条**：除了基本类型（`int` / `double` 等），对象比较一律用 `.equals()`。

---

## 5. 控制流：和 Python 几乎一样

```java
// if
if (score >= 90) {
    System.out.println("A");
} else if (score >= 80) {
    System.out.println("B");
} else {
    System.out.println("C");
}

// for（C 风格，类似 Python 的 for i in range(10)）
for (int i = 0; i < 10; i++) {
    System.out.println(i);
}

// for-each（类似 Python for x in xs）
int[] nums = {1, 2, 3};
for (int n : nums) {
    System.out.println(n);
}

// while
while (condition) { ... }

// switch（Java 14+ 表达式版，强烈推荐）
String grade = switch (score / 10) {
    case 10, 9 -> "A";
    case 8 -> "B";
    case 7 -> "C";
    default -> "D";
};
```

**Python 没有的 / 必须重新养成的习惯**
- 条件必须是 `boolean`：`if (list)` 在 Python 是判非空，在 Java **非法**，要写 `if (!list.isEmpty())`
- 没有 `elif`，写成 `else if`
- 用 `{ }`，不用缩进

---

## 6. 集合：Python `list` / `dict` / `set` 的 Java 对应

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// list → ArrayList
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
String first = names.get(0);
int n = names.size();              // ⚠️ 不是 len()
boolean has = names.contains("Alice");

// dict → HashMap
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 88);
int s = scores.get("Alice");
boolean exist = scores.containsKey("Alice");

// set → HashSet
Set<String> tags = new HashSet<>();
tags.add("java");
tags.add("python");
```

**对照表**

| Python | Java | 备注 |
|--------|------|------|
| `list` | `ArrayList<T>` | 工作中 99% 用这个 |
| `dict` | `HashMap<K, V>` | 无序；要保持插入序用 `LinkedHashMap`；要按 key 排序用 `TreeMap` |
| `set` | `HashSet<T>` / `TreeSet<T>` | 同上 |
| `tuple` | 没有原生 tuple | 用 `record` (Java 16+) 或自定义类 |
| `len(x)` | `x.size()` 或 `x.length()`（字符串）或 `x.length`（数组） | 不统一，遗憾的设计 |
| `x in xs` | `xs.contains(x)` | |
| `xs.append(x)` | `xs.add(x)` | |
| `del xs[i]` | `xs.remove(i)` | |

**`<String>` 是什么**？ 这是**泛型**，告诉编译器"这是一个只装字符串的 List"。Python 里 `list` 可以装任何东西，Java 里要先约定类型——编译器靠这个抓 bug。

---

## 7. 引用语义：Java 易踩坑

```java
// 基本类型：值传递（拷贝一份）
int a = 10;
int b = a;
b = 20;
System.out.println(a);    // 10，没变

// 引用类型：传引用（指向同一对象）
int[] arr1 = {1, 2, 3};
int[] arr2 = arr1;        // 没拷贝！两个名字指向同一个数组
arr2[0] = 999;
System.out.println(arr1[0]);   // 999 ！
```

Python 完全一致（Python 也是引用语义），所以你应该不陌生。**真正的坑**是 Java 里有 `int` / `Integer` 两种整数：

```java
int x = 100;
int y = 100;
x == y       // true（基本类型，比较值）

Integer p = 100;
Integer q = 100;
p == q       // 100 在缓存范围内，居然 true（巧合）

Integer m = 200;
Integer n = 200;
m == n       // false ！（不在缓存范围，是两个对象）
m.equals(n)  // true  ✅
```

> **铁律**：对象比较永远用 `.equals()`，不要用 `==`。

---

## 8. 函数（Java 叫"方法"）：必须在类里

```python
# Python：函数可以自由定义
def add(a, b):
    return a + b

print(add(1, 2))
```

```java
// Java：必须在类里
public class MathUtil {
    public static int add(int a, int b) {
        return a + b;
    }
}

// 使用
int result = MathUtil.add(1, 2);
```

**Python 和 Java 的差异**

| 特性 | Python | Java |
|------|--------|------|
| 自由函数 | ✅ | ❌（必须在类里） |
| 默认参数 | ✅ `def f(x=10)` | ❌（用方法重载替代） |
| 关键字参数 | ✅ `f(x=1, y=2)` | ❌（用 Builder 模式） |
| 类型提示 | 可选 `def f(x: int) -> int` | **强制** `int f(int x)` |
| `self` | 必须显式写 | Java 用 `this`，且可省略 |

**方法重载（Java 用来代替 Python 默认参数）**

```java
public static int add(int a, int b) { return a + b; }
public static double add(double a, double b) { return a + b; }
public static int add(int a, int b, int c) { return a + b + c; }
// 调用时编译器根据参数类型/个数自动选
```

---

## 9. 类：Python 类的 Java 翻译

```python
# Python
class Student:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def greet(self):
        return f"Hi, I'm {self.name}"

s = Student("Alice", 20)
print(s.greet())
```

```java
// Java
public class Student {
    private String name;              // 字段
    private int age;

    // 构造器：名字 = 类名，没有返回类型
    public Student(String name, int age) {
        this.name = name;             // this 类似 Python 的 self
        this.age = age;
    }

    public String greet() {
        return "Hi, I'm " + this.name;
    }

    // Python 里所有字段是 public 的；Java 习惯：字段 private + 提供 getter/setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// 使用
Student s = new Student("Alice", 20);     // 必须 new
System.out.println(s.greet());
```

**Java 类的几个新概念**

- **访问修饰符**：`public`（哪都能用）/ `private`（只本类）/ `protected`（本类 + 子类 + 同包）/ 默认（同包）
- **getter / setter**：Java 习惯把字段藏起来（`private`），通过方法访问。IDEA 一键生成（`⌘ + N → Getter and Setter`）；学到 Lombok 后 `@Data` 自动加。
- **`new` 关键字**：创建对象必须 `new ClassName(...)`，Python 里 `ClassName(...)` 就够。

---

## 10. 继承和接口

```java
// 抽象类（部分实现）
public abstract class Animal {
    protected String name;
    public abstract void sound();        // 抽象方法，子类必须实现
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
}

// 接口（只有方法签名，Java 8+ 可以有 default 方法）
public interface Swimmer {
    void swim();
}

// 继承一个类 + 实现多个接口
public class Duck extends Animal implements Swimmer {
    @Override                            // 注解：告诉编译器"这是覆盖父方法"
    public void sound() {
        System.out.println("Quack");
    }

    @Override
    public void swim() {
        System.out.println("Duck swimming");
    }
}
```

**关键规则**
- 一个类只能 `extends` 一个父类（Python 可以多继承）
- 一个类可以 `implements` 多个接口（用接口模拟"多继承"）
- 抽象类有部分实现，接口默认只有签名
- `@Override` 让编译器检查你确实覆盖了父方法（强烈建议永远写）

---

## 11. 异常：和 Python 差不多，多了"checked exception"

```python
# Python
try:
    f = open("a.txt")
    data = f.read()
except FileNotFoundError as e:
    print(e)
finally:
    f.close()

# 推荐
with open("a.txt") as f:
    data = f.read()
```

```java
// Java
try {
    BufferedReader r = new BufferedReader(new FileReader("a.txt"));
    String line = r.readLine();
} catch (IOException e) {           // ⚠️ IOException 是 checked 异常，必须 catch
    System.err.println(e.getMessage());
}

// 推荐：try-with-resources（Java 7+，类似 Python with）
try (BufferedReader r = new BufferedReader(new FileReader("a.txt"))) {
    String line = r.readLine();
} catch (IOException e) {
    System.err.println(e.getMessage());
}
```

**checked vs unchecked**
- **checked 异常**（如 `IOException`、`SQLException`）：编译器**强制**你处理（try-catch 或往上 throws）
- **unchecked 异常**（如 `NullPointerException`、`IllegalArgumentException`）：编译器不强制处理（崩了就崩了）

Python 没有这个区分，所有异常都是 unchecked。Java 这一点会让你在写 IO / SQL 时被编译器"逼着" try-catch。

---

## 12. 一张速查表

| 你想做的事 | Python | Java |
|-----------|--------|------|
| 打印 | `print(x)` | `System.out.println(x)` |
| 读输入 | `input()` | `new Scanner(System.in).nextLine()` |
| 字符串格式化 | `f"{x}-{y}"` | `"%s-%d".formatted(x, y)` |
| 列表 | `xs = [1, 2, 3]` | `List<Integer> xs = new ArrayList<>(List.of(1,2,3));` |
| 字典 | `d = {"a": 1}` | `Map<String,Integer> d = new HashMap<>(Map.of("a", 1));` |
| 集合 | `s = {1, 2, 3}` | `Set<Integer> s = new HashSet<>(Set.of(1,2,3));` |
| 长度 | `len(x)` | `x.size()` / `x.length()` / `x.length` (数组) |
| 当前时间 | `datetime.now()` | `LocalDateTime.now()` |
| 读文件 | `with open(p) as f: ...` | `Files.readAllLines(Path.of(p))` |
| HTTP 请求 | `requests.get(url)` | `HttpClient.newHttpClient().send(...)` (JDK 11+) |
| JSON | `json.dumps(obj)` | Jackson `objectMapper.writeValueAsString(obj)` (Spring 默认) |
| 单测 | `pytest` | JUnit 5 |
| 包管理 | `pip` | Maven |

---

## 13. 给现阶段的你：10 条避坑清单

1. **类型必须声明** —— Java 不接受 `x = 10`，必须 `int x = 10;`
2. **每行 `;` 结尾，代码块用 `{ }`** —— 像写 C 一样
3. **字符串比较用 `.equals()`** —— 不要用 `==`
4. **集合不能装基本类型** —— `List<int>` 非法，用 `List<Integer>`
5. **循环拼字符串用 `StringBuilder`** —— 否则慢到怀疑人生
6. **`null` 经常炸** —— 写 `obj.method()` 之前想想 `obj` 可能是 null 吗
7. **`@Override` 一定写** —— 防止你以为覆盖了父方法但其实拼错了名字
8. **`Integer` 和 `int` 不一样** —— `Integer` 可以为 `null`，比较要用 `.equals()`
9. **文件 IO / 数据库 / 网络** 几乎都会抛 `checked exception`，编译器会逼你 try-catch
10. **包名 = 目录** —— `package com.foo.bar;` 的文件必须放在 `com/foo/bar/` 目录下

---

## 下一步

→ [`01_setup.md`](01_setup.md)：JDK 21 + IntelliJ IDEA 安装配置
→ [`02_first_program.md`](02_first_program.md)：在 IDEA 里跑通第一个程序、学会 debug
→ 代码示例：[`../../code/week1/SyntaxCheatsheet.java`](../../code/week1/SyntaxCheatsheet.java)（一段一段对着跑、对着 debug）
