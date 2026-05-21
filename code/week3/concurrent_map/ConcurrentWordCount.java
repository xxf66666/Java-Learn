package concurrent_map;

import java.util.concurrent.*;

/**
 * 多线程词频统计：演示 ConcurrentHashMap 的原子操作。
 *
 * 普通 HashMap 不是线程安全的，多线程并发 put 可能丢数据；
 * ConcurrentHashMap 内部用分段锁/CAS，**单个操作**是线程安全的。
 */
public class ConcurrentWordCount {

    public static void main(String[] args) throws Exception {
        // String[] 数组字面量：4 段文本
        String[] texts = {
            "java is great java is fun",
            "python is dynamic java is static",
            "go is simple java is verbose",
            "java rules them all"
        };

        // ConcurrentHashMap<K, V>：线程安全的 HashMap
        ConcurrentHashMap<String, Integer> count = new ConcurrentHashMap<>();

        // 4 线程池
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // 等所有任务都完成
        CountDownLatch latch = new CountDownLatch(texts.length);

        // 增强 for：遍历数组每个 text
        for (String text : texts) {
            // 提交一个任务：把这段 text 的单词统计进 count
            pool.submit(() -> {
                // 把 text 按空白拆分成单词数组
                for (String w : text.split("\\s+")) {
                    // ⚠️ count.get(w) + 1 然后 put 的"读-改-写"在多线程下会丢更新
                    // merge 是 ConcurrentHashMap 提供的"原子"读改写：
                    //   不存在 → 放 (w, 1)
                    //   存在   → 用 BiFunction 合并旧值和新值
                    // Integer::sum 是方法引用，等价于 (a, b) -> a + b
                    count.merge(w, 1, Integer::sum);
                }
                // 这个任务完成
                latch.countDown();
            });
        }

        // 等所有任务结束
        latch.await();
        pool.shutdown();

        // 按词频降序打印结果
        // entrySet().stream() 把所有键值对作为流
        // sorted(Comparator) 排序：b - a 是降序（值大的排前面）
        // forEach 终止操作
        count.entrySet().stream()
             .sorted((a, b) -> b.getValue() - a.getValue())
             .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));
    }
}
