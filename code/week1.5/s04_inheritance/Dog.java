package s04_inheritance;

/**
 * Dog extends Animal：狗是一种动物
 *
 * 自动继承 Animal 的字段（name）和方法（sleep / sound）
 * 自己加 breed 字段 + 重写 sound 方法
 */
public class Dog extends Animal {

    String breed;       // 狗特有的字段

    public Dog(String name, String breed) {
        // 子类构造器**必须**先把父类初始化好
        // super(name) 调用父类 Animal(String name) 构造器，必须放第一行
        super(name);
        this.breed = breed;
    }

    /**
     * 重写父类方法：用自己的实现替换
     * @Override 让编译器帮忙检查：必须真的有同名父方法被覆盖
     */
    @Override
    public void sound() {
        System.out.println(name + " (" + breed + "): 汪汪!");
    }

    /** 狗特有的方法（父类没有）*/
    public void fetch() {
        System.out.println(name + " 在捡飞盘");
    }
}
