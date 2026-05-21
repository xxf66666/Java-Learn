# Week 1.5 · 语法专项（Java Language Deep Dive）

> 这是 **Week 1 和 Week 2 之间的补充周**。Week 1 速通了语法，但有很多易错点和细节没讲透。本周把它们系统补齐，**学完 Week 2 之前**先消化掉。
>
> 阅读顺序：按编号即可。每一节都有配套代码可以跑、可以 debug。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_primitives_and_conversions.md`](00_primitives_and_conversions.md) | 基本类型、装箱拆箱、Integer 缓存、整数溢出、浮点精度 |
| 01 | [`01_arrays_and_varargs.md`](01_arrays_and_varargs.md) | 数组（一维 / 二维）、Arrays 工具类、可变参数 varargs |
| 02 | [`02_strings_in_depth.md`](02_strings_in_depth.md) | String 不可变、字符串池、StringBuilder、文本块 |
| 03 | [`03_modifiers.md`](03_modifiers.md) | 全部修饰符：访问 + static / final / abstract / synchronized / volatile / transient |
| 04 | [`04_enums.md`](04_enums.md) | 枚举：基础 + 带字段方法 + 实现接口 + EnumMap |
| 05 | [`05_inner_classes_lambdas.md`](05_inner_classes_lambdas.md) | 内部类 4 种 + 匿名类 + Lambda 对比 |
| 06 | [`06_generics_advanced.md`](06_generics_advanced.md) | 通配符 ? extends / ? super、PECS、类型擦除 |
| 07 | [`07_annotations_reflection.md`](07_annotations_reflection.md) | 自定义注解 + 反射读取 + 动态创建对象 |

## 配套代码

→ [`../../code/week1.5/`](../../code/week1.5/)

## 本周里程碑

到周末你应该能：
- 解释为什么 `Integer x=200,y=200; x==y` 是 `false`
- 用 BigDecimal 而不是 double 算钱，知道为啥
- 自己写一个可变参数方法
- 不查文档说出全部 8 个非访问修饰符的作用
- 设计一个带"积分倍数"字段的 `MemberLevel` 枚举
- 解释 Lambda 和匿名内部类的差异（编译产物 / 作用域）
- 解释 `List<? extends Number>` 和 `List<? super Integer>` 各能做什么
- 自定义一个注解并用反射读取它

## 为什么把这一周插在这里

- Week 1 速通后，**直接进入 Week 2 学 OOP** 会被各种"小语法坑"卡住
- 这些坑里最常见的：Integer 缓存、equals/hashCode 配套、修饰符含义、Lambda 用法等
- 学完 Week 1.5 再去看 Week 2 的 `Student`、`Shape` 例子会**完全没有阻力**
