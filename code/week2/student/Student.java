package student;

import java.util.Objects;

public class Student {
    private final String id;
    private String name;
    private int age;
    private String major;

    public Student(String id, String name, int age, String major) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.major = major;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getMajor() { return major; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setMajor(String major) { this.major = major; }

    /** CSV 行 → Student */
    public static Student fromCsv(String line) {
        String[] p = line.split(",");
        return new Student(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()), p[3].trim());
    }

    /** Student → CSV 行 */
    public String toCsv() {
        return String.join(",", id, name, String.valueOf(age), major);
    }

    @Override
    public String toString() {
        return String.format("Student[%s, %s, %d岁, %s]", id, name, age, major);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
