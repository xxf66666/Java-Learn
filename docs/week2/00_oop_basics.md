# Week 2 §00 · 类、对象、字段、方法、修饰符

> 本周从这里开始：把 Java 的"类"用熟。后面所有 Spring 写的都是类，地基不打好后面全是坑。

---

## 1. 一个完整的类长什么样

```java
package com.learning.week2;        // 包声明，必须在文件第一行（除了注释）

public class Student {

    // ===== 字段（成员变量）=====
    private String name;
    private int age;
    private static int totalCount = 0;     // 静态字段，全类共享

    // ===== 构造器 =====
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
        totalCount++;                      // 每 new 一次加 1
    }

    // 无参构造器（如果不写，编译器自动给一个；如果写了任何一个有参构造器，就不会自动给）
    public Student() {
        this("无名", 0);                    // 调用另一个构造器，必须放第一行
    }

    // ===== 实例方法 =====
    public String describe() {
        return name + "(" + age + ")";
    }

    // ===== 静态方法 =====
    public static int getTotalCount() {
        return totalCount;
    }

    // ===== getter / setter =====
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) {
        if (age < 0) throw new IllegalArgumentException("age 不能为负");
        this.age = age;
    }

    // ===== toString（推荐重写）=====
    @Override
    public String toString() {
        return "Student{name='" + name + "', age=" + age + "}";
    }
}
```

记住这个模板，后面 80% 的实体类都是这个结构。

---

## 2. 字段：实例字段 vs 静态字段

```java
public class Counter {
    private int value;              // 实例字段：每个对象一份
    private static int total;       // 静态字段：全类共享一份

    public void incr() {
        value++;        // 当前对象的 value + 1
        total++;        // 共享的 total + 1
    }
}

Counter a = new Counter();
Counter b = new Counter();
a.incr(); a.incr();
b.incr();
// a.value = 2, b.value = 1, Counter.total = 3
```

**何时用 `static`**
- 工具方法（`Math.abs`、`Files.readAllLines`）
- 常量（`public static final int MAX_SIZE = 100;`）
- 计数器、单例引用

**何时不用**：和某个具体对象绑定的状态（name、age、balance）

---

## 3. 构造器：创建对象时被调用

```java
public class Order {
    private String orderNo;
    private double amount;

    public Order(String orderNo, double amount) {
        this.orderNo = orderNo;
        this.amount = amount;
    }

    // 构造器重载：参数列表不同
    public Order(String orderNo) {
        this(orderNo, 0.0);     // 调用上面那个构造器，必须放第一行
    }
}

Order o1 = new Order("PO001", 99.99);
Order o2 = new Order("PO002");
```

**规则**
- 构造器名 = 类名，没有返回类型（**不是** `void`，是没有）
- 不写任何构造器，编译器会给一个无参的空构造器
- 写了任何构造器（哪怕只写一个有参的），编译器就**不再**给无参的——这时如果你想要无参，要自己显式写一个
- 一个类可以有多个构造器（参数不同），叫**构造器重载**

---

## 4. 方法：实例方法 vs 静态方法

```java
public class StringUtils {
    // 静态方法：用类名调用
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

StringUtils.isBlank("  ");           // true
```

```java
public class Wallet {
    private double balance;

    // 实例方法：用对象调用，能访问当前对象的字段
    public void deposit(double amount) {
        this.balance += amount;
    }
}

Wallet w = new Wallet();
w.deposit(100.0);                    // 必须先有对象
```

**关键区别**
- 静态方法**不能**访问实例字段（没有 `this`）
- 静态方法可以被 `ClassName.method()` 直接调用，不用 `new`
- 实例方法**可以**访问静态字段（反过来不行）

---

## 5. 访问修饰符：4 个级别

| 修饰符 | 本类 | 同包 | 子类 | 其它 |
|--------|------|------|------|------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| 不写（默认）| ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

**实操原则（封装）**
- **字段一律 `private`**，外界通过 `getter` / `setter` 访问
- **方法默认 `public`**（被外界调用）；内部辅助方法可以 `private`
- **类默认 `public`**（被外界用）

---

## 6. `final` 关键字：禁止修改

```java
public final class ImmutablePoint { ... }     // 这个类不能被继承

public class Foo {
    public final int CONSTANT = 100;            // 这个字段一旦赋值不能改
    public static final int MAX = 1000;         // 类常量（最常见用法）

    public final void doSomething() { ... }     // 这个方法不能被子类重写
}
```

**最常用的形态：常量**

```java
public class HttpStatus {
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
    public static final int SERVER_ERROR = 500;
}
```

---

## 7. `this` 和 `super`

```java
public class Animal {
    protected String name;
    public Animal(String name) { this.name = name; }
}

public class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);              // 调用父类构造器，必须放第一行
        this.breed = breed;
    }

    public void describe() {
        System.out.println(this.name + " is a " + this.breed);
        //                 ^^^^ 当前对象
    }
}
```

- `this` 指当前对象；可以用 `this.field` 区分字段和参数同名
- `super` 指父类；`super(...)` 调父类构造器，`super.method()` 调父类方法

---

## 8. 一个完整可跑的例子

参见 [`code/week2/oop/Student.java`](../../code/week2/oop/Student.java) 和 [`code/week2/oop/StudentDemo.java`](../../code/week2/oop/StudentDemo.java)。

---

## 9. 自查

- [ ] 不查文档，5 分钟写出一个带字段、构造器、getter/setter、toString 的 `Order` 类
- [ ] 说出 `static` 字段和实例字段的存储位置（前者在方法区，后者在堆）
- [ ] 解释为什么 `this(name, 0)` 必须放在构造器第一行
- [ ] 写一个有 `static final` 常量的工具类（如 `HttpStatus`）
- [ ] 解释 `private` 字段配合 `public getter/setter` 这种模式的好处（封装：能加校验、能改实现不影响调用方）
