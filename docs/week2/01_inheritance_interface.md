# Week 2 §01 · 继承、抽象类、接口、多态

> 目标：把面向对象的"扩展能力"用熟。Spring 大量基于接口编程，不熟接口写 Spring 寸步难行。

---

## 1. 继承：`extends`

```java
public class Animal {
    protected String name;

    public Animal(String name) { this.name = name; }

    public void sleep() {
        System.out.println(name + " is sleeping");
    }

    public void sound() {
        System.out.println(name + " makes a sound");
    }
}

public class Dog extends Animal {
    public Dog(String name) {
        super(name);                // 调父类构造器
    }

    @Override
    public void sound() {           // 重写父类方法
        System.out.println(name + " says: Woof!");
    }
}

Dog d = new Dog("Rex");
d.sleep();          // 继承自 Animal：Rex is sleeping
d.sound();          // 自己重写的：Rex says: Woof!
```

**规则**
- 一个类**只能继承一个父类**（单继承）
- 子类构造器**必须**先调父类构造器（默认隐式 `super()`，父类没有无参构造器时必须显式写）
- 重写父类方法**必须**加 `@Override`（编译器帮你抓拼写错误）
- `protected` 字段子类能访问，`private` 不能

---

## 2. 多态：父类引用指向子类对象

```java
Animal a1 = new Dog("Rex");        // ✅ 合法：Dog 是一种 Animal
Animal a2 = new Cat("Whiskers");

a1.sound();       // Rex says: Woof!   ← 实际调用 Dog 的版本
a2.sound();       // Whiskers says: Meow!
```

**为什么重要**：写代码的时候你可能不知道传进来的是 Dog 还是 Cat，但调 `a.sound()` 总能正确执行。这就是"面向接口编程"的根。

```java
public void treatAnimal(Animal a) {
    a.sleep();
    a.sound();
}

treatAnimal(new Dog("Rex"));
treatAnimal(new Cat("Whiskers"));    // 同一个方法，不同行为
```

---

## 3. 抽象类：`abstract`

有些类天生就**不该被实例化**（"Animal" 是个抽象概念，没有具体的 Animal 对象），它应该只作为父类被继承。

```java
public abstract class Shape {
    protected String color;

    public Shape(String color) { this.color = color; }

    // 抽象方法：没有实现，子类必须实现
    public abstract double area();

    // 普通方法：有默认实现
    public void describe() {
        System.out.println(color + " shape with area " + area());
    }
}

public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

// Shape s = new Shape("red");      // ❌ 编译错误：抽象类不能 new
Shape s = new Circle("red", 5.0);   // ✅
s.describe();
```

**规则**
- `abstract class` 不能 `new`
- 抽象类**可以**有字段、构造器、普通方法
- `abstract` 方法没有 `{ }` 函数体，必须被子类实现

---

## 4. 接口：`interface`

接口比抽象类**更轻**——只描述"能做什么"，不描述"怎么做"。

```java
public interface Swimmer {
    void swim();                              // 默认 public abstract
    int MAX_DEPTH = 100;                      // 默认 public static final
}

public interface Flyer {
    void fly();
}

public class Duck extends Animal implements Swimmer, Flyer {
    public Duck(String name) { super(name); }

    @Override
    public void swim() {
        System.out.println(name + " is swimming");
    }

    @Override
    public void fly() {
        System.out.println(name + " is flying");
    }
}
```

**关键规则**
- 一个类可以 `implements` **多个**接口（解决"单继承"的局限）
- 接口里的方法默认 `public abstract`（可以省略）
- 接口里的字段默认 `public static final`（即常量）
- Java 8+ 接口可以有 `default` 方法（提供默认实现）和 `static` 方法

### Java 8+ 的 `default` 方法

```java
public interface Greeter {
    String getName();

    // default 方法：接口提供默认实现，实现类可选择重写
    default void greet() {
        System.out.println("Hello, " + getName());
    }
}
```

这个特性是为了**给老接口加方法不破坏已有实现**（比如给 `List` 加 `forEach`）。

---

## 5. 抽象类 vs 接口：什么时候用哪个

| 维度 | 抽象类 | 接口 |
|------|--------|------|
| 状态（字段） | 有 | 只能有常量 |
| 构造器 | 有 | 没有 |
| 继承数量 | 单继承 | 多实现 |
| 关系语义 | "is-a"（是一种） | "can-do"（能做） |
| 共享代码 | 适合放共同代码 | 适合纯定义契约 |

**经验法则**
- 表示"一类东西"（Animal、Shape、User）→ 抽象类
- 表示"一种能力"（Swimmer、Comparable、Serializable）→ 接口
- **不确定就用接口**（Spring 整个生态都偏好接口）

---

## 6. `Object` 类：所有类的祖先

任何类如果没写 `extends`，默认继承 `Object`。

```java
public class Foo { ... }
// 等价于
public class Foo extends Object { ... }
```

`Object` 有几个关键方法，**强烈建议熟练重写**：

| 方法 | 默认行为 | 推荐重写时机 |
|------|----------|--------------|
| `toString()` | 返回 `类名@哈希` | 几乎每个实体类都该重写 |
| `equals(Object o)` | 比较引用（== 那种） | 业务上需要"两个对象内容相同就算相等" |
| `hashCode()` | 默认基于地址 | **重写 equals 必须重写 hashCode**（HashMap / HashSet 依赖它） |
| `getClass()` | 返回运行时类对象 | 反射时用 |

```java
public class Point {
    private int x, y;

    public Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point p = (Point) o;
        return this.x == p.x && this.y == p.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}
```

> **IDEA 一键生成**：右键 → `Generate` → `equals() and hashCode()` / `toString()`

---

## 7. 类型转换和 `instanceof`

```java
Animal a = new Dog("Rex");

// 向下转型：需要显式转换
if (a instanceof Dog) {
    Dog d = (Dog) a;
    d.fetch();
}

// Java 16+ 模式匹配（推荐）
if (a instanceof Dog d) {
    d.fetch();                  // d 直接可用
}
```

---

## 8. 自查

- [ ] 不查文档写一个 `Shape` 抽象类，包含 3 个子类（Circle、Rectangle、Triangle）
- [ ] 解释什么时候用抽象类、什么时候用接口
- [ ] 给一个类正确重写 `equals` / `hashCode` / `toString`
- [ ] 解释为什么 `equals` 和 `hashCode` 必须一起重写（HashMap 找 key 先按 hash 定桶，再按 equals 比较）
- [ ] 用 Java 16+ 的 `instanceof` 模式匹配重写一段类型判断代码

## 代码示例

→ [`code/week2/oop/`](../../code/week2/oop/) —— Animal / Dog / Cat / Duck 完整继承体系
→ [`code/week2/shape/`](../../code/week2/shape/) —— 抽象类 Shape + 多个子类
