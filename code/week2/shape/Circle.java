package shape;

// extends 关键字：Circle 继承 Shape（"圆是一种形状"）
// 一个类只能 extends 一个父类（单继承）
public class Circle extends Shape {

    // final 字段：构造后不能再改
    private final double radius;

    public Circle(String color, double radius) {
        // super(...) 调用父类构造器
        // 必须放构造器的第一行（在 this 赋值之前）
        super(color);
        this.radius = radius;
    }

    // @Override 强制检查：必须有同名同签名的方法在父类，否则编译报错
    @Override
    public double area() {
        // Math.PI 是 JDK 内置的圆周率常量
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }
}
