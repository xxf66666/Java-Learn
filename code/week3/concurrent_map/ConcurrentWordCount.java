package concurrent_map;

import java.util.concurrent.*;

/**
 * 多线程词频统计：演示 ConcurrentHashMap 的原子操作。
 */
public class ConcurrentWordCount {

    public static void main(String[] args) throws Exception {
        String[] texts = {
            "java is great java is fun",
            "python is dynamic java is static",
            "go is simple java is verbose",
            "java rules them all"
        };

        ConcurrentHashMap<String, Integer> count = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(texts.length);

        for (String text : texts) {
            pool.submit(() -> {
                for (String w : text.split("\\s+")) {
                    // ⚠️ 必须用原子方法，否则 "读->加->写" 在多线程下会丢失
                    count.merge(w, 1, Integer::sum);
                }
                latch.countDown();
            });
        }

        latch.await();
        pool.shutdown();

        count.entrySet().stream()
             .sorted((a, b) -> b.getValue() - a.getValue())
             .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));
    }
}
