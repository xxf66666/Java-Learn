package s05_abstract_interface;

/**
 * 接口：表达"能被画出来"这个能力
 *
 * interface 关键字：
 *  - 不是 class
 *  - 默认所有方法都是 public abstract
 *  - 一个类可以同时 implements 多个接口
 */
public interface Drawable {

    // 抽象方法（默认是 public abstract，不写也是）
    void draw();

    // default 方法：接口里可以有默认实现（Java 8+）
    // 实现类可以选择重写，也可以直接用
    default void drawTwice() {
        draw();
        draw();
    }

    // static 方法：接口自己的工具
    static String version() {
        return "Drawable-v1";
    }
}
