package shape;

import java.util.List;

/**
 * 演示多态 + 接口 + Lambda
 */
public class ShapeDemo {

    public static void main(String[] args) {
        // 多态：用父类 Shape 类型的 List 装具体子类对象
        // List.of(...) 创建不可变 List（JDK 9+）
        List<Shape> shapes = List.of(
            new Circle("red", 5.0),
            new Rectangle("blue", 4.0, 6.0),
            new Circle("green", 3.0)
        );

        // for-each 遍历：每次拿一个 Shape
        // 调 s.describe() 时，JVM 根据 s 的"实际类型"找方法（动态绑定）
        for (Shape s : shapes) {
            s.describe();
        }

        // stream API：把集合转成"流"，链式处理
        // mapToDouble(Shape::area) = 用方法引用，对每个 Shape 调 area()，得到 double 流
        // sum() = 终止操作，求所有元素总和
        double totalArea = shapes.stream().mapToDouble(Shape::area).sum();
        System.out.printf("总面积 = %.2f%n", totalArea);

        // 类型判断 + 转型（Java 16+ 模式匹配）
        for (Shape s : shapes) {
            // s instanceof Circle c 等价于：
            //   if (s instanceof Circle) { Circle c = (Circle) s; ... }
            if (s instanceof Circle c) {
                System.out.println("发现一个圆，半径 = ?");
            } else if (s instanceof Rectangle r) {
                System.out.println("发现一个矩形");
            }
        }

        // ---- 演示接口 ----
        // 调静态方法：接口名.方法名
        System.out.println(Drawable.banner());

        // 用 Lambda 表达式快速实现单方法接口（函数式接口）
        // Drawable 只有一个抽象方法 draw()，所以可以这样写
        // () -> { ... } 等价于 new Drawable() { @Override public void draw() {...} }
        Drawable d = () -> System.out.println("hello, draw!");

        // 调用 default 方法：内部会调 draw() 两次
        d.drawTwice();
    }
}
