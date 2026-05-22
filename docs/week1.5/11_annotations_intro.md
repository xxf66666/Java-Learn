# Week 1.5 §11 · 注解 + 反射初步

> 之前你见过 `@Override`、`@Deprecated`。Spring 后面会大量出现 `@Autowired`、`@Transactional`。
>
> 它们都是**注解**。注解本身不改变代码行为——**但框架可以读取它们去做事**。
>
> 本篇讲注解是什么、怎么自定义、以及"反射"是怎么"读"注解的。是 Spring 原理的钥匙。

---

## 1. 用过的注解

```java
@Override           // 标记：覆盖父类方法（编译器校验拼写）
@Deprecated         // 标记：已废弃（用了会有警告）
@SuppressWarnings   // 标记：闭嘴，别警告了
```

这些是 JDK 自带的。Spring 等框架定义了自己的：

```java
@Component          // Spring：这个类作为 Bean 管理
@Autowired          // Spring：注入依赖
@Transactional      // Spring：开事务
@RequestMapping     // Spring MVC：URL 映射
```

---

## 2. 自定义注解

```java
import java.lang.annotation.*;

@Target(ElementType.METHOD)         // 这个注解能贴在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时仍保留（反射能读到）
public @interface LogTime {
    String value() default "";       // 一个属性，默认值是空串
}
```

**关键语法**

- `@interface` 不是 `interface`！这是注解定义的特殊关键字
- 注解的"属性"用方法形态定义（看着像方法实际是字段声明）
- 有 `default` 时可省略，没有的话使用注解时必须填

使用：

```java
public class MyService {
    @LogTime("查询用户")
    public User query(int id) { ... }
}
```

---

## 3. 元注解（注解的注解）

`@Target` 和 `@Retention` 这种"修饰其它注解的注解"叫**元注解**。常用两个：

### @Target：注解能贴在哪

```java
ElementType.TYPE         // 类 / 接口 / 枚举
ElementType.FIELD         // 字段
ElementType.METHOD        // 方法
ElementType.PARAMETER     // 参数
ElementType.CONSTRUCTOR   // 构造器
ElementType.LOCAL_VARIABLE // 局部变量
```

### @Retention：注解保留到什么阶段

| 值 | 含义 | 谁能读到 |
|----|------|---------|
| `SOURCE` | 编译后丢弃 | 仅编译器（如 @Override） |
| `CLASS` | 字节码里有，运行时丢 | 字节码工具 |
| **`RUNTIME`** | 一直保留 | **反射能读到（最常用）** |

写自己的注解几乎永远用 `RUNTIME`。

---

## 4. 反射：运行时检查类

**反射**让你在**运行时**检查一个对象 / 类的字段、方法、注解、并动态调用方法。

### 拿 Class 对象

```java
Class<?> c1 = User.class;             // 1) 类名.class
Class<?> c2 = user.getClass();        // 2) 实例.getClass()
Class<?> c3 = Class.forName("com.learning.User");   // 3) 字符串
```

### Class 常用方法

```java
c.getName()                  // "com.learning.User"
c.getSimpleName()            // "User"
c.getSuperclass()             // 父类的 Class
c.getDeclaredFields()         // 所有字段（含 private）
c.getDeclaredMethods()        // 所有方法
c.getDeclaredConstructors()   // 所有构造器
```

---

## 5. 反射 new 对象 + 调方法

```java
import java.lang.reflect.*;

Class<User> c = User.class;

// 反射 new：调无参构造器
User u = c.getDeclaredConstructor().newInstance();

// 反射 new：带参构造器
User u2 = c.getDeclaredConstructor(String.class).newInstance("Alice");

// 反射调方法
Method m = c.getDeclaredMethod("setName", String.class);
m.invoke(u, "Bob");          // 在 u 对象上调 setName("Bob")

Object result = c.getDeclaredMethod("getName").invoke(u);   // 调 getName

// 反射访问私有字段
Field f = c.getDeclaredField("name");
f.setAccessible(true);        // 暴力打开 private 访问
String name = (String) f.get(u);
f.set(u, "Carol");
```

---

## 6. 读取注解

```java
Method m = MyService.class.getDeclaredMethod("query", int.class);

if (m.isAnnotationPresent(LogTime.class)) {
    LogTime ann = m.getAnnotation(LogTime.class);
    System.out.println(ann.value());     // "查询用户"
}
```

Spring AOP 就是这种思路：扫描所有 Bean 的方法 → 找到带 `@Transactional` 的 → 织入事务逻辑。

---

## 7. 迷你 IoC：理解 Spring 原理的钥匙

```java
// 自定义注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface MyBean {}

// 业务类
@MyBean
class UserService { ... }

@MyBean
class OrderService { ... }

class NotABean { }     // 没标 @MyBean

// "容器"：扫描 + 反射创建 + 缓存
Map<Class<?>, Object> beans = new HashMap<>();

for (Class<?> c : candidateClasses) {
    if (c.isAnnotationPresent(MyBean.class)) {
        Object bean = c.getDeclaredConstructor().newInstance();
        beans.put(c, bean);
    }
}

// 用 Bean
UserService svc = (UserService) beans.get(UserService.class);
```

Spring 的 `@Component` 自动装配就是这种思路的工业化版本。

---

## 8. 性能提醒

反射比直接调用慢 2-5 倍。**不要在循环热点里反射**；启动 / 配置类场景没问题。Spring 内部会缓存 Method / Class 对象减少重复开销。

---

## 9. 配套代码

[`code/week1.5/s11_annotations/AnnotationDemo.java`](../../code/week1.5/s11_annotations/AnnotationDemo.java)

跑一遍看：
- 拿 Class 对象
- 反射 new + 调方法
- 反射访问 private 字段
- 读取自定义注解
- 迷你 IoC 演示

---

## 10. 自查

- [ ] 写一个 `@Author(String value())` 注解
- [ ] 用反射 new 一个 Dog（带参构造器）
- [ ] 用反射调一个 setter
- [ ] 用反射读一个 private 字段
- [ ] 写代码扫描某个类的所有方法，列出标了 `@Deprecated` 的
- [ ] 解释 Spring 怎么用注解 + 反射实现 `@Component` 自动装配

---

## Week 1.5 至此结束

恭喜！你已经把 Java 的核心打透了：

- **OOP 全套**：方法 / 类 / 对象 / 构造器 / 封装 / 继承 / 多态 / 抽象类 / 接口
- **常用语法**：基本类型 / 字符串 / 集合 / 枚举 / Lambda / 泛型 / 注解反射

现在进 Week 2，你看到的所有代码都能立刻理解结构。
