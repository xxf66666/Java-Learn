# Week 2 · 代码

> 配套笔记：[`../../docs/week2/`](../../docs/week2/)

## 模块清单

| 子目录 | 主题 | 入口 |
|--------|------|------|
| `oop/` | 实体类标准模板：字段、构造器、getter/setter、equals/hashCode/toString | `StudentDemo.main` |
| `shape/` | 抽象类 + 多态 + 接口（含 default 方法 / Lambda 实现接口） | `ShapeDemo.main` |
| `generic/` | 泛型容器 `MyStack<T>` | `MyStack.main` |
| `collections/` | List / Set / Map 各种实现 + 词频统计 | `CollectionsDemo.main` |
| `io/` | NIO.2 文件读写示例 | `FileIoDemo.main` |
| `student/` | **综合**：学生管理系统 CLI（增删改查 + CSV 持久化） | `StudentApp.main` |

## 在 IDEA 里运行

1. 把 `code/week2` 标记为 Sources Root（右键 → Mark Directory as）
2. 打开任一文件，点 `main` 旁的绿三角

## 命令行运行

```bash
cd code/week2
javac -d build $(find . -name "*.java")
java -cp build oop.StudentDemo
java -cp build shape.ShapeDemo
java -cp build collections.CollectionsDemo
java -cp build generic.MyStack
java -cp build io.FileIoDemo
java -cp build student.StudentApp
```

## 本周自查

- [ ] `Student` 完整跑通：能 new、能 setter 校验、能 equals 比较、能放 HashSet 去重
- [ ] `Shape` 多态：父类引用 + 子类实例，方法调用走的是实际类型
- [ ] `MyStack<T>` 装不同类型都不报错
- [ ] 词频统计能跑出正确结果
- [ ] `FileIoDemo` 完整跑过，理解 try-with-resources
- [ ] 学生管理系统：完整跑一遍菜单、添加几条数据、关闭重开能加载到上次保存的内容
