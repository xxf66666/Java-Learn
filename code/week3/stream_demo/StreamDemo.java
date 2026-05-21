package stream_demo;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class StreamDemo {

    record Employee(String name, String dept, double salary) {}

    public static void main(String[] args) {
        lambdaBasics();
        streamBasics();
        groupAndAgg();
        optionalDemo();
    }

    static void lambdaBasics() {
        System.out.println("\n=== Lambda ===");
        Function<String, Integer> len = String::length;
        Predicate<String> nonEmpty = s -> !s.isEmpty();
        Consumer<String> printer = System.out::println;
        Supplier<Double> random = Math::random;

        System.out.println("len(hello) = " + len.apply("hello"));
        System.out.println("nonEmpty(\"\") = " + nonEmpty.test(""));
        printer.accept("from consumer");
        System.out.println("random = " + random.get());
    }

    static void streamBasics() {
        System.out.println("\n=== Stream 基础 ===");
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // filter + map + toList
        List<Integer> evenSquared = nums.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * n)
            .toList();
        System.out.println("偶数的平方: " + evenSquared);

        // reduce 求和
        int sum = nums.stream().reduce(0, Integer::sum);
        System.out.println("求和 = " + sum);

        // count + anyMatch / allMatch
        long cnt = nums.stream().filter(n -> n > 5).count();
        System.out.println("> 5 的有 " + cnt + " 个");
        System.out.println("都 > 0? " + nums.stream().allMatch(n -> n > 0));
    }

    static void groupAndAgg() {
        System.out.println("\n=== 分组聚合 ===");
        List<Employee> emps = List.of(
            new Employee("Alice", "ENG", 100),
            new Employee("Bob",   "ENG", 80),
            new Employee("Carol", "HR",  60),
            new Employee("Dave",  "HR",  70),
            new Employee("Eve",   "ENG", 120)
        );

        // 按部门分组
        Map<String, List<Employee>> byDept = emps.stream()
            .collect(Collectors.groupingBy(Employee::dept));
        byDept.forEach((dept, list) -> {
            System.out.println("  " + dept + " -> " + list.size() + " 人");
        });

        // 各部门平均薪资
        Map<String, Double> avg = emps.stream()
            .collect(Collectors.groupingBy(Employee::dept,
                     Collectors.averagingDouble(Employee::salary)));
        System.out.println("各部门平均薪资: " + avg);

        // 找最高薪
        Optional<Employee> top = emps.stream()
            .max(Comparator.comparingDouble(Employee::salary));
        top.ifPresent(e -> System.out.println("最高薪: " + e.name() + " " + e.salary()));

        // 姓名拼接
        String names = emps.stream()
            .map(Employee::name)
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("所有人: " + names);
    }

    static void optionalDemo() {
        System.out.println("\n=== Optional ===");
        Optional<String> opt = Optional.ofNullable(null);
        String v = opt.orElse("默认值");
        System.out.println("orElse: " + v);

        Optional.of("hello").map(String::toUpperCase).ifPresent(System.out::println);
    }
}
