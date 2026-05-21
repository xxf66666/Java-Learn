package generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Week 1.5 §06 配套示例：泛型进阶
 */
public class GenericsDemo {

    /** 泛型类：一个能装任意类型的"盒子" */
    static class Box<T> {
        // T 在编译期是占位符，运行时被擦除成 Object
        private T value;

        public void set(T value) { this.value = value; }
        public T get() { return value; }
    }

    /**
     * 泛型方法：&lt;T extends Comparable&lt;T&gt;&gt; 要求 T 必须能比较
     */
    static <T extends Comparable<T>> T max(List<T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("list 不能为空");
        T best = list.get(0);
        // for-each 拿到的元素类型就是 T
        for (T x : list) {
            // compareTo > 0 表示 x 比 best 大
            if (x.compareTo(best) > 0) best = x;
        }
        return best;
    }

    /**
     * PECS 经典例子：从 src 拷到 dest
     *
     * src 是 Producer（提供数据）→ ? extends T 上界
     * dest 是 Consumer（接收数据）→ ? super T 下界
     */
    static <T> void copyAll(List<? super T> dest, List<? extends T> src) {
        // 从 src 读：能拿出 T
        for (T item : src) {
            // 往 dest 写：T 一定能放进"T 或父类"的 List
            dest.add(item);
        }
    }

    /**
     * 求和：?  extends Number 表示能接受 Integer / Double / Long 等的 List
     */
    static double sumAll(List<? extends Number> nums) {
        double total = 0;
        for (Number n : nums) {
            // doubleValue() 是 Number 抽象类的方法
            total += n.doubleValue();
        }
        return total;
    }

    public static void main(String[] args) {
        boxDemo();
        maxDemo();
        pecsDemo();
        sumDemo();
        erasureDemo();
    }

    static void boxDemo() {
        System.out.println("\n=== 泛型类 Box<T> ===");

        // String 版
        Box<String> sb = new Box<>();
        sb.set("hello");
        String s = sb.get();         // 不用强转，编译器知道返回 String
        System.out.println("String Box: " + s);

        // Integer 版
        Box<Integer> ib = new Box<>();
        ib.set(42);
        int i = ib.get();
        System.out.println("Integer Box: " + i);
    }

    static void maxDemo() {
        System.out.println("\n=== 泛型方法 max ===");

        // Integer 实现 Comparable<Integer>，OK
        Integer maxI = max(List.of(3, 1, 4, 1, 5, 9, 2, 6));
        System.out.println("max int: " + maxI);

        // String 实现 Comparable<String>，OK
        String maxS = max(List.of("banana", "apple", "cherry"));
        System.out.println("max str: " + maxS);
    }

    static void pecsDemo() {
        System.out.println("\n=== PECS ===");

        List<Integer> ints = new ArrayList<>(Arrays.asList(1, 2, 3));
        List<Number> numbers = new ArrayList<>();

        // 把 List<Integer> 拷进 List<Number>
        // src 是 Producer (? extends Integer)，dest 是 Consumer (? super Integer)
        copyAll(numbers, ints);
        System.out.println("拷贝后: " + numbers);
    }

    static void sumDemo() {
        System.out.println("\n=== ? extends Number ===");

        // Integer 是 Number 子类，List<Integer> 是 List<? extends Number>
        System.out.println("sum int: " + sumAll(List.of(1, 2, 3)));

        // Double 也是 Number 子类
        System.out.println("sum double: " + sumAll(List.of(1.5, 2.5)));

        // ⚠️ List<String> 不行，String 不是 Number
        // sumAll(List.of("a"));   // 编译错误
    }

    /** 类型擦除：运行时看不到泛型参数 */
    @SuppressWarnings("rawtypes")
    static void erasureDemo() {
        System.out.println("\n=== 类型擦除 ===");

        List<String> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();

        // 运行时 a 和 b 是同一个 class
        System.out.println("a.class = " + a.getClass());
        System.out.println("b.class = " + b.getClass());
        System.out.println("same class? " + (a.getClass() == b.getClass()));

        // 因为擦除，运行时能往 "raw" List 里塞任何类型
        // 编译器只是警告，运行不报错（直到你按错误类型取出来）
        List rawList = a;          // 警告但能编译
        rawList.add(42);            // 把 int 放进了"曾经的 List<String>"！

        // 取出来强转 String 时崩
        try {
            String s = a.get(0);     // ClassCastException
        } catch (ClassCastException e) {
            System.out.println("擦除导致的运行时异常: " + e);
        }
    }
}
