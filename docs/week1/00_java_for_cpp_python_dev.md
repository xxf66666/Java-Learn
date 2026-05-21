# Week 1 §00 · 给 C++/Python 老兵的 Java 速通对照

> 目标：把已有的 C++/Python 知识"翻译"到 Java，1 小时建立基本认知，知道哪里是新东西、哪里可以跳过。

---

## 1. 心智模型：Java 在三者中的位置

| 维度 | C++ | Python | Java |
|------|-----|--------|------|
| 类型系统 | 静态强类型 | 动态强类型 | **静态强类型** |
| 内存管理 | 手动 + RAII | GC + 引用计数 | **GC（自动）** |
| 编译执行 | 编译为机器码 | 解释执行 | **编译为字节码 + JVM 执行** |
| 指针 | 有指针/引用 | 全是引用（万物皆对象） | **全是引用，无指针运算** |
| 继承 | 多继承 | 多继承（MRO） | **单继承类 + 多实现接口** |
| 运算符重载 | 支持 | 支持（魔术方法） | **不支持** |
| 模板/泛型 | 模板（编译期展开） | 鸭子类型 | **泛型（运行时擦除）** |
| 标准容器 | STL（`vector` / `map` / ...） | `list` / `dict` / `set` | **集合框架（`ArrayList` / `HashMap` / ...）** |

**一句话总结**：Java ≈ "C++ 的语法 + Python 的全引用心态 + 自己的 GC 和接口设计"。

---

## 2. 语法对照速查（90% 是 C++ 翻译）

### 2.1 变量和基本类型

```java
// Java
int a = 10;
long b = 100_000_000L;       // 长整型加 L
double c = 3.14;
boolean flag = true;          // 不是 bool！
char ch = 'A';                // 单引号 char，双引号 String
String s = "hello";           // String 是类（首字母大写），不是基本类型
```

| C++ | Python | Java | 备注 |
|-----|--------|------|------|
| `int` (32 位通常) | `int` (任意精度) | `int` (固定 32 位) | Java 没有无符号 |
| `long long` | 同 int | `long` (64 位) | 字面量加 `L` |
| `double` | `float` | `double` | 字面量默认 double |
| `bool` | `bool` | `boolean` | **名字不一样** |
| `char` (1B) | `str` (1 字符) | `char` (2B, Unicode) | Java 的 char 是 UTF-16 |
| `std::string` | `str` | `String` | 不可变（immutable） |

### 2.2 控制流：和 C++ 几乎一致

```java
if (a > 0) { ... } else if (...) { ... } else { ... }

for (int i = 0; i < 10; i++) { ... }

for (String name : names) { ... }      // 增强 for（类似 Python for x in xs）

while (cond) { ... }

switch (status) {
    case 1: ...; break;
    case 2: ...; break;
    default: ...;
}
```

**Python 用户注意**：Java 必须用 `{ }` 而不是缩进；条件必须是 `boolean` 而不是 truthy（`if (list)` 在 Java 是非法的，要写 `if (!list.isEmpty())`）。

### 2.3 函数（Java 叫"方法"）

```java
// 必须在类内部，没有自由函数
public class MathUtil {
    public static int add(int a, int b) {
        return a + b;
    }
}

int result = MathUtil.add(1, 2);
```

- 没有默认参数（用方法重载替代）
- 没有关键字参数（Builder 模式 / Lombok `@Builder` 替代）
- 没有自由函数（一切都在类里；可以用 `static` 模拟）

### 2.4 数组 vs 集合

```java
// 原生数组（固定长度，类似 C++ 数组）
int[] arr = new int[10];
int[] arr2 = {1, 2, 3};

// 动态数组（实际工作 99% 用这个）
List<Integer> list = new ArrayList<>();
list.add(1);
list.add(2);
int x = list.get(0);
int size = list.size();          // 不是 list.size！
```

| C++ STL | Python | Java |
|---------|--------|------|
| `std::vector<T>` | `list` | `ArrayList<T>` |
| `std::list<T>` | `collections.deque` | `LinkedList<T>` |
| `std::map<K,V>` | `dict` (有序，3.7+) | `HashMap<K,V>` (无序) / `LinkedHashMap` (插入序) / `TreeMap` (key 有序) |
| `std::set<T>` | `set` | `HashSet<T>` / `TreeSet<T>` |
| `std::queue` | `queue.Queue` | `Queue` / `ArrayDeque` |

