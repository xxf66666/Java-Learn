# Week 1.5 §04 · 继承与多态

> 之前我们能写一个独立的类。但现实里类之间常常**有共性**：
>
> - 狗、猫、鸟 都是动物，都有名字、都能 sleep
> - 但它们叫的方式不同（汪汪 / 喵喵 / 啾啾）
>
> 如果给每种动物单独写一个类，**字段和方法会大量重复**。
>
> 本篇用"继承"消除重复，用"多态"让"调同一个方法、自动执行各自的实现"。

---

## 1. 继承：extends

```java
// 父类
public class Animal {
    String name;

    public Animal(String name) {
        this.name = name;
    }

    public void sleep() {
        System.out.println(name + " 在睡觉 zzz");
    }

    public void sound() {
        System.out.println(name + " 发出声音");
    }
}

// 子类：Dog 是一种 Animal
public class Dog extends Animal {
    // extends Animal 后，Dog 自动有了 Animal 的字段（name）和方法（sleep / sound）
}
```

**`extends` 的含义**：Dog **是一种** Animal。Animal 有的，Dog 都有。

```java
Dog d = new Dog(...);
d.sleep();          // 来自父类 Animal 的方法，Dog 自动继承
```

---

## 2. 子类的构造器：super(...)

继承后，**子类的构造器必须先把父类初始化好**：

```java
public class Dog extends Animal {
    String breed;

    public Dog(String name, String breed) {
        super(name);              // 调父类的构造器，把 name 传上去
        this.breed = breed;       // 再处理自己新加的字段
    }
}
```

**`super(...)` 规则**

- `super(参数)` 在子类构造器里**调父类的构造器**
- **必须是构造器第一行**（和 `this(...)` 一样的规则）
- 不写时编译器自动加 `super()`（无参版）；如果父类没无参构造器，**必须显式写 `super(参数)`**

```java
// 父类只有带参构造器
public class Animal {
    public Animal(String name) { ... }
}

public class Dog extends Animal {
    public Dog() {
        // ❌ 编译错误！编译器自动加 super()，但 Animal 没无参构造器
    }

    public Dog(String name) {
        super(name);    // ✅ 显式调
    }
}
```

---

## 3. 方法重写（@Override）

**重写**（override）：子类**重新实现**父类的某个方法。

```java
public class Animal {
    public void sound() {
        System.out.println("某种动物的声音");
    }
}

public class Dog extends Animal {
    // @Override 注解：告诉编译器"这是覆盖父类方法"
    // 编译器会检查父类是否真有这个方法 —— 拼错了会报错
    @Override
    public void sound() {
        System.out.println("汪汪!");
    }
}

public class Cat extends Animal {
    @Override
    public void sound() {
        System.out.println("喵~");
    }
}
```

`Dog` 和 `Cat` 的 `sound()` 都覆盖了父类的版本。

```java
new Dog().sound();      // 汪汪!（执行 Dog 的版本）
new Cat().sound();      // 喵~（执行 Cat 的版本）
```

**`@Override` 强烈建议每次都写**——编译器帮你抓拼写错误。

### super 调父类方法

子类想在重写时**保留父类逻辑** + **加自己的**：

```java
@Override
public void sound() {
    super.sound();           // 先调父类的（输出"某种动物的声音"）
    System.out.println("汪汪!");
}
```

---

## 4. 多态：父类引用，子类对象

下面这一行是 Java OOP 的精髓：

```java
Animal a = new Dog("小白", "柴犬");
```

- 变量类型是 `Animal`（父类）
- 实际对象是 `Dog`（子类）

**这一切都是合法的**，因为"Dog 是一种 Animal"。

### 多态的关键行为

```java
Animal a = new Dog("小白", "柴犬");
a.sound();          // 输出：汪汪!（不是"某种动物的声音"）
```

`a.sound()` 调用时，**Java 根据 `a` 指向的实际对象类型**（Dog）决定调哪个版本，**不是看变量声明的类型**（Animal）。

这叫**动态绑定**（dynamic binding），是多态的基础。

### 为什么有用：统一处理一批对象

```java
Animal[] zoo = {
    new Dog("小白", "柴犬"),
    new Cat("汤姆"),
    new Dog("大黄", "金毛"),
};

for (Animal a : zoo) {
    a.sound();      // 自动调用每个动物自己的 sound 实现
}
```

输出：
```
汪汪!
喵~
汪汪!
```

写"循环调 sound" 时根本不用判断"这是 Dog 还是 Cat"——多态自动处理。

---

## 5. instanceof：检查实际类型

有时候确实需要知道具体类型：

```java
Animal a = new Dog("小白", "柴犬");

if (a instanceof Dog) {
    // 这里能确定 a 是 Dog
    System.out.println("是只狗");
}
```

### Java 16+ 模式匹配（推荐）

```java
if (a instanceof Dog d) {
    // 直接拿到 Dog 类型的 d 变量，省一步强转
    System.out.println(d.breed);   // 能访问 Dog 自己的字段
}
```

旧写法是要先 `instanceof` 判断、再强转，模式匹配把两步合一：

```java
// 旧
if (a instanceof Dog) {
    Dog d = (Dog) a;
    System.out.println(d.breed);
}
```

---

## 6. 向上转型 vs 向下转型

- **向上转型**（子 → 父）：**永远安全**，编译器自动转

```java
Dog d = new Dog(...);
Animal a = d;       // 自动向上转型，永远成功
```

- **向下转型**（父 → 子）：**可能失败**，必须显式写

```java
Animal a = new Dog(...);
Dog d = (Dog) a;        // 强制转型；如果 a 实际是 Cat 会抛 ClassCastException
```

建议向下转型前先 `instanceof` 判断。

---

## 7. Java 的继承限制

- **一个类只能 `extends` 一个父类**（单继承）
- 但可以 `implements` 多个接口（下一篇讲）
- 如果不写 `extends`，默认继承 `Object`（所有类的祖先）

```java
public class Foo { ... }
// 等价于
public class Foo extends Object { ... }
```

这就是为什么任何对象都能调 `toString()` / `equals()` / `hashCode()`——它们是 `Object` 类的方法。

---

## 8. 配套代码

[`code/week1.5/s04_inheritance/`](../../code/week1.5/s04_inheritance/)

- `Animal.java`：父类
- `Dog.java` / `Cat.java`：两个子类（不同 sound）
- `InheritanceDemo.java`：完整跑一遍，演示多态、instanceof、向下转型

---

## 9. 这一步之后

到现在的 Animal 类有个问题：**它本身能 new**，但 "Animal" 是个抽象概念，没人养"一只 Animal"。

下一篇 [`05_abstract_and_interface.md`](05_abstract_and_interface.md) 讲：
- **abstract class**：禁止 new，强制让子类实现关键方法
- **interface**：纯粹的"能力契约"，能多实现

---

## 10. 自查

- [ ] 写一个父类 `Vehicle`（有字段 `brand`），子类 `Car` 和 `Bike`
- [ ] 子类构造器里用 `super(...)` 调父类构造器
- [ ] 子类重写父类的 `describe()` 方法
- [ ] 在 main 里用 `Vehicle[] arr = { new Car(...), new Bike(...) }`，循环调 describe（多态）
- [ ] 用 `instanceof` + 模式匹配判断元素是 Car 还是 Bike
- [ ] 解释 `Vehicle v = new Car(...); v.method();` 到底调谁的 method
