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
        // 危险操作默认注释掉，要演示哪个就取消注释
        // heapOOM();           // 堆溢出（大量分配对象不释放）
        // stackOverflow();     // 栈溢出（递归过深）

        // 默认只看一下 Runtime 信息
        showRuntime();
    }

    /** 堆溢出：不断创建 1MB 字节数组，引用都装进 List 不让回收 */
    static void heapOOM() {
        // 用 List 持有引用 → GC 不能回收 → 堆迟早爆
        List<byte[]> list = new ArrayList<>();
        while (true) {
            // new byte[N] 在堆上分配 N 字节的数组
            // 1024 * 1024 = 1 MB
            list.add(new byte[1024 * 1024]);
            System.out.println("已分配 " + list.size() + " MB");
        }
        // 最终抛 OutOfMemoryError: Java heap space
    }

    /** 栈溢出：无限递归，每次调用都会压栈帧 */
    static void stackOverflow() {
        // 自己调自己，没有终止条件 → StackOverflowError
        stackOverflow();
    }

    /** 看 JVM 当前的内存状况 */
    static void showRuntime() {
        // Runtime.getRuntime() 返回单例 Runtime 对象（JVM 提供的运行时入口）
        Runtime r = Runtime.getRuntime();

        // 把字节单位转成 MB 用：除以 1024 * 1024
        long mb = 1024 * 1024;

        // availableProcessors 当前可用的 CPU 核心数
        System.out.println("可用处理器: " + r.availableProcessors());

        // maxMemory  最大可用堆（-Xmx 参数）
        // totalMemory 当前堆总大小
        // freeMemory  当前堆空闲
        // 已用 = totalMemory - freeMemory
        System.out.printf("堆 max=%dMB total=%dMB free=%dMB used=%dMB%n",
            r.maxMemory() / mb,
            r.totalMemory() / mb,
            r.freeMemory() / mb,
            (r.totalMemory() - r.freeMemory()) / mb);
    }
}
