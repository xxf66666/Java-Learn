# Week 1.5 · 语法专项（OOP 基础 + 常用语法）

> Week 1 让你能在 IDEA 里敲出能跑的代码。但你只学会了"语法长什么样"，没学会"**怎么用 Java 思考**"。
>
> 本周从最底层重新讲：**方法 → 类 → 对象 → 构造器 → 封装 → 继承 → 多态 → 抽象类 / 接口**，然后再补常用语法（基本类型、字符串、数组、枚举、Lambda、泛型、注解）。
>
> 学完这周再进 Week 2，写代码会**完全没有阻力**。

## 笔记顺序

### 地基篇（必学，按序读）

| 序号 | 文件 | 核心概念 |
|------|------|---------|
| 00 | [`00_methods.md`](00_methods.md) | **方法** —— Java 里"函数"的正式名字 |
| 01 | [`01_classes_and_objects.md`](01_classes_and_objects.md) | **类与对象** —— 蓝图 vs 实例 |
| 02 | [`02_constructors_and_this.md`](02_constructors_and_this.md) | **构造器与 this** —— 对象诞生的入口 |
| 03 | [`03_encapsulation_and_static.md`](03_encapsulation_and_static.md) | **封装** + **static** |
| 04 | [`04_inheritance_and_polymorphism.md`](04_inheritance_and_polymorphism.md) | **继承** + **多态** |
| 05 | [`05_abstract_and_interface.md`](05_abstract_and_interface.md) | **抽象类** vs **接口** |

### 常用语法篇（可按需读）

| 序号 | 文件 | 主题 |
|------|------|------|
| 06 | [`06_primitives_and_strings.md`](06_primitives_and_strings.md) | 基本类型 + 包装类 + 字符串 |
| 07 | [`07_arrays_and_collections_intro.md`](07_arrays_and_collections_intro.md) | 数组 + List / Map 初见 |
| 08 | [`08_enums.md`](08_enums.md) | 枚举 |
| 09 | [`09_lambda_intro.md`](09_lambda_intro.md) | Lambda 入门 |
| 10 | [`10_generics_intro.md`](10_generics_intro.md) | 泛型入门 |
| 11 | [`11_annotations_intro.md`](11_annotations_intro.md) | 注解 + 反射初步 |

## 配套代码

→ [`../../code/week1.5/`](../../code/week1.5/)

每一篇都配一个独立的 demo，可以在 IDEA 里直接点小三角运行。

## 学习节奏建议

- **00-05 地基篇**：每天 1-2 篇，每篇配套代码跑一遍 + debug 一遍 → 5 天
- **06-11 语法篇**：每天 1-2 篇，挑感兴趣的先读 → 3-5 天

> 不一定要全部学完才往后走。**00-05 是硬底子**，至少要会。06-11 在 Week 2 实战里也会反复遇到，遇到再回来翻也行。

## 本周里程碑

学完地基篇后你应该能：
- 自己设计一个类（字段 + 方法 + 构造器），并 new 出对象使用
- 解释"什么是对象"、"对象在内存里长什么样"
- 写出有 `private` 字段 + `public` getter/setter 的类
- 写一个父类 + 两个子类，并演示多态
- 解释 `abstract class` 和 `interface` 的差异，知道什么时候用哪个

学完语法篇后你应该能：
- 解释 Integer 缓存的坑、用 BigDecimal 算钱
- 用 ArrayList 和 HashMap 完成基本数据操作
- 设计带字段的枚举（如订单状态）
- 看懂 Lambda 表达式、写出最简单的 Stream 操作
- 看懂 `<T>` 和 `<? extends Number>` 是什么意思
- 自定义一个注解并用反射读取它
