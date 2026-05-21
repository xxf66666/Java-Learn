package shape;

/**
 * 抽象类示例：Shape 是"形状"的抽象概念，没有具体形状不能 new
 *
 * abstract 关键字让这个类:
 *   1. 不能 new Shape(...)，只能被继承
 *   2. 可以有 abstract 方法（没有方法体，留给子类实现）
 *   3. 也可以有普通方法 / 字段 / 构造器（这是它和 interface 的差别）
 */
public abstract class Shape {

    // protected = 本类 + 同包 + 子类可访问
    // 颜色字段一旦构造时给定就不再改（final）
    protected final String color;

    // 抽象类**可以**有构造器，但只能被子类用 super(...) 调用
    public Shape(String color) {
        this.color = color;
    }

    // abstract 方法：没有方法体，分号结尾
    // 任何子类必须 @Override 实现这两个方法，否则也得是 abstract
    public abstract double area();           // 面积
    public abstract double perimeter();      // 周长

    // 普通方法：有实现，子类可继承可重写
    public void describe() {
        // getClass().getSimpleName() 拿到运行时实际类名（如 "Circle"）
        // 体现多态：传 Circle 实例时这里输出 Circle 而不是 Shape
        // %.2f 浮点保留 2 位小数；%n 跨平台换行
        System.out.printf("%s %s, 面积 %.2f, 周长 %.2f%n",
                          color, getClass().getSimpleName(), area(), perimeter());
    }
}
