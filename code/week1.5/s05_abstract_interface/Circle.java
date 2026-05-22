package s05_abstract_interface;

/**
 * 圆：继承 Shape + 实现 Drawable
 *
 * extends 在前，implements 在后
 * implements 可以列多个接口，逗号分隔
 */
public class Circle extends Shape implements Drawable {

    private final double radius;

    public Circle(String color, double radius) {
        super(color);            // 调父类构造器把 color 传上去
        this.radius = radius;
    }

    // ====== 实现父类 Shape 的抽象方法 ======
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }

    // ====== 实现接口 Drawable 的抽象方法 ======
    @Override
    public void draw() {
        System.out.println("画一个" + color + "圆，半径 " + radius);
    }
}
