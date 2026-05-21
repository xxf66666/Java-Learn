package student;

import java.util.Objects;

/**
 * 学生管理系统 - 学生实体（与 oop/Student 是两个独立的演示）
 */
public class Student {

    // 学号一旦确定不能改 -> final
    private final String id;
    private String name;
    private int age;
    private String major;             // 专业

    // 全参构造器
    public Student(String id, String name, int age, String major) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.major = major;
    }

    // ====== 基础 getter / setter（不带校验，简化版）======
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMajor() { return major; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setMajor(String major) { this.major = major; }

    /**
     * CSV 行 → Student：解析一行文本，返回对象
     * static 方法：以 Student.fromCsv(...) 调用，不需要先 new
     */
    public static Student fromCsv(String line) {
        // split(",") 用逗号切分，返回 String[]
        String[] p = line.split(",");
        // trim() 去掉首尾空格，避免 " Alice " 这种带空格的脏数据
        // Integer.parseInt 把字符串解析成 int
        return new Student(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()), p[3].trim());
    }

    /** Student → CSV 行 */
    public String toCsv() {
        // String.join(分隔符, 多个字符串) 把多个字符串用分隔符拼接
        // String.valueOf(int) 把 int 转 String
        return String.join(",", id, name, String.valueOf(age), major);
    }

    @Override
    public String toString() {
        // String.format 类似 printf，但返回字符串而不是直接输出
        return String.format("Student[%s, %s, %d岁, %s]", id, name, age, major);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;
        // 业务规则：学号相同 = 同一个学生
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
