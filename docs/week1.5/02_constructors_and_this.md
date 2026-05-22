# Week 1.5 §02 · 构造器与 this

> 上一篇造狗时是这样的：
> ```java
> Dog d = new Dog();
> d.name = "小白";
> d.breed = "柴犬";
> d.age = 3;
> ```
>
> 4 行才能造好一只狗，而且**可能漏赋字段**（比如忘了写 age）。
>
> 本篇引入**构造器**，让"new + 初始化"一行搞定。

---

## 1. 构造器是什么

**构造器**（constructor）是一个特殊的方法，**在 new 对象时被自动调用**，用来初始化对象的字段。

```java
public class Dog {
    String name;
    String breed;
    int age;

    // 这就是构造器
    public Dog(String name, String breed, int age) {
        this.name = name;
        this.breed = breed;
        this.age = age;
    }
}
```

**构造器的形态规则**

| 项 | 规则 |
|----|------|
| 名字 | 必须**和类名一模一样**（连大小写都要一样） |
| 返回类型 | **没有！连 `void` 都不写**——直接 `public Dog(...)` |
| 修饰符 | 通常 `public` |
| 调用时机 | `new Dog(...)` 时自动调用，**不能手动调** |

```java
// 现在 new 一只狗一行搞定
Dog d = new Dog("小白", "柴犬", 3);
```

`new Dog("小白", "柴犬", 3)` 做的事：
1. 在堆上分配 Dog 对象的内存
2. **调用构造器** `Dog(String, String, int)`
3. 构造器把字段填好
4. 把指向这个对象的引用赋给 `d`

---

## 2. this 是什么

注意构造器里这一行：

```java
this.name = name;
```

为什么写 `this.name`？

因为参数名也叫 `name`，方法体里直接写 `name = name;` **编译器分不清左边是字段还是参数**（其实它会都当成参数，结果就是字段没赋值）。

**`this` 指"当前正在被构造 / 操作的这个对象"**。`this.name` 明确指对象的字段，右边的 `name` 是参数。

### this 的两个用法

**(1) 区分字段和同名参数**（最常见）

```java
public Dog(String name, String breed, int age) {
    this.name = name;       // 左：字段；右：参数
    this.breed = breed;
    this.age = age;
}
```

**(2) 在方法里指代当前对象**

```java
public void introduce() {
    // 实例方法里其实可以省略 this，编译器自动加
    System.out.println(this.name);  // 等价于 System.out.println(name);
}
```

实例方法里 `this` 通常可以省略，但**有同名变量时**或想强调时要写。

---

## 3. 默认构造器

**如果你没写任何构造器**，编译器会**自动送你一个无参的、什么都不做的默认构造器**：

```java
public class Dog {
    String name;
    // 我没写构造器
}

// 但仍然可以
Dog d = new Dog();        // 调用了编译器送的默认构造器
```

**但是！只要你写了任何构造器，编译器就不送默认的了**：

```java
public class Dog {
    String name;

    // 我自己写了带参构造器
    public Dog(String name) {
        this.name = name;
    }
}

Dog d1 = new Dog("小白");    // ✅
Dog d2 = new Dog();           // ❌ 编译错误！没有无参构造器
```

如果想保留无参构造的能力，**自己显式写一个**：

```java
public Dog() {
    // 啥也不做也行
}

public Dog(String name) {
    this.name = name;
}
```

---

## 4. 构造器重载

构造器也能**重载**（同名不同参数列表，§00 讲过）。常见模式：多个构造器，一个全参，其它的转调全参。

```java
public class Dog {
    String name;
    String breed;
    int age;

    // 全参构造器
    public Dog(String name, String breed, int age) {
        this.name = name;
        this.breed = breed;
        this.age = age;
    }

    // 只有名字的构造器：品种和年龄给默认值
    public Dog(String name) {
        this(name, "未知", 0);      // 调用上面那个构造器
    }

    // 完全无参
    public Dog() {
        this("无名");                 // 调用上面那个（再级联到全参）
    }
}

Dog a = new Dog("小白", "柴犬", 3);
Dog b = new Dog("大黄");                 // → 转调 "大黄","未知",0
Dog c = new Dog();                        // → 转调 "无名","未知",0
```

**`this(...)` 的规则**

- `this(参数列表)` 用来在构造器里**调用另一个构造器**
- **必须放在构造器的第一行**（这是硬性规定，编译器强制）

---

## 5. 完整示例对比

### 没有构造器（旧方式）

```java
Dog d = new Dog();
d.name = "小白";
d.breed = "柴犬";
d.age = 3;
// 漏写 d.age 也能编译，age 默认是 0
```

### 有构造器（推荐）

```java
Dog d = new Dog("小白", "柴犬", 3);
// 想 new 必须传齐字段，少传编译报错
```

**好处**
- **不会漏赋字段**：构造器签名强制要求传齐
- **代码更短**：一行 vs 多行
- **能在赋值时做校验**（下一篇会讲）

---

## 6. 配套代码

[`code/week1.5/s02_constructors/`](../../code/week1.5/s02_constructors/)

里面有：
- `Dog.java`：三个重载构造器
- `DogDemo.java`：演示三种 new 法 + this 在方法里的用法

---

## 7. 这一步之后

构造器解决了"创建对象时一次性填好字段"的问题。但**字段还是裸露的**，外面任何代码都能写 `dog.age = -100`，把数据搞坏。

下一篇 [`03_encapsulation_and_static.md`](03_encapsulation_and_static.md) 讲：
- **封装**：用 `private` 把字段藏起来 + 用 getter/setter 暴露访问入口
- **static**：怎么定义"属于类、不属于具体对象"的数据和方法

---

## 8. 自查

- [ ] 给上一篇的 `Person` 类加构造器，一行 new 出对象
- [ ] 解释构造器和普通方法的 3 个差异（名字、返回类型、调用时机）
- [ ] 解释为什么有了带参构造器后，无参 new 会编译报错
- [ ] 写一个有三个重载构造器的类（全参 + 两参 + 一参，互相 `this(...)` 转调）
- [ ] 解释 `this.name = name;` 里左右两个 name 各是谁
