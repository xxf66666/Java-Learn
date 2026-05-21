package jvm_demo;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示三种典型的 JVM 内存错误（注释掉一个跑一个，不要全开）
 *
 * 加 JVM 参数运行（IDEA：Run → Edit Configurations → VM options）
 *   -Xmx64m -Xss256k  -XX:+HeapDumpOnOutOfMemoryError
 */
public class JvmStressTest {

    public static void main(String[] args) {
        // 取消注释其中一个跑：
        // heapOOM();
        // stackOverflow();
        showRuntime();
    }

    /** 堆溢出：不断创建对象不释放 */
    static void heapOOM() {
        List<byte[]> list = new ArrayList<>();
        while (true) {
            list.add(new byte[1024 * 1024]);     // 每次 1MB
            System.out.println("已分配 " + list.size() + " MB");
        }
    }

    /** 栈溢出：无限递归 */
    static void stackOverflow() {
        stackOverflow();
    }

    /** 看 JVM 当前的内存状况 */
    static void showRuntime() {
        Runtime r = Runtime.getRuntime();
        long mb = 1024 * 1024;
        System.out.println("可用处理器: " + r.availableProcessors());
        System.out.printf("堆 max=%dMB total=%dMB free=%dMB used=%dMB%n",
            r.maxMemory() / mb,
            r.totalMemory() / mb,
            r.freeMemory() / mb,
            (r.totalMemory() - r.freeMemory()) / mb);
    }
}
