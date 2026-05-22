package s03_encapsulation;

/**
 * 演示实例字段 vs 静态字段
 *
 *  - instanceCount：每个 Counter 对象各自一份
 *  - totalCount：整个 Counter 类共享一份
 */
public class Counter {

    // 实例字段：每个对象都有自己的一份
    private int instanceCount = 0;

    // static 字段：全类共享，多少个对象都只有这一份
    private static int totalCount = 0;

    public Counter() {
        // 每次 new 一个 Counter：
        //   - 当前对象的 instanceCount = 1
        //   - 共享的 totalCount + 1
        instanceCount = 1;
        totalCount++;
    }

    public int getInstanceCount() { return instanceCount; }

    // 静态方法：用 Counter.getTotalCount() 调用，不需要 new
    public static int getTotalCount() { return totalCount; }
}
