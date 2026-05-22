package s02_constructors;

public class DogDemo {

    public static void main(String[] args) {

        // ====== 三种构造器分别用法 ======

        // 全参：一次性把所有字段填好
        Dog a = new Dog("小白", "柴犬", 3);
        a.introduce();          // 我叫 小白，品种 柴犬，年龄 3

        // 只传名字：品种和年龄走默认
        Dog b = new Dog("大黄");
        b.introduce();          // 我叫 大黄，品种 未知，年龄 0

        // 完全无参：全部默认
        Dog c = new Dog();
        c.introduce();          // 我叫 无名，品种 未知，年龄 0

        // ====== 演示：构造器**不能手动调用** ======
        // a.Dog("xxx", "xxx", 5);     // ❌ 编译错误
        // 要重新创建对象只能 new
    }
}
