package stream_demo;

import java.util.*;
// java.util.function 提供常用函数式接口（Function / Predicate / Consumer / Supplier）
import java.util.function.*;
// Collectors 是 Stream 的"终止操作"工具箱
import java.util.stream.Collectors;

public class StreamDemo {

    // record (Java 16+) 简洁声明"不可变数据类"
    // 自动生成：构造器、字段、getter（叫 name() 不是 getName()）、equals、hashCode、toString
    record Employee(String name, String dept, double salary) {}

    public static void main(String[] args) {
        lambdaBasics();
        streamBasics();
        groupAndAgg();
        optionalDemo();
    }

    // ============ Lambda 基础 ============
    static void lambdaBasics() {
        System.out.println("\n=== Lambda ===");

        // Function<T, R>：输入 T 返回 R
        // String::length 是方法引用，等价于 s -> s.length()
        Function<String, Integer> len = String::length;

        // Predicate<T>：输入 T 返回 boolean，用于判断
        // s -> ... 是 Lambda 表达式，参数 s 推断为 String
        Predicate<String> nonEmpty = s -> !s.isEmpty();

        // Consumer<T>：输入 T 无返回，用于"消费"（打印 / 写日志）
        Consumer<String> printer = System.out::println;

        // Supplier<T>：无输入返回 T，用于"凭空提供"（随机数 / 默认值）
        // Math::random 是引用 Math 类的静态方法
        Supplier<Double> random = Math::random;

        // apply / test / accept / get 是这四个接口分别的"唯一抽象方法"
        System.out.println("len(hello) = " + len.apply("hello"));
        System.out.println("nonEmpty(\"\") = " + nonEmpty.test(""));
        printer.accept("from consumer");
        System.out.println("random = " + random.get());
    }

    // ============ Stream 基础 ============
    static void streamBasics() {
        System.out.println("\n=== Stream 基础 ===");

        // List.of(...) 创建不可变 List
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Stream 三段式：源 → 中间操作 → 终止操作
        // .stream() 把集合转成流
        // .filter(...) 过滤（中间操作，返回新 Stream，懒求值）
        // .map(...)   元素变换（中间操作）
        // .toList()  终止操作，触发计算，收集成 List
        List<Integer> evenSquared = nums.stream()
            .filter(n -> n % 2 == 0)     // 留下偶数
            .map(n -> n * n)              // 平方
            .toList();
        System.out.println("偶数的平方: " + evenSquared);

        // reduce(初始值, BiFunction)：把流"折叠"成单个值
        // Integer::sum 是 (a, b) -> a + b 的方法引用
        int sum = nums.stream().reduce(0, Integer::sum);
        System.out.println("求和 = " + sum);

        // count() 终止操作：流元素个数（long）
        long cnt = nums.stream().filter(n -> n > 5).count();
        System.out.println("> 5 的有 " + cnt + " 个");

        // allMatch：全部满足条件吗？(返回 boolean，终止操作)
        // 还有 anyMatch / noneMatch
        System.out.println("都 > 0? " + nums.stream().allMatch(n -> n > 0));
    }

    // ============ 分组聚合 ============
    static void groupAndAgg() {
        System.out.println("\n=== 分组聚合 ===");

        // record 直接用构造器创建
        List<Employee> emps = List.of(
            new Employee("Alice", "ENG", 100),
            new Employee("Bob",   "ENG", 80),
            new Employee("Carol", "HR",  60),
            new Employee("Dave",  "HR",  70),
            new Employee("Eve",   "ENG", 120)
        );

        // groupingBy(分组键) 按某字段分组，返回 Map<分组键, List<元素>>
        // Employee::dept 取 dept 字段（record 自动生成的同名方法）
        Map<String, List<Employee>> byDept = emps.stream()
            .collect(Collectors.groupingBy(Employee::dept));

        // forEach 遍历 Map 的每个 (key, value) 对
        // list.size() 是该部门的人数
        byDept.forEach((dept, list) -> {
            System.out.println("  " + dept + " -> " + list.size() + " 人");
        });

        // groupingBy(分组键, 下游收集器) 两参版本
        // 下游 averagingDouble(取值函数)：对每组算平均
        Map<String, Double> avg = emps.stream()
            .collect(Collectors.groupingBy(Employee::dept,
                     Collectors.averagingDouble(Employee::salary)));
        System.out.println("各部门平均薪资: " + avg);

        // max 接受 Comparator：找最大的元素
        // Comparator.comparingDouble(取值) 按取出的 double 排序
        // 返回 Optional 因为流可能为空
        Optional<Employee> top = emps.stream()
            .max(Comparator.comparingDouble(Employee::salary));

        // ifPresent：Optional 有值就跑 Lambda
        top.ifPresent(e -> System.out.println("最高薪: " + e.name() + " " + e.salary()));

        // joining(分隔符, 前缀, 后缀)：把流元素拼接为字符串
        String names = emps.stream()
            .map(Employee::name)
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("所有人: " + names);
    }

    // ============ Optional ============
    static void optionalDemo() {
        System.out.println("\n=== Optional ===");

        // Optional.ofNullable(可能为 null 的值) 包装成 Optional
        Optional<String> opt = Optional.ofNullable(null);

        // orElse(默认值)：Optional 为空时用默认值
        String v = opt.orElse("默认值");
        System.out.println("orElse: " + v);

        // Optional.of(非 null 值) → map 变换 → ifPresent 消费
        // 整段链式调用，没有显式 null 判断
        Optional.of("hello").map(String::toUpperCase).ifPresent(System.out::println);
    }
}
