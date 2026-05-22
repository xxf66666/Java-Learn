package s01_classes;

/**
 * 一个最简单的类：Dog
 *
 * 这个类描述"狗"这个**类型**有什么数据、能做什么。
 * 但它本身不是任何具体的狗——具体的狗要靠 new Dog() 造出来。
 */
public class Dog {

    // ====== 字段（成员变量）======
    // 描述"对象有什么数据"
    // 这里没写 public/private 等修饰符，用的是"默认"权限（同包可访问，§03 会讲）
    String name;          // 名字
    String breed;          // 品种
    int age;               // 年龄

    // ====== 方法 ======
    // 描述"对象能做什么"
    // 注意：这些方法**没有 static**，叫"实例方法"
    // 实例方法必须先有具体对象才能调（dog.bark()）

    /** 叫 */
    void bark() {
        // 在实例方法里可以直接用字段名（编译器知道是"当前对象的字段"）
        System.out.println(name + " says: 汪汪!");
    }

    /** 自我介绍 */
    void introduce() {
        System.out.println("我叫 " + name + "，是 " + breed + "，今年 " + age + " 岁");
    }
}
