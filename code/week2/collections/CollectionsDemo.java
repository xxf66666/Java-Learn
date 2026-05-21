package collections;

import java.util.*;

/**
 * 集合框架综合演示：List / Set / Map 各种实现 + 遍历 + 修改技巧
 */
public class CollectionsDemo {

    public static void main(String[] args) {
        listDemo();
        setDemo();
        mapDemo();
        modifyWhileIterating();
        countWords();
    }

    static void listDemo() {
        System.out.println("\n=== List ===");
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add(0, "Carol");                    // 插到开头
        System.out.println(names);

        names.set(0, "Carol*");
        names.remove("Bob");
        System.out.println("after: " + names);
        System.out.println("first: " + names.get(0));
        System.out.println("size: " + names.size());

        // JDK 9+ 不可变 List
        List<Integer> nums = List.of(1, 2, 3);
        System.out.println("immutable: " + nums);
        // nums.add(4);   // ❌ UnsupportedOperationException
    }

    static void setDemo() {
        System.out.println("\n=== Set ===");

        Set<String> hash = new HashSet<>();
        hash.add("a"); hash.add("b"); hash.add("a");    // 重复忽略
        System.out.println("HashSet: " + hash);

        Set<String> linked = new LinkedHashSet<>();
        linked.add("c"); linked.add("a"); linked.add("b");
        System.out.println("LinkedHashSet (插入序): " + linked);

        Set<Integer> sorted = new TreeSet<>();
        sorted.add(3); sorted.add(1); sorted.add(2);
        System.out.println("TreeSet (自然序): " + sorted);
    }

    static void mapDemo() {
        System.out.println("\n=== Map ===");
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 88);
        scores.put("Carol", 76);

        System.out.println("Alice = " + scores.get("Alice"));
        System.out.println("Unknown = " + scores.getOrDefault("Unknown", 0));

        // 遍历方式 1: entrySet
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
        // 遍历方式 2: forEach lambda
        scores.forEach((k, v) -> System.out.println("  " + k + " => " + v));
    }

    static void modifyWhileIterating() {
        System.out.println("\n=== 遍历时修改 ===");
        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));

        // ❌ for-each 中 remove 会抛 ConcurrentModificationException
        // for (String s : list) if (s.equals("b")) list.remove(s);

        // ✅ Iterator
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().equals("b")) it.remove();
        }
        System.out.println("after iterator remove: " + list);

        // ✅ removeIf
        list.removeIf(s -> s.equals("c"));
        System.out.println("after removeIf: " + list);
    }

    /** 经典练习：统计每个单词出现次数 */
    static void countWords() {
        System.out.println("\n=== 词频统计 ===");
        String text = "the quick brown fox jumps over the lazy dog the fox is quick";
        Map<String, Integer> count = new HashMap<>();
        for (String w : text.split("\\s+")) {
            count.merge(w, 1, Integer::sum);     // 如果 w 不存在则放 1，否则 +1
        }
        count.forEach((w, c) -> System.out.println("  " + w + " = " + c));
    }
}
