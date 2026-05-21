# Week 1.5 §04 · 枚举（enum）

> 枚举不是"几个常量"，它是 Java 里一个完整的特殊类。用好它能让代码可读性 + 类型安全双赢。

---

## 1. 基础用法

```java
public enum Status {
    DRAFT,
    APPROVED,
    DONE,
    VOIDED
}

// 使用
Status s = Status.DRAFT;
if (s == Status.APPROVED) { ... }     // 枚举值用 == 比较是 OK 的

// 遍历所有枚举值
for (Status v : Status.values()) {
    System.out.println(v);            // 默认 toString = 枚举名
}

// 名字 ↔ 枚举值互转
Status.valueOf("DRAFT");              // 字符串转枚举（找不到抛 IllegalArgumentException）
s.name();                              // 枚举转字符串
s.ordinal();                           // 枚举的位置索引（0 1 2...）
```

---

## 2. 带字段和方法的枚举

枚举本质是一个类，可以有字段、构造器、方法：

```java
public enum OrderStatus {
    // 枚举值后面括号传构造器参数
    DRAFT("草稿", 1),
    APPROVED("已审核", 2),
    DONE("已完成", 3),
    VOIDED("已作废", 9);

    // 字段必须 final（一旦确定不再变）
    private final String label;
    private final int weight;

    // 构造器必须 private 或默认（不能 public）
    OrderStatus(String label, int weight) {
        this.label = label;
        this.weight = weight;
    }

    public String getLabel() { return label; }
    public int getWeight() { return weight; }
}

// 用法
OrderStatus s = OrderStatus.DRAFT;
s.getLabel();    // "草稿"
s.getWeight();   // 1
```

---

## 3. 枚举实现接口

让不同枚举值有**不同行为**：

```java
public interface Operation {
    int apply(int a, int b);
}

public enum Op implements Operation {
    ADD {
        @Override
        public int apply(int a, int b) { return a + b; }
    },
    SUB {
        @Override
        public int apply(int a, int b) { return a - b; }
    },
    MUL {
        @Override
        public int apply(int a, int b) { return a * b; }
    };
}

// 用法
int r = Op.ADD.apply(2, 3);   // 5
int r2 = Op.MUL.apply(2, 3);  // 6
```

这是策略模式的极简实现，比 if/else 强很多。

---

## 4. switch 配合枚举

```java
public String describe(Status s) {
    return switch (s) {
        case DRAFT    -> "草稿";
        case APPROVED -> "已审核";
        case DONE     -> "已完成";
        case VOIDED   -> "已作废";
        // 不用写 default，枚举类型 switch 编译器会检查穷尽性
    };
}
```

---

## 5. EnumMap / EnumSet（性能优化）

当 key 是枚举时，**用 `EnumMap` 而不是 `HashMap`**：

```java
import java.util.EnumMap;

// EnumMap：内部用数组，按 ordinal 寻址，比 HashMap 快几倍
EnumMap<Status, String> labels = new EnumMap<>(Status.class);
labels.put(Status.DRAFT, "草稿");
labels.put(Status.APPROVED, "已审核");

// EnumSet：同理，集合操作更快
EnumSet<Status> activeStatuses = EnumSet.of(Status.DRAFT, Status.APPROVED);
EnumSet<Status> all = EnumSet.allOf(Status.class);
```

---

## 6. 枚举 vs 常量字符串

```java
// ❌ 字符串常量：编译期不检查、易拼错
public static final String DRAFT = "DRAFT";
if (status.equals("DRFT")) { ... }       // 拼错了，运行时才发现

// ✅ 枚举：编译器替你检查
public enum Status { DRAFT, APPROVED }
if (status == Status.DRFT) { ... }       // ❌ 编译错误：DRFT 不存在
```

**规则**：状态、类型、单据种类、币种 ... 凡是"有限的几个值"，**用枚举**。

---

## 7. 序列化（数据库 / JSON）

### 存数据库

```java
// 实体类
@TableField(value = "status")
private OrderStatus status;

// MyBatis-Plus 默认把枚举存成 name（"DRAFT"）
// 想存 ordinal 或自定义值需要 TypeHandler
```

### JSON

```java
// 默认 Jackson 把枚举序列化成 name
ObjectMapper m = new ObjectMapper();
m.writeValueAsString(Status.DRAFT);     // "DRAFT"

// 想自定义可以加 @JsonValue 到某个 getter
public enum Status {
    DRAFT("draft");
    private final String code;
    Status(String code) { this.code = code; }
    @JsonValue
    public String getCode() { return code; }
}
// 输出 "draft" 而不是 "DRAFT"
```

---

## 8. 高级：抽象方法

每个枚举值实现自己的"方法体"：

```java
public enum Calculator {
    PLUS {
        @Override public int op(int a, int b) { return a + b; }
    },
    MINUS {
        @Override public int op(int a, int b) { return a - b; }
    };

    // 抽象方法，每个枚举常量必须实现
    public abstract int op(int a, int b);
}
```

和"枚举实现接口"效果类似，但不需要单独定义接口。

---

## 9. 自查

- [ ] 写一个 `Gender` 枚举，带 `code` 和 `label` 字段
- [ ] 写一个 `Op` 枚举（ADD / SUB / MUL / DIV），实现 `Operation` 接口
- [ ] 用 `switch` 处理 `OrderStatus` 的所有 case，故意漏一个看编译器是否提醒
- [ ] 用 `EnumMap` 存"状态 → 显示名" 的映射
- [ ] 把枚举序列化成 JSON 看默认输出

## 代码示例

→ [`code/week1.5/enums/EnumDemo.java`](../../code/week1.5/enums/EnumDemo.java)
