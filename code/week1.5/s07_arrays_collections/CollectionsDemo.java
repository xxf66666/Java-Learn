package s07_arrays_collections;

import java.util.*;

public class CollectionsDemo {

    public static void main(String[] args) {
        arrayDemo();
        arraysUtil();
        varargsDemo();
        arrayListDemo();
        hashMapDemo();
        wordCount();
    }

    /** 数组基础 */
    static void arrayDemo() {
        System.out.println("\n=== 数组 ===");

        // 字面量
        int[] arr = {10, 20, 30};
        System.out.println("长度 = " + arr.length);    // 数组用 .length，**属性**
        System.out.println("arr[0] = " + arr[0]);

        // 二维数组（数组的数组）
        int[][] m = {
            {1, 2, 3},
            {4, 5, 6}
        };
        // 嵌套循环遍历
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    /** Arrays 工具类 */
    static void arraysUtil() {
        System.out.println("\n=== Arrays 工具 ===");

        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};

        // ⚠️ 用 println 打印数组只会输出 [I@xxxx 这种鬼东西
        // 必须用 Arrays.toString
        System.out.println("原始: " + Arrays.toString(arr));

        // 排序（原地）
        Arrays.sort(arr);
        System.out.println("排序: " + Arrays.toString(arr));

        // 二维数组用 deepToString
        int[][] mat = {{1, 2}, {3, 4}};
        System.out.println("二维: " + Arrays.deepToString(mat));
    }

    /** 可变参数 */
    static void varargsDemo() {
        System.out.println("\n=== 可变参数 ===");

        System.out.println(sum());                // 0
        System.out.println(sum(1));               // 1
        System.out.println(sum(1, 2, 3));         // 6
        System.out.println(sum(new int[]{10, 20, 30}));   // 60
    }

    // int... 让这个方法可以接受 0 到任意多个 int
    // 方法体里 nums 类型就是 int[]
    static int sum(int... nums) {
        int total = 0;
        for (int n : nums) total += n;
        return total;
    }

    /** ArrayList 增删改查 */
    static void arrayListDemo() {
        System.out.println("\n=== ArrayList ===");

        // 左用接口 List，右用实现 ArrayList
        // <String> 告诉编译器：这个 List 只装 String
        List<String> names = new ArrayList<>();

        names.add("Alice");                   // 末尾追加
        names.add("Bob");
        names.add(0, "Carol");                 // 在 0 位置插入

        System.out.println("初始: " + names);
        System.out.println("size = " + names.size());   // List 用 size()，**方法**
        System.out.println("get(0) = " + names.get(0));

        names.set(0, "Dave");                  // 替换
        names.remove("Bob");                    // 按值删

        System.out.println("操作后: " + names);

        // 遍历
        for (String n : names) {
            System.out.println("  - " + n);
        }

        // 不可变 List
        List<Integer> immutable = List.of(1, 2, 3);
        System.out.println("不可变: " + immutable);
        try {
            immutable.add(4);                   // 抛 UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("不可变 List 不能 add");
        }
    }

    /** HashMap 增删改查 */
    static void hashMapDemo() {
        System.out.println("\n=== HashMap ===");

        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 88);
        scores.put("Carol", 76);

        System.out.println("Alice = " + scores.get("Alice"));
        // 不存在返回 null
        System.out.println("Unknown = " + scores.get("Unknown"));
        // 不存在时给默认值
        System.out.println("Unknown getOrDefault = " + scores.getOrDefault("Unknown", 0));

        // 遍历：用 entrySet 一次拿 key 和 value，性能最好
        System.out.println("所有人:");
        for (Map.Entry<String, Integer> e : scores.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
    }

    /** 综合：词频统计 */
    static void wordCount() {
        System.out.println("\n=== 词频统计 ===");

        String text = "the quick brown fox jumps over the lazy dog the fox is quick";
        Map<String, Integer> count = new HashMap<>();

        // split("\\s+") = 用一个或多个空白字符切分
        for (String w : text.split("\\s+")) {
            // merge: 不存在则放 (w, 1)；存在则用 (旧值, 新值) → 旧值+1
            // Integer::sum 是 (a, b) -> a + b 的方法引用
            count.merge(w, 1, Integer::sum);
        }

        count.forEach((w, c) -> System.out.println("  " + w + " = " + c));
    }
}
