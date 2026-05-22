# Week 1.5 §05 · 抽象类 vs 接口

> 上一篇的 `Animal` 类有个问题：它**能被 new**。
>
> ```java
> Animal a = new Animal("???");
> a.sound();         // "??? 发出某种声音"
> ```
>
> 但现实里没人养"一只 Animal"——它只是个抽象概念。我们要的是 Dog、Cat 这种**具体的子类**。
>
> 本篇用 `abstract` 强制 "Animal 不能 new、必须靠子类"，并引入 `interface`——它是 Java OOP 设计的另一半。

---

## 1. 抽象类：abstract class

```java
public abstract class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    // 普通方法：有完整实现，子类继承
    public void sleep() {
        System.out.println(name + " 在睡觉");
    }

    // 抽象方法：没有方法体！
    // 子类**必须**重写它
    public abstract void sound();
}
```

**两个核心变化**

**(1) 类前加 `abstract`**：这个类**不能直接 new**

```java
Animal a = new Animal("???");    // ❌ 编译错误
```

**(2) 抽象方法 `abstract void sound();`**：
- 没有方法体（连 `{}` 都没有，直接 `;` 结尾）
- 表示"我承诺有这个能力，但具体怎么做交给子类"
- 子类**必须**重写它，否则子类也得是 abstract

### 子类怎么写

```java
public class Dog extends Animal {
    public Dog(String name) { super(name); }

    // 必须实现父类的抽象方法
    @Override
    public void sound() {
        System.out.println(name + ": 汪汪!");
    }
}
```

---

## 2. 抽象类 vs 普通类对比

| 特性 | 普通类 | 抽象类 |
|------|--------|--------|
| 能否 new | ✅ | ❌ |
| 能否有字段 | ✅ | ✅ |
| 能否有构造器 | ✅ | ✅（虽然不能直接 new，但子类的 super(...) 会调用） |
| 能否有普通方法 | ✅ | ✅ |
| 能否有抽象方法 | ❌ | ✅ |

**什么时候用 abstract**：父类只是个"概念"，**只有具体子类才有意义**（Animal / Shape / 支付方式...）。

---

## 3. 接口：interface

`interface` 是 Java 的**纯能力契约**：只描述"能做什么"，不描述"怎么做"。

```java
public interface Swimmer {
    // 没有字段（其实可以有 public static final 常量，但很少用）
    // 方法默认是 public abstract，不用写出来

    void swim();                  // 抽象方法（编译器自动加 public abstract）
}
```

```java
public interface Flyer {
    void fly();
}
```

### 实现接口：implements

```java
// 一只鸭子：又能游又能飞
public class Duck extends Animal implements Swimmer, Flyer {
    public Duck(String name) { super(name); }

    @Override
    public void sound() {
        System.out.println(name + ": 嘎嘎!");
    }

    @Override
    public void swim() {
        System.out.println(name + " 在游泳");
    }

    @Override
    public void fly() {
        System.out.println(name + " 在飞");
    }
}
```

**关键点**

- 一个类**只能 `extends` 一个父类**
- 但可以 **`implements` 多个接口**（用逗号分隔）
- 实现接口 = 必须实现接口里的所有抽象方法

---

## 4. Java 8+ 的接口能力扩展

老接口里**只有**抽象方法。Java 8 之后能力变强：

### default 方法：接口里也能有方法体

```java
public interface Swimmer {
    void swim();

    // default 方法：有默认实现
    // 实现类可以选择重写，也可以直接用默认的
    default void floatOnWater() {
        System.out.println("漂在水上...");
    }
}
```

为什么有这个：JDK 给已有接口（如 `List`）加新方法时，**不破坏已有实现类**——所有实现类自动得到 default 实现。

### static 方法：接口自己的工具

```java
public interface Swimmer {
    void swim();

    static String version() {
        return "Swimmer-v1";
    }
}

// 调用：用接口名
String v = Swimmer.version();
```

---

## 5. 抽象类 vs 接口：什么时候用哪个

这是面试和工作都常被问的问题。

| 维度 | 抽象类 | 接口 |
|------|--------|------|
| 关系语义 | "is-a"（是一种） | "can-do"（能做） |
| 字段 | 可以有（也能保存状态） | 只能有 `public static final` 常量 |
| 构造器 | 有 | 无 |
| 一个类能继承几个 | **1 个** | **多个** |
| 共享代码 | 适合（写普通方法给子类继承） | 也能（default 方法） |

**经验法则**

- 表达"一类东西"（Animal、Shape、Order）→ **抽象类**
- 表达"一种能力"（Swimmer、Comparable、Serializable）→ **接口**
- 不确定 → 优先**接口**（Spring / 整个 Java 生态都偏好接口）

---

## 6. 多重实现下的多态

接口也能多态：

```java
// 用接口类型装实现类对象
Swimmer s = new Duck("唐老鸭");
s.swim();           // 走 Duck 的实现

Flyer f = new Duck("唐老鸭");
f.fly();
```

可以同时把 Duck 当 Animal、Swimmer、Flyer 用，看你需要哪一面。

---

## 7. 配套代码

[`code/week1.5/s05_abstract_interface/`](../../code/week1.5/s05_abstract_interface/)

- `Shape.java`：抽象类（area / perimeter 是 abstract）
- `Circle.java` / `Rectangle.java`：具体子类
- `Drawable.java`：接口（含 default 方法）
- `AbstractInterfaceDemo.java`：把 Circle 当 Shape 用 / 当 Drawable 用

---

## 8. 地基篇到此结束

学到这里你已经掌握了 Java OOP 的**全部核心**：

- ✅ 方法 / 类 / 对象
- ✅ 构造器 / this
- ✅ 封装 / static
- ✅ 继承 / 多态
- ✅ 抽象类 / 接口

回到 Week 2 看 `Student` / `Shape` 等例子时**完全不会卡**。

后面 06-11 是常用语法（基本类型、字符串、数组、枚举、Lambda、泛型、注解），是**辅助工具**——可以现在读，也可以遇到再回来翻。

---

## 9. 自查

- [ ] 写一个抽象类 `Shape`，抽象方法 `area()`
- [ ] 写两个子类 `Circle` / `Rectangle`，各自实现 area
- [ ] 试着 `new Shape(...)` 看编译报错
- [ ] 写一个 `Printable` 接口含 `print()` 方法
- [ ] 让 `Circle implements Printable`，print 时输出"画一个圆"
- [ ] 用 `Shape[]` 和 `Printable[]` 各装一遍 circle，调对应方法
- [ ] 解释抽象类和接口在 3 个维度的差异（字段 / 单还是多 / 关系语义）
