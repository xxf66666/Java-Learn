package shape;

/**
 * 接口示例：表达一种"能力"。
 *
 * interface 关键字定义接口（不同于 class）
 * 接口 vs 抽象类：
 *   - 接口没有字段（只有常量 public static final）
 *   - 没有构造器
 *   - 一个类可以同时 implements 多个接口
 *   - 适合表达"能做某件事"（Swimmer / Comparable）
 *
 * Java 8+ 接口可以有：
 *   - default 方法（有默认实现，子类可重写）
 *   - static 方法（接口自己的工具方法）
 */
public interface Drawable {

    // 抽象方法：默认就是 public abstract，写不写都一样
    // 实现类必须重写
    void draw();

    // default 方法：接口里有默认实现
    // 实现类可以直接用，也可以重写覆盖
    default void drawTwice() {
        draw();      // 调本接口的 draw 方法
        draw();
    }

    // static 方法：接口自己的工具方法，通过接口名调
    // 例如 Drawable.banner()
    static String banner() {
        return "===== Drawable =====";
    }
}
