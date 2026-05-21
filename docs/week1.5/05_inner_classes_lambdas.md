# Week 1.5 §05 · 内部类与 Lambda

> Java 有 4 种"在类里嵌套定义类"的方式 + Lambda。本节理清它们什么场景用。

---

## 1. 四种内部类总览

```
1. 静态内部类      static class Inner { ... }
2. 成员内部类      class Inner { ... }（非 static）
3. 局部内部类      在方法里 class Inner { ... }
4. 匿名内部类      new SomeInterface() { ... }
```

加上 Lambda（5），日常实际只常用 **静态内部类** 和 **Lambda**。

---

## 2. 静态内部类（最常用）

```java
public class Outer {

    private int x;

    public static class Inner {       // static = 不依赖 Outer 实例
        private int y;
        public int getY() { return y; }
    }
}

// 使用：不需要 Outer 实例
Outer.Inner inner = new Outer.Inner();
```

**典型场景**：嵌套的数据类、Builder、工具类

```java
public class HttpRequest {
    private String url;
    private Map<String, String> headers;

    // Builder 是静态内部类，专门用来构造 HttpRequest
    public static class Builder {
        private String url;
        private Map<String, String> headers = new HashMap<>();

        public Builder url(String url) { this.url = url; return this; }
        public Builder header(String k, String v) { headers.put(k, v); return this; }

        public HttpRequest build() {
            HttpRequest r = new HttpRequest();
            r.url = this.url;
            r.headers = this.headers;
            return r;
        }
    }
}

// 用法
HttpRequest req = new HttpRequest.Builder()
    .url("https://...")
    .header("Auth", "Bearer xxx")
    .build();
```

---

## 3. 成员内部类（少用）

```java
public class Outer {
    private int x = 10;

    public class Inner {        // 非 static
        public int getX() {
            return x;           // 能直接访问 Outer 的字段
        }
    }
}

// 使用：必须先有 Outer 实例
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();    // 奇葩语法
```

**缺点**：每个 Inner 隐式持有 Outer 引用，容易内存泄漏。**能用 static 就用 static**。

---

## 4. 局部内部类（极少用）

```java
public void doSomething() {
    class LocalClass {       // 在方法里定义类
        void run() { ... }
    }
    new LocalClass().run();
}
```

工作中**几乎看不到**，Lambda 替代了它的所有用途。

---

## 5. 匿名内部类（被 Lambda 取代）

没有名字的"一次性类"。Java 8 之前用得很多：

```java
// 老写法：实现 Runnable 接口
Runnable r = new Runnable() {
    @Override
    public void run() {
        System.out.println("hello");
    }
};

// Lambda：等价但简洁得多
Runnable r2 = () -> System.out.println("hello");
```

### 什么时候还要用匿名内部类（不能用 Lambda）

- 接口有**多个抽象方法**（不是函数式接口）
- 需要重写**多个方法**

```java
// 接口有 2 个方法，Lambda 表达不了
interface Handler {
    void onSuccess(String data);
    void onFailure(Exception e);
}

Handler h = new Handler() {
    @Override public void onSuccess(String data) { ... }
    @Override public void onFailure(Exception e) { ... }
};
```

---

## 6. Lambda 表达式（Java 8+）

### 三种形态

```java
// 1. 无参
Runnable r = () -> System.out.println("hi");

// 2. 单参，类型可省略
Consumer<String> c = s -> System.out.println(s);

// 3. 多参 + 多行
BiFunction<Integer, Integer, Integer> add = (a, b) -> {
    int sum = a + b;
    return sum;
};
```

### 函数式接口：Lambda 的"承接容器"

Lambda 必须赋给"**只有一个抽象方法的接口**"（函数式接口）。

```java
@FunctionalInterface      // 加这个注解后编译器会检查接口只有一个抽象方法
interface MyFunc {
    int apply(int x);
}

MyFunc square = x -> x * x;
```

### JDK 提供的 4 个常用函数式接口

| 接口 | 签名 | 用途 |
|------|------|------|
| `Function<T, R>` | `R apply(T t)` | 输入 T 输出 R |
| `Predicate<T>` | `boolean test(T t)` | 判断 / 过滤 |
| `Consumer<T>` | `void accept(T t)` | 消费 |
| `Supplier<T>` | `T get()` | 凭空产出 |

---

## 7. 方法引用（`::`）

更简洁的 Lambda：

```java
// Lambda
list.forEach(s -> System.out.println(s));

// 方法引用：上面这种"Lambda 只是简单调用某方法"可以简写
list.forEach(System.out::println);
```

**四种方法引用**

| 形态 | 例子 | 等价 Lambda |
|------|------|-------------|
| 类::静态方法 | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| 类::实例方法 | `String::length` | `s -> s.length()` |
| 对象::实例方法 | `System.out::println` | `s -> System.out.println(s)` |
| 类::new | `ArrayList::new` | `() -> new ArrayList()` |

---

## 8. Lambda 闭包（捕获外部变量）

Lambda 可以引用外部局部变量，但变量**必须是 final 或事实上 final**（即不能再修改）：

```java
int factor = 10;
Function<Integer, Integer> f = x -> x * factor;    // ✅ 捕获 factor

factor = 20;       // ❌ 编译错误：factor 不能再改了
```

### 循环里的坑

```java
// ❌ 编译错误：i 在循环里变
for (int i = 0; i < 3; i++) {
    pool.submit(() -> System.out.println(i));    // i 不是 effectively final
}

// ✅ 拷一份到 final 局部变量
for (int i = 0; i < 3; i++) {
    int id = i;        // id 在每轮循环里是新变量，effectively final
    pool.submit(() -> System.out.println(id));
}
```

---

## 9. 对比表

| 特性 | 匿名内部类 | Lambda |
|------|----------|--------|
| 编译产物 | 单独的 `.class` 文件 | invokedynamic 指令，不生成新类 |
| this 含义 | 内部类自己 | 外层方法的 this |
| 能否多方法 | ✅ | ❌（只能单方法） |
| 性能 | 略慢（类加载） | 快 |
| 现代代码 | 几乎不用 | **首选** |

---

## 10. 自查

- [ ] 写一个 `Builder` 模式的静态内部类
- [ ] 把一段匿名内部类（如 `new Runnable() {...}`）改写成 Lambda
- [ ] 写四种方法引用各一例（静态 / 实例 / 对象 / new）
- [ ] 写一段循环里用 Lambda 引用循环变量的代码，触发编译错，再修复
- [ ] 解释 Lambda 里的 `this` 指向谁

## 代码示例

→ [`code/week1.5/inner_classes/InnerClassDemo.java`](../../code/week1.5/inner_classes/InnerClassDemo.java)
