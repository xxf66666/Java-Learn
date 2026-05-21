# Week 1.5 §03 · 修饰符总览

> Java 的修饰符分两大类：访问修饰符（4 种）+ 非访问修饰符（8+ 种）。本节一次理清。

---

## 1. 访问修饰符（4 种）

| 修饰符 | 本类 | 同包 | 子类 | 其它包 |
|--------|------|------|------|--------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| 不写（默认 / package-private） | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

### 实操惯例

- **类**：`public`（被外界用）；同文件辅助类可不写（默认）
- **字段**：`private`（封装），通过 getter/setter 访问
- **方法**：默认 `public`（被外界调用）；内部辅助方法 `private`
- **构造器**：通常 `public`；单例模式用 `private`

---

## 2. 非访问修饰符

```
static       静态：属于类不属于对象
final        最终：不可改变
abstract     抽象：不能实例化（类）/ 必须被实现（方法）
synchronized 同步：方法/代码块加锁
volatile     可见：保证多线程间字段可见性
transient    瞬态：序列化时跳过这个字段
native       本地：方法用 C/C++ 实现，JNI 调用
strictfp     严格浮点：跨平台精度一致（Java 17 后默认）
```

下面挑常用的细讲。

---

## 3. `static`

**类级别**，不属于具体对象：

```java
public class Counter {
    static int totalCount = 0;        // 静态字段：全类共享
    int instanceCount = 0;             // 实例字段：每个对象一份

    static void show() {               // 静态方法
        // ❌ 这里不能访问 instanceCount（没 this）
        System.out.println("total = " + totalCount);
    }

    void incr() {                       // 实例方法
        totalCount++;                   // ✅ 实例方法可访问静态字段
        instanceCount++;
    }
}

// 用法
Counter.totalCount;          // 用类名访问
Counter.show();              // 用类名调用静态方法

Counter c = new Counter();   // 实例方法 / 字段必须先 new
c.incr();
c.instanceCount;
```

### 静态初始化块

```java
public class Config {
    static final Map<String, String> ENV = new HashMap<>();

    // 类被加载时执行一次，按代码顺序
    static {
        ENV.put("host", "localhost");
        ENV.put("port", "8080");
    }
}
```

### static 常用场景

- 工具类（`Math` / `Files` / `Arrays`）
- 常量（`public static final int MAX = 100;`）
- 单例引用
- 计数器、缓存

---

## 4. `final`

**"最终"，不能再改**。用在三个地方含义不同：

### 4.1 final 字段

```java
public class Point {
    final int x;     // 一旦赋值不能改
    final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // x = 100 → ❌ 编译错误
}
```

**注意**：final 引用是"指针不能换"，但**对象内容能改**。

```java
final List<String> list = new ArrayList<>();
list.add("a");       // ✅ 可以改内容
list = new ArrayList<>();   // ❌ 不能换引用
```

### 4.2 final 方法

```java
public class Animal {
    public final void run() { ... }    // 子类不能 @Override
}
```

防止子类乱改父类的关键行为。

### 4.3 final 类

```java
public final class String { ... }   // 不能被继承
```

JDK 里 `String` / `Integer` 等基础类都是 final，保证它们的行为不被改变。

### final + static = 常量

```java
public class HttpStatus {
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
}
```

约定常量名**全大写下划线分隔**。

---

## 5. `abstract`

### abstract 类

```java
public abstract class Shape {        // 不能 new Shape(...)
    public abstract double area();   // 抽象方法，没有方法体
    public void describe() { ... }    // 普通方法
}
```

### abstract 方法

- 只能在 abstract 类或 interface 里
- 没有方法体（`;` 结尾）
- 子类必须实现（除非子类也是 abstract）

---

## 6. `synchronized`

**线程同步**，同一时刻只允许一个线程进入。

```java
// 加在实例方法上：锁 this 对象
public synchronized void incr() { ... }

// 加在静态方法上：锁 Class 对象
public static synchronized void global() { ... }

// 同步代码块：明确锁对象
public void doSomething() {
    synchronized (this) {
        // 临界区
    }
}
```

详见 Week 3。

---

## 7. `volatile`

**保证多线程间字段可见性**（一个线程改了，其它线程立刻看到），但**不保证原子性**。

```java
class Worker {
    private volatile boolean running = true;

    public void stop() { running = false; }

    public void run() {
        while (running) { /* work */ }   // 不加 volatile 时，可能永远看不到 running 变成 false
    }
}
```

**注意**：`volatile` 不能替代锁做计数器（`x++` 不是原子操作）。

---

## 8. `transient`

序列化时**跳过这个字段**。

```java
public class User implements Serializable {
    String name;
    transient String password;   // 序列化时不写入
}
```

实操：敏感字段（密码、token）一定标 transient。

---

## 9. `native`

方法体用 C/C++ 实现，通过 JNI 调用。

```java
public class Foo {
    public native void doNative();   // 没有方法体
}
```

工作中**基本不会写**，看到能认就行。`System.currentTimeMillis()` 等底层方法是 native。

---

## 10. 综合表格

```
类的修饰符：
  public / 默认 / final / abstract / sealed

方法的修饰符：
  public / protected / 默认 / private
  static / final / abstract / synchronized / native

字段的修饰符：
  public / protected / 默认 / private
  static / final / volatile / transient
```

注意：
- `abstract` 和 `final` 互斥（抽象就是为了被覆盖，final 不让覆盖）
- `abstract` 和 `static` 互斥（抽象方法要被覆盖，static 方法不能被覆盖）
- `abstract` 和 `private` 互斥
- 接口的方法默认 `public abstract`，字段默认 `public static final`

---

## 11. 自查

- [ ] 写一个有 `static final int MAX` 常量的类
- [ ] 解释 final 字段、方法、类各自禁止了什么
- [ ] 写一个 `static` 工具方法 + 用类名调用
- [ ] 解释 `volatile` 和 `synchronized` 各自解决什么
- [ ] 给一个含密码的 Serializable 类的密码字段加 `transient`

## 代码示例

→ [`code/week1.5/modifiers/ModifierDemo.java`](../../code/week1.5/modifiers/ModifierDemo.java)
