// package 声明：文件第一行有效代码（除注释外）
// 包名要和目录路径一致：这个文件在 oop/ 目录下，所以包名是 oop
package oop;

// java.util.Objects 提供 equals / hashCode 等帮助方法
import java.util.Objects;

/**
 * 学生类：本周最基本的实体模板。
 *
 * 知识点覆盖：
 *  - private 字段 + public getter/setter
 *  - 构造器重载（this(...) 互调）
 *  - static 字段（计数器）+ static 方法
 *  - 重写 toString / equals / hashCode
 *  - final 字段（一旦赋值不能改）
 */
public class Student {

    // private final 字段：
    //   private 只能在本类访问，外界只能通过 getter
    //   final 一旦初始化就不能再赋值；学号一旦确定就锁死
    private final String id;

    // 普通可变字段
    private String name;
    private int age;

    // static = 静态字段：属于"类"而不是"对象"，全类共享一份
    // 这里用来记录已创建的 Student 总数
    private static int totalCount = 0;

    // 构造器：方法名 = 类名，没有返回类型
    // 这是"主构造器"，3 参版
    public Student(String id, String name, int age) {
        // this.xxx 表示"当前对象的 xxx 字段"
        // 因为参数名和字段名相同，所以必须写 this 区分
        this.id = id;
        this.name = name;

        // 注意这里走 setter 而不是 this.age = age，
        // 因为 setter 内部有合法性校验，从构造器开始就保证状态合法
        setAge(age);

        // ++ 是自增运算符；这里给"类共享"的 totalCount 加 1
        totalCount++;
    }

    // 构造器重载：参数列表不同
    // 这个 2 参版本内部调主构造器，age 给个默认值 0
    public Student(String id, String name) {
        // this(...) 调用当前类的另一个构造器
        // 必须在构造器的**第一行**，否则编译报错
        this(id, name, 0);
    }

    // ====== getter / setter ======

    // getter 通常无参 + 返回字段值
    public String getId() { return id; }
    public String getName() { return name; }

    // setter 通常有 1 个参数 + 返回 void
    // 走 setter 而不是 public 字段的好处：能在赋值时做校验
    public void setName(String name) {
        // isBlank() (Java 11+) 判断字符串是 null 之外的"空白"（空串、全空格）
        if (name == null || name.isBlank()) {
            // throw 抛出异常，调用方需要 try-catch 或者由 JVM 终止程序
            // IllegalArgumentException 表示"参数非法"，是 RuntimeException 子类（unchecked）
            throw new IllegalArgumentException("name 不能为空");
        }
        this.name = name;
    }

    public int getAge() { return age; }
    public void setAge(int age) {
        // 校验业务规则
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("age 必须在 0~150 之间，收到 " + age);
        }
        this.age = age;
    }

    // 静态方法：用类名直接调，不依赖具体对象
    public static int getTotalCount() { return totalCount; }

    // ====== Object 三件套：toString / equals / hashCode ======

    // @Override 是注解，告诉编译器"我覆盖了父类（Object）的方法"
    // 如果方法名写错了（如 ToString），编译器会报错
    // 强烈建议每次覆盖都加这个注解
    @Override
    public String toString() {
        // toString 在 println 一个对象时被自动调用，类似 Python __str__
        // 不写则默认输出 "类名@十六进制 hashcode"，没什么意义
        return "Student{id='" + id + "', name='" + name + "', age=" + age + "}";
    }

    @Override
    public boolean equals(Object o) {
        // 第一步快速通道：和自己比，直接 true
        if (this == o) return true;

        // 第二步：类型判断 + 自动转型（Java 16+ 的 "模式匹配"）
        // o instanceof Student s 等价于：
        //   if (o instanceof Student) { Student s = (Student) o; ... }
        // 注意 o 是 null 或者不是 Student 类型都返回 false
        if (!(o instanceof Student s)) return false;

        // Objects.equals 处理 null 安全：两个 null 算相等，否则调 .equals
        // 业务规则：学号相同就算同一个学生（即使姓名年龄不同）
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() {
        // 重写 equals 必须重写 hashCode：HashMap / HashSet 依赖 hashCode 定位桶
        // 规则：equals 相等的两个对象，hashCode 必须相等
        // Objects.hash(...) 会综合多个字段算一个 int hash
        return Objects.hash(id);
    }
}
