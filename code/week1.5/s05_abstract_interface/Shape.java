package s05_abstract_interface;

/**
 * 抽象类：Shape 是一个概念，没有"一个具体的 Shape"，只有具体形状
 *
 * abstract 关键字：
 *  - 让这个类**不能被 new**
 *  - 允许包含 abstract 方法（没有方法体）
 */
public abstract class Shape {

    protected String color;

    // 抽象类**可以**有构造器
    // 它不会被直接调用，但子类的 super(...) 会调它
    public Shape(String color) {
        this.color = color;
    }

    // 抽象方法：没有方法体，分号结尾
    // 子类必须重写
    public abstract double area();
    public abstract double perimeter();

    // 普通方法：所有子类共享同一实现
    public void describe() {
        // %.2f 浮点保留 2 位小数；%n 换行
        System.out.printf("%s %s: 面积 %.2f, 周长 %.2f%n",
            color, getClass().getSimpleName(), area(), perimeter());
    }
}
