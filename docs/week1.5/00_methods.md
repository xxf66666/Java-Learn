# Week 1.5 §00 · 方法

> Java 里**没有"函数"这个词**。Java 里只有"**方法**（method）"。
>
> "方法"和你以前理解的"函数"几乎是一回事：**一段被命名、能被反复调用的代码**。
>
> 但有一个核心区别：**Java 的方法必须定义在类里面**。不能像有些语言那样在文件顶层写一个自由函数。

---

## 1. 方法的基本形态

```java
public class MathUtil {

    // 一个方法
    public static int add(int a, int b) {
        int result = a + b;
        return result;
    }
}
```

把这个方法**拆开**看：

```
public  static  int     add        (int a, int b)    {  ...  }
  ↑       ↑     ↑       ↑               ↑              ↑
 修饰符  修饰符  返回   方法名          参数列表          方法体
                类型
```

- **修饰符**（可以多个）：先不管，照着写 `public static`
- **返回类型**：方法执行完返回什么类型的数据。不返回任何东西写 `void`
- **方法名**：自己起名，小驼峰命名（`getUserName`、`calculateTotal`）
- **参数列表**：圆括号 `(...)` 里。每个参数都要写**类型 + 名字**
- **方法体**：花括号 `{...}` 里的代码

---

## 2. 调用方法

写好方法只是定义，**还需要在某处调用它**才会执行。

```java
public class Demo {
    public static void main(String[] args) {
        // 调用 MathUtil 类的 add 方法
        int sum = MathUtil.add(3, 5);
        System.out.println(sum);     // 输出 8
    }
}
```

`MathUtil.add(3, 5)` 这一句做的事：

1. 跳到 `MathUtil` 类的 `add` 方法
2. 把 `3` 赋给参数 `a`，把 `5` 赋给参数 `b`
3. 执行方法体里的语句
4. 遇到 `return result;` 时停下，把 `result` 的值（8）"还回"调用处
5. `int sum = 8;`

---

## 3. 返回值与 void

### 有返回值

```java
public static int square(int x) {
    return x * x;           // 必须 return 一个 int
}

int n = square(5);          // n = 25
```

**`return` 一旦执行，方法立刻结束**。`return` 后面的代码不会跑：

```java
public static int abs(int x) {
    if (x < 0) {
        return -x;          // 如果负数，return 后方法结束
    }
    return x;               // 否则返回 x
}
```

### 没有返回值：void

`void` 是"虚无"的意思——这个方法做完事就完了，**不还任何东西**。

```java
public static void greet(String name) {
    System.out.println("Hello, " + name);
    // 没有 return 也行；想提前结束可以写 return;（不带值）
}

greet("Alice");             // 直接调用，不需要接住返回值
```

---

## 4. 参数怎么传

Java 的参数传递规则只有一条：**值传递**（pass by value）。

但"值"的含义在两种类型下不一样：

### 基本类型：传值的副本

```java
public static void tryChange(int x) {
    x = 999;                // 改的是参数 x（局部副本）
}

public static void main(String[] args) {
    int a = 1;
    tryChange(a);
    System.out.println(a);  // 还是 1，没变
}
```

`a` 的值 `1` 被**复制**了一份给 `x`。`x` 怎么改都不影响 `a`。

### 引用类型：传"指向同一对象的引用"

```java
public static void tryChange(int[] arr) {
    arr[0] = 999;           // 改的是 arr 指向的数组的第 0 个元素
}

public static void main(String[] args) {
    int[] nums = {1, 2, 3};
    tryChange(nums);
    System.out.println(nums[0]);    // 999！变了！
}
```

`arr` 和 `nums` 指向**同一个数组对象**。改这个对象的内容，两边都看得见。

> 这不是"传引用"——传的还是"引用的值"（也就是地址）。但效果上看起来像是改了原对象。

---

## 5. 方法重载

**同一个类里**可以有**多个同名方法**，只要**参数列表不同**。这叫"方法重载"（overload）。

```java
public class Calculator {
    // 三个 add，名字一样但参数不同
    public static int add(int a, int b) {
        return a + b;
    }

    public static double add(double a, double b) {
        return a + b;
    }

    public static int add(int a, int b, int c) {
        return a + b + c;
    }
}

// 调用时编译器看参数类型/个数自动选哪个版本
Calculator.add(1, 2);          // 调第一个（两个 int）
Calculator.add(1.5, 2.5);      // 调第二个（两个 double）
Calculator.add(1, 2, 3);       // 调第三个（三个 int）
```

**重载的判断标准**：参数列表（类型、个数、顺序）不同就行。

- ✅ `add(int, int)` 和 `add(double, double)`：类型不同
- ✅ `add(int, int)` 和 `add(int, int, int)`：个数不同
- ❌ `add(int, double)` 和 `add(double, int)`：技术上算重载，但容易看错，**不要这样写**
- ❌ 只是返回类型不同：编译器不能区分（调用时不写返回类型）

---

## 6. 完整示例

[`code/week1.5/s00_methods/MethodDemo.java`](../../code/week1.5/s00_methods/MethodDemo.java) 里有完整可跑的代码，覆盖：

- 静态方法的定义和调用
- void 方法
- 多种返回类型
- 方法重载（3 个 max 方法）
- 基本类型 vs 数组的参数传递差异

跑一遍 + 用 debugger 单步走一遍，体会"方法被调用 → 跳过去 → 返回回来"的过程。

---

## 7. 这一步之后

下一篇 [`01_classes_and_objects.md`](01_classes_and_objects.md) —— 我们这里写的方法都是 `static` 的（属于类本身的方法）。

但 Java 真正的核心是"**对象**"。下一篇讲：
- **类**到底是什么
- **对象**和类有什么区别
- 怎么定义自己的类、然后 `new` 出对象来用

---

## 8. 自查

- [ ] 不查文档能写出"方法 = 修饰符 + 返回类型 + 名字 + 参数 + 方法体"的形态
- [ ] 写一个 `square(int x)` 方法返回 x²，并在 main 里调用
- [ ] 写一个 `void` 方法（不返回任何值）打印一段消息
- [ ] 解释 `return` 和 `void` 的区别
- [ ] 写一个 `tryChange(int[] arr)` 改数组内容的例子，证明数组参数能"看见"修改
- [ ] 写一组重载方法（`max(int,int)` / `max(int,int,int)` / `max(double,double)`）
