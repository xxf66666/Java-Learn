# Week 1.5 §01 · 类与对象

> 这是 Java 最核心的两个词。讲透这两个，后面 OOP 一通百通。

---

## 1. 一个生活比喻

想象你要描述"狗"这种东西：

- **狗** 这个概念有：名字、品种、年龄；能叫、能跑
- **小白**（你家这只）有：名字="小白"、品种="柴犬"、年龄=3；它在叫"汪汪"
- **大黄**（邻居家那只）有：名字="大黄"、品种="金毛"、年龄=5；它在叫"汪汪汪"

这里：
- **"狗" 这个概念** = **类**（class），它是个**模板 / 蓝图**
- **小白、大黄** = **对象**（object），它们是按"狗"这个模板**具体造出来的实例**

**一个类可以造出无数个对象**，每个对象的字段值可以不同，但它们的"形状"是一样的（都有名字、品种、年龄）。

---

## 2. 定义一个类

```java
public class Dog {
    // 字段（field / 成员变量）：描述对象"有什么数据"
    String name;
    String breed;
    int age;

    // 方法：描述对象"能做什么"
    void bark() {
        System.out.println(name + " says: 汪汪!");
    }

    void introduce() {
        System.out.println("我叫 " + name + "，是 " + breed + "，今年 " + age + " 岁");
    }
}
```

注意：
- 文件名必须是 `Dog.java`（和 public 类名一致）
- 字段写在类里、方法外
- 方法**没有** `static`（这点和 §00 不一样，下面讲）

---

## 3. 创建对象 + 使用对象

```java
public class Demo {
    public static void main(String[] args) {
        // new Dog() = 按 Dog 这个模板造一只新狗
        // d1 是一个变量，指向这只狗
        Dog d1 = new Dog();

        // 给字段赋值
        d1.name = "小白";
        d1.breed = "柴犬";
        d1.age = 3;

        // 调用对象的方法
        d1.bark();          // 小白 says: 汪汪!
        d1.introduce();      // 我叫 小白，是 柴犬，今年 3 岁

        // 再造一只
        Dog d2 = new Dog();
        d2.name = "大黄";
        d2.breed = "金毛";
        d2.age = 5;

        d2.bark();          // 大黄 says: 汪汪!

        // 两只狗各自有各自的数据
        System.out.println(d1.name);    // 小白
        System.out.println(d2.name);    // 大黄
    }
}
```

### 几个关键点

**(1) `new Dog()`**：创建一个新对象。每次 `new` 都造一个**全新的**。

**(2) `Dog d1 = ...`**：声明一个**类型是 Dog 的变量** `d1`，让它指向那个新对象。

**(3) `d1.name`**：用点 `.` 访问对象的字段。

**(4) `d1.bark()`**：用点 `.` 调用对象的方法。

**(5) `d1` 和 `d2` 是两个独立的对象**：改 `d1.name` 不会影响 `d2.name`。

---

## 4. 类的方法 vs 对象的方法

回头看 §00 的 `MathUtil.add(...)`，方法名前面有个 `static`：

```java
public static int add(int a, int b) { ... }    // 静态方法（属于类）

public void bark() { ... }                       // 实例方法（属于对象）
```

| | 静态方法（static） | 实例方法（无 static） |
|--|--------------------|-----------------------|
| 属于谁 | **类**本身 | **对象** |
| 怎么调 | `类名.方法()` | `对象.方法()` |
| 能访问什么 | 不能访问对象的字段（没有具体对象） | 能访问当前对象的字段 |
| 典型场景 | 工具方法（`Math.abs`） | 描述"对象能做的事"（`dog.bark()`） |

```java
Dog d = new Dog();
d.bark();              // ✅ 用对象调
Dog.bark();             // ❌ 编译错误：bark 不是静态方法

Math.abs(-5);           // ✅ 用类名调（Math.abs 是静态方法）
```

---

## 5. 对象在内存里长什么样

简化版图示：

```
栈（Stack）                  堆（Heap）
─────────                    ──────────────────
d1 ──────────────────────►   Dog 对象 #1
                              ├── name: "小白"
                              ├── breed: "柴犬"
                              └── age: 3

d2 ──────────────────────►   Dog 对象 #2
                              ├── name: "大黄"
                              ├── breed: "金毛"
                              └── age: 5
```

- **`d1` 这个变量**放在栈上（方法的局部变量在栈里）
- **真正的 Dog 对象**放在堆上（new 出来的对象在堆里）
- `d1` 存的是"指向堆里那个对象的引用"（类似一个地址）

### 引用赋值的坑

```java
Dog a = new Dog();
a.name = "小白";

Dog b = a;             // 不是新建对象！b 和 a 指向同一个 Dog
b.name = "大黄";

System.out.println(a.name);    // 大黄！不是小白！
```

`Dog b = a;` 只是让 `b` 也指向 `a` 指着的那个对象。两个变量、一个对象。

---

## 6. null：什么都不指向

```java
Dog d = null;          // d 是个 Dog 类型变量，但什么也不指向

d.bark();              // ❌ NullPointerException（空指针异常）
                       // 因为 d 没指向任何对象，没法调它的方法
```

写代码时如果一个变量可能是 `null`，**调用它的方法前要先判断**：

```java
if (d != null) {
    d.bark();
}
```

---

## 7. 一个完整的小例子

[`code/week1.5/s01_classes/`](../../code/week1.5/s01_classes/) 里有：

- `Dog.java`：定义 Dog 类
- `DogDemo.java`：造几只狗、调用方法、演示引用赋值的坑

跑一遍 + debug 一遍，**重点观察**：
- 用 IDEA 的 debugger 可以在变量面板看到 `d1`、`d2` 各自的字段值
- `Dog b = a;` 后 `a` 和 `b` 在 debugger 里显示同一个对象（IDEA 会标 `id`）

---

## 8. 这一步之后

到这里我们的 Dog 类有个问题：**字段是裸露的**，谁都能改 `d.age = -100`，没人拦着。

下一篇 [`02_constructors_and_this.md`](02_constructors_and_this.md) 讲：
- **构造器**：怎么在 new 对象时就把字段填好，避免"先 new 再一行行赋值"
- **this**：怎么在方法里指代"当前对象自己"

---

## 9. 自查

- [ ] 写一个 `Person` 类，含字段 `name` / `age`，方法 `greet()` 打印自我介绍
- [ ] 在 main 里 new 两个 Person，分别赋值、分别调用 greet
- [ ] 解释 `Dog d1 = new Dog()` 这一行做了几件事
- [ ] 解释 `static` 方法和实例方法（无 static）的区别
- [ ] 写一段会抛 `NullPointerException` 的代码
- [ ] 解释 `Dog b = a` 之后改 b 为什么 a 也变
