package shape;

import java.util.List;

public class ShapeDemo {

    public static void main(String[] args) {
        // 多态：父类引用指向子类对象
        List<Shape> shapes = List.of(
            new Circle("red", 5.0),
            new Rectangle("blue", 4.0, 6.0),
            new Circle("green", 3.0)
        );

        for (Shape s : shapes) {
            s.describe();    // 调用各自子类的 area / perimeter
        }

        double totalArea = shapes.stream().mapToDouble(Shape::area).sum();
        System.out.printf("总面积 = %.2f%n", totalArea);

        // 类型判断 + 转型（Java 16+ 模式匹配）
        for (Shape s : shapes) {
            if (s instanceof Circle c) {
                System.out.println("发现一个圆，半径 = ?");   // c 直接可用
            } else if (s instanceof Rectangle r) {
                System.out.println("发现一个矩形");
            }
        }

        // 接口示例
        System.out.println(Drawable.banner());
        Drawable d = () -> System.out.println("hello, draw!");
        d.drawTwice();
    }
}
