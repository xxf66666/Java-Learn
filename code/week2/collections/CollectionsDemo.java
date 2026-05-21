package collections;

// 通配符 * 一次导入 java.util 下所有公开类
import java.util.*;

/**
 * 集合框架综合演示：List / Set / Map 各种实现 + 遍历 + 修改技巧
 */
public class CollectionsDemo {

    public static void main(String[] args) {
        // 按主题分多个 section
        listDemo();
        setDemo();
        mapDemo();
        modifyWhileIterating();
        countWords();
    }

    // ============ List ============
    static void listDemo() {
        System.out.println("\n=== List ===");

        // ArrayList 实现 List 接口
        // 左侧用接口类型 List 是"面向接口编程"，将来想换 LinkedList 改一行就好
        List<String> names = new ArrayList<>();

        names.add("Alice");           // 末尾追加
        names.add("Bob");

        // add(index, element) 在指定位置插入，后面的元素往后挪
        names.add(0, "Carol");
        System.out.println(names);    // 直接 print 集合会调它的 toString，输出 [Carol, Alice, Bob]

        // set(index, element) 替换指定位置元素
        names.set(0, "Carol*");

        // remove(Object) 按值删除第一个匹配
        // 注：还有 remove(int) 按索引删除，调用时小心重载混淆
        names.remove("Bob");

        System.out.println("after: " + names);
        System.out.println("first: " + names.get(0));     // get(index) 取元素
        System.out.println("size: " + names.size());      // 集合大小用 size()

        // JDK 9+ 创建不可变 List 的快捷方法
        // 返回的 List 不能 add / remove，否则抛 UnsupportedOperationException
        List<Integer> nums = List.of(1, 2, 3);
        System.out.println("immutable: " + nums);
        // nums.add(4);   // 会抛异常
    }

    // ============ Set ============
    static void setDemo() {
        System.out.println("\n=== Set ===");

        // HashSet：无序、不允许重复
        Set<String> hash = new HashSet<>();
        hash.add("a");
        hash.add("b");
        hash.add("a");                  // 重复被忽略
        System.out.println("HashSet: " + hash);

        // LinkedHashSet：保留插入顺序
        Set<String> linked = new LinkedHashSet<>();
        linked.add("c"); linked.add("a"); linked.add("b");
        System.out.println("LinkedHashSet (插入序): " + linked);

        // TreeSet：自动排序（按元素的自然顺序或传入的 Comparator）
        Set<Integer> sorted = new TreeSet<>();
        sorted.add(3); sorted.add(1); sorted.add(2);
        System.out.println("TreeSet (自然序): " + sorted);   // [1, 2, 3]
    }

    // ============ Map ============
    static void mapDemo() {
        System.out.println("\n=== Map ===");

        // HashMap<K, V>：哈希字典
        // K 必须是引用类型；基本类型要用对应包装类（Integer / Long...）
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);        // put(key, value) 写入
        scores.put("Bob", 88);
        scores.put("Carol", 76);

        // get(key) 取值；不存在返回 null
        System.out.println("Alice = " + scores.get("Alice"));

        // getOrDefault(key, 默认值) 不存在时给默认值，省一次判 null
        System.out.println("Unknown = " + scores.getOrDefault("Unknown", 0));

        // 遍历方式 1: entrySet 拿所有 key-value 对
        // 性能最好（一次循环拿到 key 和 value）
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }

        // 遍历方式 2: forEach + Lambda（Java 8+）
        // (k, v) -> ... 是 BiConsumer 函数式接口的 Lambda
        scores.forEach((k, v) -> System.out.println("  " + k + " => " + v));
    }

    // ============ 遍历时修改 ============
    static void modifyWhileIterating() {
        System.out.println("\n=== 遍历时修改 ===");

        // new ArrayList<>(List.of(...)) = 用不可变 List 复制出一个可变 ArrayList
        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));

        // ❌ for-each 中 remove 会抛 ConcurrentModificationException
        //    因为 for-each 内部用迭代器，迭代时集合结构被改它会检测出来
        // for (String s : list) if (s.equals("b")) list.remove(s);

        // ✅ 方式 1：手动获取 Iterator，用 it.remove() 安全删除
        // Iterator 是集合的"游标"，记录当前位置
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {           // 还有下一个元素吗
            if (it.next().equals("b")) { // next() 返回当前并把游标往后挪
                it.remove();              // 删除"当前位置"的元素
            }
        }
        System.out.println("after iterator remove: " + list);

        // ✅ 方式 2：removeIf（Java 8+）—— 最简洁
        // 接受 Predicate Lambda，对每个元素判断，返回 true 就删
        list.removeIf(s -> s.equals("c"));
        System.out.println("after removeIf: " + list);
    }

    /** 经典练习：统计每个单词出现次数 */
    static void countWords() {
        System.out.println("\n=== 词频统计 ===");

        String text = "the quick brown fox jumps over the lazy dog the fox is quick";

        // 用 HashMap 装：key 是单词，value 是出现次数
        Map<String, Integer> count = new HashMap<>();

        // split("\\s+") 用一个或多个空白字符分割（正则）
        // 返回 String[] 数组，用 for-each 遍历
        for (String w : text.split("\\s+")) {
            // merge 是 Map 的原子操作：
            //   如果 w 不存在 → 放入 (w, 1)
            //   如果 w 存在 → 用提供的函数合并：Integer::sum 等价于 (a, b) -> a + b
            count.merge(w, 1, Integer::sum);
        }

        // 遍历输出
        count.forEach((w, c) -> System.out.println("  " + w + " = " + c));
    }
}
