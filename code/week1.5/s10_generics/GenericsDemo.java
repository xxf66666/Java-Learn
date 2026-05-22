package s10_generics;

import java.util.ArrayList;
import java.util.List;

public class GenericsDemo {

    /** 泛型类：T 是类型占位符，使用时填具体类型 */
    static class Box<T> {
        private T value;

        public void set(T value) { this.value = value; }
        public T get() { return value; }
    }

    /** 两个类型参数的泛型类 */
    static class Pair<K, V> {
        K key;
        V value;
        Pair(K k, V v) { this.key = k; this.value = v; }

        @Override
        public String toString() {
            return "(" + key + ", " + value + ")";
        }
    }

    /**
     * 泛型方法：<T> 写在返回类型前
     * 这个方法对任何 T 都能用
     */
    static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 上界：T 必须实现 Comparable<T>，即"能和自己比较"
     */
    static <T extends Comparable<T>> T max(List<T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("空 list");
        T best = list.get(0);
        for (T x : list) {
            if (x.compareTo(best) > 0) best = x;
        }
        return best;
    }

    /**
     * 通配符 ? extends Number：接受 List<Integer> / List<Double> / List<Long> ...
     * 用于"从 list 读出来当 Number 用"
     */
    static double sum(List<? extends Number> nums) {
        double total = 0;
        for (Number n : nums) {       // 取出来当 Number 用
            total += n.doubleValue();
        }
        return total;
    }

    public static void main(String[] args) {
        boxDemo();
        pairDemo();
        genericMethodDemo();
        wildcardDemo();
        erasureDemo();
    }

    static void boxDemo() {
        System.out.println("\n=== Box<T> ===");

        // 装 String
        Box<String> sb = new Box<>();
        sb.set("hello");
        String s = sb.get();         // 不用强转
        System.out.println("String Box: " + s);

        // 装 Integer
        Box<Integer> ib = new Box<>();
        ib.set(42);
        int n = ib.get();             // 自动拆箱
        System.out.println("Integer Box: " + n);

        // sb.set(123);   // ❌ 编译错误：sb 是 Box<String>，不能装 int
    }

    static void pairDemo() {
        System.out.println("\n=== Pair<K, V> ===");

        Pair<String, Integer> age = new Pair<>("Alice", 20);
        Pair<String, String> kv = new Pair<>("color", "red");

        System.out.println(age);
        System.out.println(kv);
    }

    static void genericMethodDemo() {
        System.out.println("\n=== 泛型方法 ===");

        // T 由编译器自动推断
        String s = firstOrNull(List.of("a", "b", "c"));     // T = String
        Integer i = firstOrNull(List.of(1, 2, 3));            // T = Integer
        System.out.println("first String: " + s);
        System.out.println("first Integer: " + i);

        // max 要求 T 能比较
        Integer maxI = max(List.of(3, 1, 4, 1, 5, 9, 2, 6));
        String maxS = max(List.of("banana", "apple", "cherry"));
        System.out.println("max int: " + maxI);
        System.out.println("max str: " + maxS);
    }

    static void wildcardDemo() {
        System.out.println("\n=== 通配符 ===");

        // List<Integer> 可以当 List<? extends Number> 用
        System.out.println("sum int: " + sum(List.of(1, 2, 3)));
        // List<Double> 也行
        System.out.println("sum double: " + sum(List.of(1.5, 2.5)));
    }

    static void erasureDemo() {
        System.out.println("\n=== 类型擦除 ===");

        List<String> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();

        // 运行时这两个 List 是同一个 class（泛型信息被擦了）
        System.out.println("a.getClass() = " + a.getClass());
        System.out.println("b.getClass() = " + b.getClass());
        System.out.println("same class? " + (a.getClass() == b.getClass()));
    }
}
