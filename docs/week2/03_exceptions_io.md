# Week 2 §03 · 异常 + 文件 IO

> 目标：能正确处理异常，能用现代 API（NIO.2）读写文件。

---

## 1. 异常体系

```
Throwable                          ← 一切的根
  ├── Error                        ← JVM 内部错误，一般不处理（如 OutOfMemoryError）
  └── Exception
        ├── RuntimeException       ← unchecked，编译器不强制处理
        │     ├── NullPointerException
        │     ├── IllegalArgumentException
        │     ├── ArrayIndexOutOfBoundsException
        │     └── ...
        └── (其它 Exception)        ← checked，编译器强制处理
              ├── IOException
              ├── SQLException
              ├── ClassNotFoundException
              └── ...
```

**两类异常的区别**

| 类型 | 编译器强制处理？ | 典型代表 | 设计哲学 |
|------|----------------|---------|---------|
| **checked** | ✅ 必须 try-catch 或 throws | `IOException` / `SQLException` | "调用方必须知道这可能失败" |
| **unchecked**（`RuntimeException`） | ❌ 不强制 | `NPE` / `IllegalArgumentException` | "通常是程序员的 bug" |

---

## 2. try-catch-finally

```java
try {
    int[] arr = new int[3];
    arr[5] = 10;                                   // 抛 ArrayIndexOutOfBoundsException
} catch (ArrayIndexOutOfBoundsException e) {
    System.err.println("数组越界：" + e.getMessage());
    e.printStackTrace();                            // 打印完整栈
} catch (RuntimeException e) {                      // 多 catch（前面更具体，后面更宽泛）
    System.err.println("其它运行时异常：" + e);
} finally {
    System.out.println("无论是否异常都会执行");      // 用于关闭资源（已被 try-with-resources 取代）
}
```

**多类型一起 catch**

```java
try { ... }
catch (IOException | SQLException e) {              // 写法相同就合并
    log.error("IO 或 SQL 异常", e);
}
```

---

## 3. try-with-resources：自动关闭资源（强烈推荐）

任何实现了 `AutoCloseable` 接口的类，都能放进 `try ( ... )` 里，**JVM 自动调用 `.close()`**。

```java
// 旧写法（容易忘 close）
BufferedReader r = null;
try {
    r = new BufferedReader(new FileReader("a.txt"));
    System.out.println(r.readLine());
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (r != null) try { r.close(); } catch (IOException e) { ... }
}

// 现代写法（推荐）
try (BufferedReader r = new BufferedReader(new FileReader("a.txt"))) {
    System.out.println(r.readLine());
} catch (IOException e) {
    e.printStackTrace();
}
// 出 try 块时自动 close，即使中间抛异常也会关
```

**记住**：所有 IO、数据库连接、网络连接，**永远**用 try-with-resources。

---

## 4. throw 和 throws

```java
public void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("age 不能为负: " + age);     // 主动抛
    }
    this.age = age;
}

// throws：声明本方法可能往外抛 checked 异常，调用方必须处理
public String readFirstLine(String path) throws IOException {
    try (var r = new BufferedReader(new FileReader(path))) {
        return r.readLine();
    }
}
```

---

## 5. 自定义异常

```java
// 业务异常（unchecked 推荐，省 try-catch 包袱）
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() { return code; }
}

// 使用
throw new BusinessException(40001, "用户已存在");
```

在 Spring Boot 项目里，自定义业务异常 + 全局异常处理（`@RestControllerAdvice`）是非常常见的搭配，Week 8 详讲。

---

## 6. 文件 IO：用 NIO.2 (`java.nio.file`)

Java 早期的 IO API（`File` / `FileInputStream` / `BufferedReader` 组合）很啰嗦。从 Java 7 起，**用 `Files` + `Path` 替代**。

### 读

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

Path p = Path.of("data.txt");
// 或：Path.of("/tmp", "data.txt") / Paths.get("data.txt")

// 一次性读所有行
List<String> lines = Files.readAllLines(p);

// 一次性读全部为字符串
String content = Files.readString(p);

// 大文件流式读（Java 8+，记得关）
try (var stream = Files.lines(p)) {
    stream.filter(line -> line.startsWith("ERROR"))
          .forEach(System.out::println);
}
```

### 写

```java
// 覆盖写
Files.writeString(p, "hello\nworld\n");

// 写多行
Files.write(p, List.of("line1", "line2"));

// 追加
Files.writeString(p, "another\n", StandardOpenOption.APPEND);
```

### 文件检查

```java
Files.exists(p);
Files.isDirectory(p);
Files.size(p);
Files.delete(p);
Files.createDirectories(Path.of("a/b/c"));     // 递归创建
```

### 列目录

```java
try (var stream = Files.list(Path.of("."))) {
    stream.forEach(System.out::println);
}

// 递归遍历
try (var stream = Files.walk(Path.of("src"))) {
    stream.filter(p2 -> p2.toString().endsWith(".java"))
          .forEach(System.out::println);
}
```

---

## 7. CSV 读取一例

```java
public static List<Student> loadFromCsv(Path csv) throws IOException {
    return Files.readAllLines(csv).stream()
        .skip(1)                                        // 跳过表头
        .map(line -> line.split(","))
        .map(parts -> new Student(parts[0], Integer.parseInt(parts[1])))
        .toList();
}
```

实际生产读 Excel 用 EasyExcel / Apache POI（Week 12 讲）。

---

## 8. 自查

- [ ] 解释 checked 和 unchecked 异常的区别，举一个各自的例子
- [ ] 把一段 "旧式 try-finally close" 改写成 try-with-resources
- [ ] 自定义一个 `NotFoundException(int code, String msg)`
- [ ] 用 `Files.readAllLines` 读一个文件，按某条件过滤后写到另一个文件
- [ ] 用 `Files.walk` 找出当前目录及子目录下所有 .java 文件
- [ ] 解释为什么 `Files.lines()` 返回的 Stream 一定要 try-with-resources（底层有文件句柄）

## 代码示例

→ [`code/week2/io/FileIoDemo.java`](../../code/week2/io/FileIoDemo.java)
→ [`code/week2/student/`](../../code/week2/student/) —— 学生管理系统（综合所有本周知识）