---

## 3. 引用语义：所有非基本类型都是引用

这是 Java 最容易让 C++ 老兵绊倒的地方。

```java
Student s1 = new Student("Tom");
Student s2 = s1;                  // 这是引用赋值，不是拷贝！
s2.setName("Jerry");
System.out.println(s1.getName()); // 输出 Jerry！！
```

> Python 用户秒懂（和 Python 行为一致）。C++ 用户类比 `Student* s2 = s1;`。

**`==` 比较的是引用，不是内容**

```java
String a = new String("hello");
String b = new String("hello");
a == b              // false（两个不同的对象）
a.equals(b)         // true（内容相等）
```

> 字符串字面量有特殊优化（字符串常量池），`String a = "hello"; String b = "hello";` 时 `a == b` 是 true。**记住一条规则：永远用 `.equals()`**。

---

## 4. 类与对象：和 C++ 大同小异

```java
public class Student {
    private String name;          // 字段
    private int age;

    public Student(String name, int age) {  // 构造器（不写返回类型，方法名 = 类名）
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }     // getter
    public void setName(String name) { this.name = name; } // setter

    @Override
    public String toString() {
        return "Student(" + name + ", " + age + ")";
    }
}
```

**与 C++ 的差异**
- 没有头文件，类定义和实现写一起
- 没有 `~Destructor()`（GC 管），用 `try-with-resources` + `AutoCloseable` 释放资源
- 没有运算符重载、没有友元
- 默认 **动态绑定**（C++ 要 `virtual`，Java 默认就是）
- 想禁止重写要加 `final`（类似 C++ 的 `final`）

**与 Python 的差异**
- 必须声明字段类型
- 没有 `__init__`，构造器名 = 类名
- 没有 `self`，用 `this`（且可以省略）
- 字段默认 `private`，方法通过 getter/setter 访问（IDE 一键生成 / Lombok `@Data`）

---

## 5. 继承与接口：单继承 + 多实现

```java
// 抽象类（部分实现）
public abstract class Animal {
    protected String name;
    public abstract void sound();        // 抽象方法
    public void sleep() { ... }          // 普通方法
}

// 接口（只有签名，Java 8+ 可有 default 方法）
public interface Swimmer {
    void swim();
    default void floatOnWater() { ... }  // default 方法
}

// 继承 + 实现
public class Duck extends Animal implements Swimmer {
    @Override public void sound() { System.out.println("Quack"); }
    @Override public void swim() { System.out.println("Swimming"); }
}
```

**C++ 心态调整**：Java 没有多继承（避免菱形问题），但接口可以多个；接口比抽象类更"轻"，没有状态。

**Python 心态调整**：Java 强制 `@Override` 注解（编译器检查），没有鸭子类型 —— 必须显式声明 `implements`。

---

## 6. 异常：checked vs unchecked

```java
// Checked：必须 try-catch 或 throws 声明（编译期检查）
public void readFile() throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader("a.txt"));
    // ...
}

// Unchecked：可以不处理（运行时炸）
public void divide(int x, int y) {
    int r = x / y;  // y=0 时抛 ArithmeticException，编译不强制处理
}

// try-with-resources（Java 7+，自动关闭资源 ≈ Python with）
try (BufferedReader r = new BufferedReader(new FileReader("a.txt"))) {
    String line = r.readLine();
} catch (IOException e) {
    log.error("read failed", e);
}
```

| 类型 | 父类 | 例子 | 必须处理？ |
|------|------|------|-----------|
| Error | `Throwable` | `OutOfMemoryError` | 不处理（致命） |
| RuntimeException | `Exception` | `NullPointerException` / `IllegalArgumentException` | 否（unchecked） |
| 其他 Exception | `Exception` | `IOException` / `SQLException` | **是（checked）** |

**Python 用户注意**：Python 所有异常都是 unchecked，Java 的 checked exception 是独有的——你写 IO / SQL 时会被强制 try-catch。

