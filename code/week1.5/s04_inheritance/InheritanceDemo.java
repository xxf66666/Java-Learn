package s04_inheritance;

public class InheritanceDemo {

    public static void main(String[] args) {

        // ====== 子类自动有父类的方法 ======
        Dog d = new Dog("小白", "柴犬");
        d.sleep();          // 来自 Animal 父类，Dog 自动继承
        d.sound();          // Dog 自己重写的版本：汪汪!
        d.fetch();          // Dog 自己加的方法

        System.out.println("---");

        Cat c = new Cat("汤姆");
        c.sleep();          // 同样继承父类的
        c.sound();          // Cat 自己重写：喵~
        c.climb();

        System.out.println("---");

        // ====== 多态：父类引用指向子类对象 ======
        // 数组类型是 Animal，但实际元素是 Dog 和 Cat
        Animal[] zoo = {
            new Dog("大黄", "金毛"),
            new Cat("加菲"),
            new Dog("旺财", "中华田园")
        };

        // 循环调 sound：自动调到各自子类的实现
        // 不需要 if/else 判断这是 Dog 还是 Cat
        for (Animal a : zoo) {
            a.sound();
        }

        System.out.println("---");

        // ====== instanceof + 模式匹配（Java 16+）======
        // 多态下，想用子类特有的方法（如 Dog.fetch / Cat.climb），
        // 必须先确认实际类型
        for (Animal a : zoo) {
            if (a instanceof Dog dog) {
                // 模式匹配：判断 a 是 Dog 的同时，自动声明 dog 变量
                dog.fetch();
            } else if (a instanceof Cat cat) {
                cat.climb();
            }
        }

        System.out.println("---");

        // ====== 向下转型的坑 ======
        Animal x = new Cat("猫");
        try {
            // 强行把 Cat 转成 Dog —— 运行时崩
            Dog dog = (Dog) x;
        } catch (ClassCastException e) {
            System.out.println("强转失败: " + e);
        }
    }
}
