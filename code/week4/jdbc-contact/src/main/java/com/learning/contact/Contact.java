package com.learning.contact;

// LocalDateTime: Java 8+ 的不可变日期时间类，推荐替代老的 Date
import java.time.LocalDateTime;

/**
 * 联系人实体：对应数据库 contact 表
 * 这种"字段 + getter + setter"模式，俗称 POJO（Plain Old Java Object）
 */
public class Contact {

    // 主键，对应数据库自增 id
    // Long（首字母大写）是 long 的包装类型，允许 null（新建时还没分配 id 就是 null）
    private Long id;
    private String name;
    private String phone;
    private String email;
    private LocalDateTime createdAt;

    // 无参构造器：很多框架（如 Jackson、JPA）需要它来反射创建对象
    public Contact() {}

    // 三参构造器：方便日常 new
    public Contact(String name, String phone, String email) {
        this.name = name; this.phone = phone; this.email = email;
    }

    // ====== 标准 getter / setter（实际工作中可以用 Lombok @Data 自动生成）======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // 重写 toString 方便 print 时直接看清字段值
    @Override
    public String toString() {
        return String.format("Contact[%d, %s, %s, %s, %s]", id, name, phone, email, createdAt);
    }
}
