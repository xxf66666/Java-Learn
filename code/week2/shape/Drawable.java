package shape;

/**
 * 接口示例：表达一种"能力"。
 * Java 8+ 接口可以有 default 方法（提供默认实现）和 static 方法。
 */
public interface Drawable {
    void draw();

    default void drawTwice() {
        draw();
        draw();
    }

    static String banner() {
        return "===== Drawable =====";
    }
}
