package oop;

import java.util.HashSet;
import java.util.Set;

public class StudentDemo {

    public static void main(String[] args) {
        Student s1 = new Student("S001", "Alice", 20);
        Student s2 = new Student("S002", "Bob");
        Student s3 = new Student("S001", "Alice 改名", 21);   // 学号同 s1

        System.out.println(s1);
        System.out.println(s2);
        System.out.println("已创建学生数 = " + Student.getTotalCount());

        // equals: 学号相同 -> true
        System.out.println("s1.equals(s3) = " + s1.equals(s3));

        // HashSet 借助 equals + hashCode 去重
        Set<Student> set = new HashSet<>();
        set.add(s1);
        set.add(s3);                  // 重复（学号相同），不会加入
        System.out.println("set.size() = " + set.size());

        // 测试 setter 校验
        try {
            s1.setAge(-1);
        } catch (IllegalArgumentException e) {
            System.err.println("捕获预期异常: " + e.getMessage());
        }
    }
}