---

## 7. 泛型：擦除式（运行时没有类型信息）

```java
List<String> list = new ArrayList<>();   // 菱形语法，Java 7+ 编译器推断
list.add("a");
// list.add(1);   // 编译错误：类型不匹配

// 编译后，List<String> 和 List<Integer> 在运行时是同一个 List
```

**和 C++ 模板的差异**：C++ 模板是编译期展开（每个类型一份代码），Java 泛型是**类型擦除**（运行时只有一份代码，所有泛型变成 `Object`），所以不能 `new T()`、不能 `instanceof T`。

**和 Python 的差异**：Python 没有真泛型，靠鸭子类型；Java 在编译期强检查。

---

## 8. Lambda 和 Stream（Java 8+，类比 Python 列表推导）

```java
// 传统写法
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<String> upper = new ArrayList<>();
for (String n : names) {
    if (n.length() > 3) upper.add(n.toUpperCase());
}

// Stream 写法（推荐）
List<String> upper2 = names.stream()
    .filter(n -> n.length() > 3)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

Python 类比：
```python
upper = [n.upper() for n in names if len(n) > 3]
```

Stream 是惰性求值、可并行（`.parallelStream()`），Week 3 详细学。

---

## 9. 一张"如果你想做 X，在 Java 怎么做"速查表

| 你想做的事 | C++ | Python | **Java** |
|-----------|-----|--------|----------|
| 打印 | `cout << x` | `print(x)` | `System.out.println(x)` |
| 字符串拼接 | `s1 + s2` | `s1 + s2` 或 f-string | `s1 + s2` 或 `String.format()` 或 `"%s".formatted(x)` |
| 字符串拼接（循环里） | `std::stringstream` | `''.join(...)` | **`StringBuilder`**（性能差异大！） |
| 读一行输入 | `getline(cin, s)` | `input()` | `new Scanner(System.in).nextLine()` |
| 读文件 | `ifstream` | `with open() as f` | `Files.readAllLines(Path.of("a.txt"))` |
| 当前时间 | `chrono::system_clock` | `datetime.now()` | `LocalDateTime.now()`（Java 8+，旧的 `Date` 已弃用） |
| HTTP 请求 | `libcurl` | `requests` | `HttpClient`（JDK 11+）或 `RestTemplate`/`WebClient`（Spring） |
| JSON 序列化 | `nlohmann/json` | `json.dumps()` | **Jackson**（Spring 默认）/ Gson |
| 单测 | `gtest` | `pytest` | **JUnit 5** |
| 包管理 | CMake / vcpkg | pip / poetry | **Maven** / Gradle |
| 调试 | gdb | pdb | **IDEA Debugger**（一流） |

---

## 10. 常见踩坑清单

1. **`==` vs `.equals()`** —— 对象比较永远用 `.equals()`
2. **`String` 不可变** —— 循环里拼字符串用 `StringBuilder`，否则 O(n²)
3. **`int` 不能为 null** —— 想要可空整数用 `Integer`（包装类型）
4. **`null` 指针** —— Java 没有 Optional 强制，记得校验或用 `Optional<T>`
5. **集合不能在遍历时修改** —— 用 `Iterator.remove()` 或 `removeIf()`
6. **`Integer` 比较** —— `Integer.valueOf(128) == Integer.valueOf(128)` 是 `false`（超出缓存范围）！用 `.equals()` 或 `.intValue() ==`
7. **泛型不能用基本类型** —— `List<int>` 非法，用 `List<Integer>`
8. **静态方法不能直接访问实例字段** —— 和 C++ 一致
9. **构造器调用顺序** —— 父类构造器先执行，`super()` 默认隐式调用
10. **包名 = 目录** —— `package com.foo.bar;` 的文件必须放在 `com/foo/bar/` 下

---

## 下一步

→ [`01_setup.md`](01_setup.md)：JDK 21 + IntelliJ IDEA 安装配置
→ [`02_first_program.md`](02_first_program.md)：在 IDEA 里跑通第一个 HelloWorld，理解项目结构
→ 代码示例：[`../../code/week1/SyntaxCheatsheet.java`](../../code/week1/SyntaxCheatsheet.java)
