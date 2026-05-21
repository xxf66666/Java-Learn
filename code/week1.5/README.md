# Week 1.5 · 代码

> 配套笔记：[`../../docs/week1.5/`](../../docs/week1.5/)

## 模块清单

| 子目录 | 主题 | 入口 |
|--------|------|------|
| `primitives/` | 整数溢出 / 浮点精度 / BigDecimal / Integer 缓存 / 装箱性能 | `PrimitiveDemo.main` |
| `arrays/` | 一维/二维数组 / Arrays 工具 / varargs | `ArrayDemo.main` |
| `strings/` | 不可变 / 常量池 / StringBuilder 性能 / 常用方法 / 文本块 / null 安全比较 | `StringDemo.main` |
| `modifiers/` | static / final / 常量惯例 + 类加载顺序 | `ModifierDemo.main` |
| `enums/` | 基础 / 带字段 / 实现接口 / switch / EnumMap | `EnumDemo.main` |
| `inner_classes/` | 静态内部类（Builder）/ 匿名类 vs Lambda / 方法引用 / 捕获变量 | `InnerClassDemo.main` |
| `generics/` | 泛型类 / 泛型方法 / PECS / ? extends / 类型擦除 | `GenericsDemo.main` |
| `annotations/` | 自定义注解 / 反射 new / 反射调方法 / 读注解 / 迷你 IoC | `AnnotationReflectionDemo.main` |

## 命令行运行

```bash
cd code/week1.5
javac -d build $(find . -name "*.java")

java -cp build primitives.PrimitiveDemo
java -cp build arrays.ArrayDemo
java -cp build strings.StringDemo
java -cp build modifiers.ModifierDemo
java -cp build enums.EnumDemo
java -cp build inner_classes.InnerClassDemo
java -cp build generics.GenericsDemo
java -cp build annotations.AnnotationReflectionDemo
```

## 本周自查

- [ ] 把每个 demo 跑一遍，对照笔记看输出
- [ ] PrimitiveDemo：装箱性能差异在你机器上是多少？
- [ ] StringDemo：StringBuilder vs + 拼接 5 万次，慢多少？
- [ ] EnumDemo：能不能加一个 `MOD` 取模操作到 `Op` 枚举？
- [ ] InnerClassDemo：把 Builder 改成不用静态内部类，看代码变多少
- [ ] GenericsDemo：试着把 `sumAll` 改成 `List<? super Number>`，编译能过吗？为什么？
- [ ] AnnotationReflectionDemo：给 `User.setName` 加 `@LogTime`，写代码自动打印所有标了 @LogTime 的方法名
