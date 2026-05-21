// package 声明，告诉编译器和 JVM 这个文件归属哪个包
package oop;

// HashSet 是基于哈希表的集合，自动去重
import java.util.HashSet;
// Set 是集合的顶层接口
import java.util.Set;

/**
 * 跑这个 main 验证 Student 的所有功能。
 */
public class StudentDemo {

    public static void main(String[] args) {
        // new 创建 Student 对象，调用 3 参构造器
        Student s1 = new Student("S001", "Alice", 20);

        // 调用 2 参构造器（内部走 this(id, name, 0)）
        Student s2 = new Student("S002", "Bob");

        // 学号和 s1 相同——演示 equals 按业务键比较
        Student s3 = new Student("S001", "Alice 改名", 21);

        // println(对象) 会自动调对象的 toString()
        System.out.println(s1);
        System.out.println(s2);

        // 静态方法调用：用类名.方法名
        System.out.println("已创建学生数 = " + Student.getTotalCount());

        // s1 和 s3 学号相同 → 我们重写了 equals → 应该返回 true
        System.out.println("s1.equals(s3) = " + s1.equals(s3));

        // HashSet 用 hashCode + equals 判重
        // 因为我们让"学号相同 = 相等"，所以 s3 加不进去
        Set<Student> set = new HashSet<>();
        set.add(s1);
        set.add(s3);     // 重复，被 HashSet 忽略
        // 集合大小用 size() 方法
        System.out.println("set.size() = " + set.size());

        // 测试 setter 校验
        // try-catch 包住可能抛异常的代码
        try {
            // 故意传 -1，触发 setter 里的 throw new IllegalArgumentException
            s1.setAge(-1);
        } catch (IllegalArgumentException e) {
            // 捕获后打印；System.err 是错误输出（红色）
            System.err.println("捕获预期异常: " + e.getMessage());
        }
    }
}
