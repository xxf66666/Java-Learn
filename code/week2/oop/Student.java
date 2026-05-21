package oop;

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

    private final String id;            // 学号一旦确定不应改，用 final
    private String name;
    private int age;
    private static int totalCount = 0;

    public Student(String id, String name, int age) {
        this.id = id;
        this.name = name;
        setAge(age);                    // 走 setter 校验
        totalCount++;
    }

    public Student(String id, String name) {
        this(id, name, 0);              // 调上面那个构造器
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        this.name = name;
    }
    public int getAge() { return age; }
    public void setAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("age 必须在 0~150 之间，收到 " + age);
        }
        this.age = age;
    }

    public static int getTotalCount() { return totalCount; }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "', age=" + age + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;     // Java 16+ 模式匹配
        return Objects.equals(id, s.id);                 // 学号相同就算同一个学生
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
