package s01_classes;

/**
 * 演示：怎么 new 出 Dog 对象、给字段赋值、调用方法
 *
 * 建议在 IDEA 里**断点单步走一遍**，重点看 debugger 变量面板：
 *  - d1 和 d2 在堆里是两个独立对象
 *  - b = a 之后 b 和 a 显示成"同一个对象 ID"
 */
public class DogDemo {

    public static void main(String[] args) {

        // ====== 造第一只狗 ======
        // new Dog() 在堆上分配一块内存，按 Dog 的模板放进字段
        // 然后把"指向这块内存的引用"赋给变量 d1
        Dog d1 = new Dog();

        // 给字段赋值（点 . 操作）
        d1.name = "小白";
        d1.breed = "柴犬";
        d1.age = 3;

        // 调用方法
        d1.bark();          // 输出：小白 says: 汪汪!
        d1.introduce();      // 输出：我叫 小白，是 柴犬，今年 3 岁

        System.out.println("---");

        // ====== 造第二只狗 ======
        Dog d2 = new Dog();
        d2.name = "大黄";
        d2.breed = "金毛";
        d2.age = 5;
        d2.bark();

        // 两个对象互不影响：d1 还是小白
        System.out.println("d1.name = " + d1.name);    // 小白
        System.out.println("d2.name = " + d2.name);    // 大黄

        System.out.println("---");

        // ====== 引用赋值的坑 ======
        // 下面**不是**造新狗，而是让 b 也指向 d1 那只狗
        Dog a = new Dog();
        a.name = "小灰";

        Dog b = a;              // 注意：这一行没有 new！
                                 // a 和 b 现在指向同一个对象

        b.name = "改名后";       // 通过 b 改名字
        // 通过 a 看，名字也变了——因为它们是同一个对象
        System.out.println("a.name = " + a.name);    // 改名后

        System.out.println("---");

        // ====== null 的演示 ======
        Dog nothing = null;       // 不指向任何对象
        try {
            nothing.bark();       // 调它的方法 → NullPointerException
        } catch (NullPointerException e) {
            System.out.println("空指针异常: " + e);
        }

        // 安全做法：调方法前判 null
        if (nothing != null) {
            nothing.bark();
        } else {
            System.out.println("nothing 是 null，跳过 bark");
        }
    }
}
