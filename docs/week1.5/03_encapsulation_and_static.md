# Week 1.5 §03 · 封装 + static

> 上一篇我们的字段还是裸露的：
> ```java
> dog.age = -100;        // 没人拦着，狗能 -100 岁
> dog.age = 99999;       // 也能 99999 岁
> ```
>
> 本篇用**封装**把字段藏起来，强迫外界走"门"（getter/setter），并顺便把 `static` 一次讲清。

---

## 1. 访问修饰符：谁能看到这个字段/方法

每个字段和方法前面都可以加一个**访问修饰符**：

| 修饰符 | 谁能访问 |
|--------|---------|
| `public` | **所有人**（任何类、任何包） |
| `protected` | 本类 + 同包 + 子类 |
| 不写（默认） | 本类 + 同包 |
| `private` | **只有本类**（连同包都不能） |

记最常用的两个：
- **`public`** = 公开的"门"
- **`private`** = 锁起来，只有自己能动

---

## 2. 封装：private 字段 + public 方法

**封装**（encapsulation）的思想：

- **字段一律 `private`**：藏起来，外界看不到
- **暴露 `public` 方法**作为访问入口（getter 拿、setter 改）
- **在 setter 里做校验**：不让外界写入非法值

### 示例：BankAccount

```java
public class BankAccount {
    private double balance;        // 字段私有，外界看不到

    public BankAccount(double initial) {
        // 初始化也要走校验
        if (initial < 0) {
            throw new IllegalArgumentException("初始余额不能为负");
        }
        this.balance = initial;
    }

    // getter：让外界能"读"
    public double getBalance() {
        return balance;
    }

    // 存钱：业务方法，里面有校验
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("存款金额必须 > 0");
        }
        balance += amount;
    }

    // 取钱：校验余额
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("取款金额必须 > 0");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("余额不足");
        }
        balance -= amount;
    }
}
```

外界用法：

```java
BankAccount acc = new BankAccount(100);
acc.deposit(50);
acc.withdraw(30);
System.out.println(acc.getBalance());     // 120

// acc.balance = -999999;    // ❌ 编译错误：balance 是 private
// 外界永远不能绕过 deposit/withdraw 把余额改成非法值
```

---

## 3. 常规 getter / setter 模板

对一个 `private` 字段 `name`，惯例命名：

```java
private String name;

// getter：约定 getXxx，无参，返回字段类型
public String getName() {
    return name;
}

// setter：约定 setXxx，一个参数，返回 void
public void setName(String name) {
    // 通常加校验
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("name 不能为空");
    }
    this.name = name;
}

// boolean 字段的 getter 用 isXxx
private boolean enabled;
public boolean isEnabled() { return enabled; }
public void setEnabled(boolean enabled) { this.enabled = enabled; }
```

> **IDEA 一键生成**：`Code → Generate (⌘N / Alt+Insert) → Getter and Setter`，选字段，全部自动生成。

---

## 4. 为什么不直接 public 字段

短期看封装"多写代码"，但长期看：

**(1) 加校验**：上面 BankAccount 的例子，setter 拦了负数。

**(2) 改实现不影响外界**：将来你想"读 age 时其实是用生日算的"，只要 getter 里改：

```java
private LocalDate birthday;

public int getAge() {
    return LocalDate.now().getYear() - birthday.getYear();
}
```

外面调用方还是 `person.getAge()`，**不用改任何代码**。

**(3) 控制只读字段**：只写 getter 不写 setter → 字段对外只读。

**(4) 框架要求**：Spring、Jackson 这些框架默认通过 getter/setter 访问字段。不写就用不了。

---

## 5. static：属于类，不属于对象

回到 §01 我们提过：
- **实例字段 / 方法**：属于对象，每个对象一份
- **静态字段 / 方法**（带 `static`）：属于类本身，全类共享一份

### 实例字段 vs 静态字段

```java
public class Counter {
    private int instanceCount = 0;          // 每个 Counter 对象各自一份
    private static int totalCount = 0;       // 整个 Counter 类共享一份

    public Counter() {
        instanceCount = 1;        // 给当前对象的字段
        totalCount++;             // 给共享字段 +1
    }
}

Counter a = new Counter();
Counter b = new Counter();
Counter c = new Counter();

// a / b / c 各自的 instanceCount 都是 1（互不影响）
// 但 totalCount 是共享的 = 3
```

### 静态方法

```java
public class MathUtil {
    // 静态方法：用类名调，不用 new
    public static int square(int x) {
        return x * x;
    }
}

int n = MathUtil.square(5);     // 25
// 不需要 new MathUtil()
```

**静态方法的限制**：**不能访问实例字段 / 实例方法**。

```java
public class Counter {
    private int instanceCount = 0;
    private static int totalCount = 0;

    public static void show() {
        System.out.println(totalCount);          // ✅ 静态字段能访问
        System.out.println(instanceCount);        // ❌ 编译错误！
        // 因为静态方法不绑定某个具体对象，无从知道是哪个 instanceCount
    }
}
```

---

## 6. static 的常见用法

### (1) 工具类

整个类都是静态方法，存放无状态的"小工具"。

```java
public class StringUtils {
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

StringUtils.isBlank("  ");    // true，直接用类名调
```

### (2) 常量

`static final` 配合 + 全大写命名：

```java
public class HttpStatus {
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;
}

if (status == HttpStatus.OK) { ... }
```

> `final` 的含义：一旦赋值不能再改（详见后面笔记）。
> 这套 = **常量**。

### (3) 全局计数 / 单例引用

```java
public class IdGenerator {
    private static long counter = 0;

    public static long next() {
        counter++;
        return counter;
    }
}

long id1 = IdGenerator.next();    // 1
long id2 = IdGenerator.next();    // 2
```

---

## 7. 配套代码

[`code/week1.5/s03_encapsulation/`](../../code/week1.5/s03_encapsulation/)

- `BankAccount.java`：经典封装案例（private 余额 + setter 校验）
- `Counter.java`：演示实例字段 vs 静态字段
- `StringUtils.java`：纯静态工具类
- `EncapsulationDemo.java`：跑起来看效果

---

## 8. 这一步之后

到这里我们的类比较"工业级"了：private 字段、构造器、getter/setter。

下一篇 [`04_inheritance_and_polymorphism.md`](04_inheritance_and_polymorphism.md) 讲：
- **继承**：怎么让一个类"复用"另一个类（Dog 是一种 Animal）
- **多态**：父类类型的变量可以装子类对象，调用方法时执行子类版本

---

## 9. 自查

- [ ] 把上一篇的 `Person` 类改成"封装版"：字段 private + getter/setter
- [ ] 给 `setAge` 加校验：age < 0 或 > 150 时抛 `IllegalArgumentException`
- [ ] 解释为什么要把字段 `private` 而不是 `public`
- [ ] 写一个 `Counter` 类，每次 new 时 totalCount + 1
- [ ] 写一个静态工具类，里面有 `static boolean isEven(int n)`
- [ ] 解释为什么静态方法不能访问实例字段
