package s05_abstract_interface;

public class Rectangle extends Shape implements Drawable {

    private final double width;
    private final double height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() { return width * height; }

    @Override
    public double perimeter() { return 2 * (width + height); }

    @Override
    public void draw() {
        System.out.println("画一个" + color + "矩形，" + width + " × " + height);
    }
}
