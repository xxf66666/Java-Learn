package s05_abstract_interface;

import java.util.List;

public class AbstractInterfaceDemo {

    public static void main(String[] args) {

        // ====== 抽象类不能 new ======
        // Shape s = new Shape("red");    // ❌ 编译错误：Shape is abstract

        // 只能 new 具体子类
        Circle c = new Circle("红色", 5);
        Rectangle r = new Rectangle("蓝色", 4, 6);

        c.describe();           // 来自父类 Shape 的方法（调多态的 area / perimeter）
        r.describe();

        System.out.println("---");

        // ====== 把它们当 Shape 用（多态 + 抽象类）======
        List<Shape> shapes = List.of(c, r);
        // 求总面积：根本不用判断是 Circle 还是 Rectangle，直接调 area
        double total = 0;
        for (Shape s : shapes) {
            total += s.area();
        }
        System.out.printf("总面积 = %.2f%n", total);

        System.out.println("---");

        // ====== 同一个对象，当 Drawable 用 ======
        // c 同时是 Shape 也是 Drawable
        Drawable d = c;        // 用接口类型装
        d.draw();
        d.drawTwice();          // 调接口里的 default 方法

        // ====== 调接口的静态方法：用接口名调 ======
        System.out.println("版本: " + Drawable.version());

        System.out.println("---");

        // ====== 用接口类型存一组对象 ======
        // 只要"能 draw"就放进来，不管它是 Circle / Rectangle / 还是其他
        Drawable[] drawables = { c, r };
        for (Drawable item : drawables) {
            item.draw();
        }
    }
}
