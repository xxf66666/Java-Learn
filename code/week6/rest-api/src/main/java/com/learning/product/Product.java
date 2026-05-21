package com.learning.product;

// Jakarta Bean Validation 校验注解（Java EE 9+ 改名 jakarta，老版本是 javax）
import jakarta.validation.constraints.*;

// BigDecimal 用于精确的金额计算，避免 double 浮点误差
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体（兼 DTO 用，简化版）
 *
 * @NotBlank / @NotNull / @Min ... 是声明式校验
 * Controller 加 @Valid 时这些约束才会真正生效
 */
public class Product {

    // 主键，新建时为 null（数据库自动分配）
    private Long id;

    // @NotBlank: 不能 null、不能空串、不能全空格
    // message 是校验失败时返回给前端的提示
    @NotBlank(message = "商品名不能为空")
    @Size(max = 64, message = "商品名最长 64 字符")
    private String name;

    // @NotNull: 字段不能为 null（但允许空字符串、0 等）
    // @DecimalMin: 最小值（字符串形式，因为 BigDecimal 没法在注解里写字面量）
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于 0")
    private BigDecimal price;

    // @Min: 整数最小值
    @Min(value = 0, message = "库存不能为负")
    private int stock;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 无参构造器 + 全参构造器：Jackson 反序列化 / 业务创建都用
    public Product() {}
    public Product(String name, BigDecimal price, int stock) {
        this.name = name; this.price = price; this.stock = stock;
    }

    // ====== 标准 getter / setter ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
