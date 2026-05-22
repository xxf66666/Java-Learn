package s04_inheritance;

/**
 * Cat extends Animal
 */
public class Cat extends Animal {

    public Cat(String name) {
        super(name);
    }

    @Override
    public void sound() {
        System.out.println(name + ": 喵~");
    }

    /** 猫特有的方法 */
    public void climb() {
        System.out.println(name + " 在爬树");
    }
}
