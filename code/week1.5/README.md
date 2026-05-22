# Week 1.5 · 代码

> 配套笔记：[`../../docs/week1.5/`](../../docs/week1.5/)

## 模块清单

### 地基篇（按序学）

| 子目录 | 主题 | 入口 |
|--------|------|------|
| `s00_methods/` | 方法：定义、调用、参数传递、重载 | `MethodDemo.main` |
| `s01_classes/` | 类与对象：new、字段、实例方法、null、引用赋值 | `DogDemo.main` |
| `s02_constructors/` | 构造器与 this：构造器形态、重载、`this(...)` 互调 | `DogDemo.main` |
| `s03_encapsulation/` | 封装 + static：private 字段 / setter 校验 / 实例 vs 静态 | `EncapsulationDemo.main` |
| `s04_inheritance/` | 继承 + 多态：extends / super / @Override / instanceof | `InheritanceDemo.main` |
| `s05_abstract_interface/` | 抽象类 + 接口：abstract / interface / implements 多个 | `AbstractInterfaceDemo.main` |

### 常用语法篇

| 子目录 | 主题 | 入口 |
|--------|------|------|
| `s06_primitives_strings/` | 基本类型 / 包装类 / Integer 缓存 / BigDecimal / 字符串 | `PrimitiveStringDemo.main` |
| `s07_arrays_collections/` | 数组 / 二维 / Arrays / varargs / ArrayList / HashMap | `CollectionsDemo.main` |
| `s08_enums/` | 枚举：基础 / 带字段方法 / switch / 实现接口 | `EnumDemo.main` |
| `s09_lambda/` | Lambda：4 个函数式接口 / 方法引用 / Stream 入门 / 变量捕获 | `LambdaDemo.main` |
| `s10_generics/` | 泛型：Box<T> / Pair<K,V> / 泛型方法 / 通配符 / 类型擦除 | `GenericsDemo.main` |
| `s11_annotations/` | 注解 + 反射：自定义注解 / 反射 new / 读注解 / 迷你 IoC | `AnnotationDemo.main` |

## 在 IDEA 里跑

1. 用 IDEA `File → Open` 打开整个 `Java-Learn` 仓库
2. 右键 `code/week1.5` 文件夹 → `将目录标记为` → `源代码根目录`
3. 打开任一 `*Demo.java`，点 `main` 旁的绿色三角

## 命令行运行

```bash
cd code/week1.5
javac -d build $(find . -name "*.java")
java -cp build s00_methods.MethodDemo
java -cp build s01_classes.DogDemo
java -cp build s02_constructors.DogDemo
java -cp build s03_encapsulation.EncapsulationDemo
java -cp build s04_inheritance.InheritanceDemo
java -cp build s05_abstract_interface.AbstractInterfaceDemo
java -cp build s06_primitives_strings.PrimitiveStringDemo
java -cp build s07_arrays_collections.CollectionsDemo
java -cp build s08_enums.EnumDemo
java -cp build s09_lambda.LambdaDemo
java -cp build s10_generics.GenericsDemo
java -cp build s11_annotations.AnnotationDemo
```

## 本周自查

完整自查在每篇 docs 末尾。**地基篇结束时的硬指标**：

- [ ] 不查文档写一个有字段 / 构造器 / getter / setter 的类
- [ ] 写一个父类 + 两个子类，演示多态
- [ ] 解释抽象类和接口在 3 个维度的差异
- [ ] 解释"对象在内存里长什么样"（栈引用 + 堆对象）
- [ ] 解释 `Integer a=200,b=200; a==b` 为什么 false
