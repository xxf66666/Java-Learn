package com.learning.contact;

import java.time.LocalDateTime;

public class Contact {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private LocalDateTime createdAt;

    public Contact() {}
    public Contact(String name, String phone, String email) {
        this.name = name; this.phone = phone; this.email = email;
    }

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

    @Override
    public String toString() {
        return String.format("Contact[%d, %s, %s, %s, %s]", id, name, phone, email, createdAt);
    }
}
