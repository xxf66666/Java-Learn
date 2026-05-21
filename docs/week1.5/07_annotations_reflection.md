# Week 1.5 §07 · 注解 + 反射初步

> Spring 大量基于"注解 + 反射"运作。理解原理之后再用 `@Autowired` / `@Transactional` 心里就不慌了。

---

## 1. 什么是注解

注解 = 给代码加"元数据"标签。本身不改变代码行为，但**框架可以读取这些标签去做事**。

```java
@Override                          // 标记：覆盖父方法
@Deprecated                        // 标记：已废弃
@SuppressWarnings("unchecked")    // 标记：忽略警告

@Component                         // Spring：把这个类作为 Bean
@Autowired                         // Spring：注入依赖
@Transactional                     // Spring：开事务
```

---

## 2. 自定义注解

```java
import java.lang.annotation.*;

// 元注解（注解的注解）：
@Target(ElementType.METHOD)         // 用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时仍保留，反射能读到
public @interface LogTime {
    String value() default "";       // 属性
    boolean enabled() default true;
}
```

### 元注解四件套

| 元注解 | 含义 |
|--------|------|
| `@Target` | 这个注解能贴在哪（类 / 方法 / 字段...） |
| `@Retention` | 注解保留到什么时期 |
| `@Documented` | 是否出现在 Javadoc |
| `@Inherited` | 子类是否继承父类的这个注解 |

### `@Target` 可选值

```java
ElementType.TYPE          // 类、接口、枚举
ElementType.FIELD          // 字段
ElementType.METHOD         // 方法
ElementType.PARAMETER      // 参数
ElementType.CONSTRUCTOR    // 构造器
ElementType.LOCAL_VARIABLE // 局部变量
ElementType.ANNOTATION_TYPE // 注解本身
ElementType.PACKAGE        // 包
```

### `@Retention` 三档

| 值 | 含义 | 谁能看到 |
|----|------|---------|
| `SOURCE` | 源码 → 编译丢弃 | 仅编译器（如 @Override） |
| `CLASS` | 编译保留 → 运行时丢弃 | 字节码工具 |
| `RUNTIME` | 一直保留 | **反射能读到（最常用）** |

### 注解的"属性"

注解的属性是用方法定义的：

```java
public @interface Audit {
    String operation() default "";
    String[] targets() default {};        // 数组也行
    int level() default 1;
}

// 使用时给属性赋值
@Audit(operation = "删除用户", targets = {"db", "log"}, level = 2)
public void deleteUser(Long id) { ... }
```

### `value()` 的特殊待遇

```java
public @interface Tag {
    String value();
}

@Tag(value = "demo")        // 标准写法
@Tag("demo")                 // 简写：只有一个属性且名字是 value 时可省略
```

---

## 3. 反射：运行时检查类

反射 = 程序在运行时**检查和调用**类、方法、字段的能力。

### 拿 Class 对象的 3 种方式

```java
// 1. 类名.class（最常用）
Class<User> c1 = User.class;

// 2. 实例.getClass()
User u = new User();
Class<?> c2 = u.getClass();

// 3. Class.forName（字符串）
Class<?> c3 = Class.forName("com.learning.User");
```

### Class 常用方法

```java
Class<?> c = User.class;

c.getName();                            // com.learning.User
c.getSimpleName();                       // User
c.getSuperclass();                       // 父类的 Class
c.getInterfaces();                       // 实现的接口

c.getDeclaredFields();                   // 所有字段（含 private）
c.getDeclaredMethods();                  // 所有方法
c.getConstructors();                     // 所有 public 构造器
```

---

## 4. 反射创建对象 + 调方法

```java
public class User {
    private String name;
    public User() {}
    public User(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// 反射 new
Class<User> c = User.class;
User u = c.getDeclaredConstructor().newInstance();         // 调无参构造器
User u2 = c.getDeclaredConstructor(String.class).newInstance("Alice");

// 反射调方法
Method m = c.getDeclaredMethod("setName", String.class);
m.invoke(u, "Bob");
String name = (String) c.getDeclaredMethod("getName").invoke(u);

// 反射访问私有字段（暴力破解）
Field f = c.getDeclaredField("name");
f.setAccessible(true);                  // 跳过访问检查
f.set(u, "Carol");
String value = (String) f.get(u);
```

---

## 5. 读取注解（实战）

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogTime {
    String value() default "";
}

public class Service {
    @LogTime("查询")
    public String query(int id) { return "result"; }
}

// 读取
Method m = Service.class.getDeclaredMethod("query", int.class);
if (m.isAnnotationPresent(LogTime.class)) {
    LogTime ann = m.getAnnotation(LogTime.class);
    System.out.println(ann.value());       // "查询"
}
```

Spring AOP 就是用这种方式找到带 `@Transactional` 的方法，然后织入事务逻辑。

---

## 6. 反射 + 注解 = 自己实现一个迷你 IoC

```java
// 注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface MyComponent {}

// 业务类
@MyComponent
class UserService {
    public void doIt() { System.out.println("doing..."); }
}

// 迷你容器
Map<Class<?>, Object> beans = new HashMap<>();

// 扫描指定包，找到带 @MyComponent 的类，反射 new
for (Class<?> c : scanPackage("com.learning")) {
    if (c.isAnnotationPresent(MyComponent.class)) {
        beans.put(c, c.getDeclaredConstructor().newInstance());
    }
}

// 用
UserService svc = (UserService) beans.get(UserService.class);
svc.doIt();
```

Spring 内部就是这套思路的工业化版本（加上依赖注入、AOP、生命周期管理）。

---

## 7. 性能与限制

- 反射比直接调用**慢 2-5 倍**（涉及方法签名查找）
- 关键路径不要用反射；启动 / 配置类操作可以用
- Spring / MyBatis 等框架内部会缓存 Method / Class 对象，减少重复反射开销
- 反射会**绕过 private**，破坏封装，慎用

---

## 8. 自查

- [ ] 写一个 `@Author(String value())` 注解
- [ ] 用反射拿到 String 类的所有 public 方法
- [ ] 用反射 new 一个 User 对象并 set name
- [ ] 写一段代码扫描某个类的所有方法，列出带 `@Deprecated` 的
- [ ] 解释 Spring 怎么用反射 + 注解实现 @Component 自动装配

## 代码示例

→ [`code/week1.5/annotations/AnnotationReflectionDemo.java`](../../code/week1.5/annotations/AnnotationReflectionDemo.java)
