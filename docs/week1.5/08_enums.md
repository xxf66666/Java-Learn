# Week 1.5 §08 · 枚举

> "状态"、"类型"、"币种"、"性别"... 这些字段**只有有限几个值**。用枚举比用字符串安全很多。

---

## 1. 最简单的枚举

```java
public enum Status {
    DRAFT,
    APPROVED,
    DONE,
    VOIDED
}
```

`enum` 是关键字，定义一个**枚举类型**。

```java
// 使用：用类名.值
Status s = Status.DRAFT;

// == 比较枚举值是 OK 的（枚举值是单例对象）
if (s == Status.APPROVED) { ... }

// values() 拿到所有枚举值
for (Status v : Status.values()) {
    System.out.println(v);            // 默认 toString = 枚举名
}

// 字符串 ↔ 枚举互转
Status x = Status.valueOf("DRAFT");   // "DRAFT" → Status.DRAFT
String name = x.name();                // → "DRAFT"
int idx = x.ordinal();                  // 索引：0
```

---

## 2. 枚举 vs 字符串常量

**没枚举时**人们经常用字符串：

```java
String status = "DRAFT";
if (status.equals("DRFT")) { ... }     // 拼错了，运行时才发现
```

**用枚举**：

```java
Status status = Status.DRAFT;
if (status == Status.DRFT) { ... }     // ❌ 编译错误：DRFT 不存在
```

编译器帮你抓拼写错。**只要值是"有限的几个"，永远优先选枚举**。

---

## 3. 带字段和方法的枚举

枚举本质是一个类，可以有字段、构造器、方法。

```java
public enum OrderStatus {
    // 枚举值后面括号传**构造器参数**
    DRAFT("草稿", 1),
    APPROVED("已审核", 2),
    DONE("已完成", 3),
    VOIDED("已作废", 9);     // 最后一行用分号结尾（如果后面还有方法）

    // 字段：必须 final（枚举值是单例，字段不可变）
    private final String label;
    private final int weight;

    // 构造器：必须 private（或不写访问修饰符，默认就是私有）
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

**关键点**

- 枚举值放最上面，**用括号传构造器参数**
- 字段必须 `final`
- 构造器**不能 `public`**（枚举值只能在自己内部创建）

---

## 4. switch 配合枚举

```java
public String describe(OrderStatus s) {
    return switch (s) {
        case DRAFT    -> "草稿状态";
        case APPROVED -> "已审核状态";
        case DONE     -> "已完成状态";
        case VOIDED   -> "已作废状态";
        // 不用写 default：编译器自动检查所有枚举值是否都覆盖了
    };
}
```

写 case 时**不用写枚举类型前缀**（编译器从 switch 表达式自动推断）。

---

## 5. 枚举实现接口

让不同枚举值有**不同行为**（策略模式的极简实现）：

```java
public interface Operation {
    int apply(int a, int b);
}

public enum Op implements Operation {
    // 每个枚举值后用 { } 重写接口方法
    ADD {
        @Override public int apply(int a, int b) { return a + b; }
    },
    SUB {
        @Override public int apply(int a, int b) { return a - b; }
    },
    MUL {
        @Override public int apply(int a, int b) { return a * b; }
    };
}

// 用法
int r = Op.ADD.apply(2, 3);    // 5
int r2 = Op.MUL.apply(2, 3);   // 6
```

每个枚举值就是一个"小策略"，不需要写多个 if / else 或子类。

---

## 6. 配套代码

[`code/week1.5/s08_enums/EnumDemo.java`](../../code/week1.5/s08_enums/EnumDemo.java)

跑一遍看：
- 基础枚举的 values / name / valueOf
- 带字段枚举（OrderStatus）
- switch 配合
- 实现接口的枚举（Op）

---

## 7. 自查

- [ ] 写一个 `Gender` 枚举（MALE / FEMALE）
- [ ] 写一个 `OrderStatus` 枚举，带 `label`（中文显示名）和 `weight` 字段
- [ ] 用 switch 表达式处理所有 OrderStatus，故意漏一个看编译报错
- [ ] 写一个 `Op` 枚举实现 Operation 接口（ADD / SUB / MUL / DIV）
- [ ] 解释"枚举优于字符串常量"的两个理由
