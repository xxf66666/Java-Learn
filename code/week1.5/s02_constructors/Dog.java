package s02_constructors;

/**
 * 这版 Dog 加了构造器
 */
public class Dog {

    String name;
    String breed;
    int age;

    // ====== 构造器 1：全参（最完整）======
    // 名字 = 类名（Dog），没有返回类型（不是 void，是没有）
    public Dog(String name, String breed, int age) {
        // 参数名和字段名一样，必须用 this 区分
        // 左边的 this.name 指"当前对象的字段"
        // 右边的 name 指"传进来的参数"
        this.name = name;
        this.breed = breed;
        this.age = age;
    }

    // ====== 构造器 2：只传名字 ======
    // 重载：参数列表不同
    public Dog(String name) {
        // this(...) 调用同类的另一个构造器
        // **必须放在构造器的第一行**（这是硬性规定）
        this(name, "未知", 0);
    }

    // ====== 构造器 3：完全无参 ======
    public Dog() {
        this("无名");           // 级联到上面那个，再级联到全参
    }

    // ====== 实例方法 ======
    void introduce() {
        // 实例方法里可以用 this 指代当前对象
        // 这里写 this.name 或 name 都行（没有同名变量遮挡时编译器自动加 this）
        System.out.println("我叫 " + this.name + "，品种 " + breed + "，年龄 " + age);
    }
}
