package shape;

public abstract class Shape {
    protected final String color;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double area();
    public abstract double perimeter();

    public void describe() {
        System.out.printf("%s %s, 面积 %.2f, 周长 %.2f%n",
                          color, getClass().getSimpleName(), area(), perimeter());
    }
}
