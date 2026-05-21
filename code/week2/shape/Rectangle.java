package shape;

// 矩形继承 Shape
public class Rectangle extends Shape {

    // 长和宽，构造后不变
    private final double width;
    private final double height;

    public Rectangle(String color, double width, double height) {
        super(color);                     // 父类构造器先跑：设置 color
        this.width = width;
        this.height = height;
    }

    // 重写抽象方法
    @Override
    public double area() { return width * height; }   // 面积 = 长 × 宽

    @Override
    public double perimeter() { return 2 * (width + height); }  // 周长 = 2(长+宽)
}
