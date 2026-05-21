# Week 1.5 §02 · 字符串深入

> 字符串是用得最频繁的引用类型，**坑也最多**。

---

## 1. 不可变（Immutable）

`String` 对象一旦创建，**内容不能改**。所有 "修改" 操作都返回**新对象**。

```java
String s = "hello";
s.toUpperCase();              // 返回 "HELLO"，但 s 本身没变
System.out.println(s);        // 还是 "hello"

String s2 = s.toUpperCase();  // 要接住返回值
System.out.println(s2);       // HELLO
```

**为什么设计成不可变**
- **线程安全**：多线程读同一个 String 不需要锁
- **可以缓存 hash**：HashMap 用 String 当 key 时，hash 只算一次
- **字符串常量池能复用**

---

## 2. 字符串常量池

JVM 维护一个"字符串常量池"，相同字面量复用同一对象：

```java
String a = "hello";          // 字面量进常量池
String b = "hello";          // 复用同一对象
a == b                       // true

String c = new String("hello");   // new 强制新建对象
a == c                       // false（不同对象）
a.equals(c)                  // true（内容相同）
```

### 实操准则

- **永远用 `.equals()` 比较字符串**，不要用 `==`
- 字符串字面量自动入池，无需 `intern()`

---

## 3. 字符串拼接

### 三种写法

```java
// 1) + 拼接：编译器在简单场景下会优化成 StringBuilder
String s = "a" + "b" + "c";

// 2) String.format（类似 Python f-string）
String fmt = String.format("Hello, %s! You are %d.", "Alice", 20);

// 3) Java 15+ 实例方法 formatted
String fmt2 = "Hello, %s!".formatted("Alice");

// 4) String.join 拼接 List / 数组
String csv = String.join(",", "a", "b", "c");        // "a,b,c"
String csv2 = String.join(",", List.of("a", "b"));    // "a,b"
```

### 循环里必须用 StringBuilder

```java
// ❌ 慢得离谱
String s = "";
for (int i = 0; i < 10_000; i++) {
    s = s + i;             // 每次新建一个 String（O(n²)）
}

// ✅ 快
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10_000; i++) {
    sb.append(i);          // 内部维护可变 char[]
}
String s = sb.toString();
```

### StringBuilder vs StringBuffer

| | StringBuilder | StringBuffer |
|--|---------------|--------------|
| 线程安全 | ❌ | ✅（方法加 synchronized） |
| 性能 | 快 | 慢 |
| 推荐场景 | 单线程（99%） | 几乎不用 |

**实操**：永远用 `StringBuilder`。多线程场景用其它同步机制，不靠 StringBuffer。

---

## 4. 常用方法速查

```java
String s = "Hello, World!";

// 长度
s.length()                // 13（注意是方法不是属性）

// 大小写
s.toUpperCase()           // "HELLO, WORLD!"
s.toLowerCase()           // "hello, world!"

// 查找
s.indexOf("World")        // 7（找不到返回 -1）
s.contains("World")       // true
s.startsWith("Hello")     // true
s.endsWith("!")           // true

// 截取
s.substring(7)            // "World!"（从索引 7 到末尾）
s.substring(7, 12)        // "World"（[7, 12)，左闭右开）

// 替换
s.replace("World", "Java")    // "Hello, Java!"
s.replaceAll("[aeiou]", "*")  // 正则：替换所有元音

// 切分
"a,b,c".split(",")        // ["a", "b", "c"]
"a b  c".split("\\s+")    // 一个或多个空白拆分

// 拼接
String.join("-", "a", "b", "c")   // "a-b-c"

// trim / strip
"  hello  ".trim()        // "hello"（去首尾 ASCII 空白）
"  hello  ".strip()       // "hello"（Java 11+，支持 Unicode 空白）

// 空判断
"".isEmpty()              // true（长度为 0）
"   ".isEmpty()           // false（有空格不是空）
"   ".isBlank()           // true（Java 11+，全空白也算）

// 转字符数组
s.toCharArray()           // ['H', 'e', 'l', ...]
s.charAt(0)               // 'H'

// 数字转字符串
String.valueOf(42)        // "42"
Integer.toString(42)      // "42"

// 字符串转数字
Integer.parseInt("42")    // 42（失败抛 NumberFormatException）
Double.parseDouble("3.14")
```

---

## 5. 文本块（Java 15+）

跨行字符串，免去 `\n` 拼接：

```java
String json = """
        {
          "name": "Alice",
          "age": 20
        }
        """;
```

- `"""` 包裹，**首个 `"""` 必须独占一行**
- 自动去掉最小的公共前导缩进
- 还支持插值前的 `\` 续行、`%s` 配合 `formatted` 等

---

## 6. char vs String

```java
char c = 'A';             // 单引号 + 一个字符
String s = "A";           // 双引号

// char 是数值类型，能算术
char c2 = (char) (c + 1); // 'B'
int code = 'A';            // 65（ASCII / Unicode 码点）

// String 转 char[]
char[] arr = "abc".toCharArray();
```

---

## 7. 常见坑速查

| 错误写法 | 正确写法 |
|---------|---------|
| `if (s == "abc")` | `if ("abc".equals(s))` |
| `if (s.equals("abc"))` 但 s 可能 null | `if ("abc".equals(s))` 字面量在前防 NPE |
| 循环 `s = s + i` | `StringBuilder` |
| `s.replace("a*b", "x")` 当正则用 | `s.replaceAll("a.*b", "x")` 才是正则 |
| `s.length` | `s.length()`（方法不是属性） |
| `"123".equals(123)` 永远 false | 类型不同；先 `String.valueOf(123)` |

---

## 8. 自查

- [ ] 用 StringBuilder 拼接 1 万个数字，测耗时
- [ ] 跑通本周 demo 看 `==` 和 `equals` 在不同场景的输出
- [ ] 用 `split + map + Collectors.joining` 把 "a,b,c" 处理成 "A;B;C"
- [ ] 写一段会抛 NPE 的字符串比较，再用 "字面量在前" 修复
- [ ] 用文本块写一段 JSON

## 代码示例

→ [`code/week1.5/strings/StringDemo.java`](../../code/week1.5/strings/StringDemo.java)
