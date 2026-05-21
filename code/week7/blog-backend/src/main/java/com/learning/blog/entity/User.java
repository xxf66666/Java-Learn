package com.learning.blog.entity;

// MyBatis-Plus 的实体注解
import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 用户实体 - 对应数据库 user 表
 *
 * @TableName("user"): 指定数据库表名（默认会用类名小写，但显式写更清楚）
 */
@TableName("user")
public class User {

    // @TableId: 标记主键字段
    // type = IdType.AUTO: 数据库自增（依赖表本身的 AUTO_INCREMENT）
    // 其它策略：ASSIGN_ID 雪花算法 / ASSIGN_UUID 等
    @TableId(type = IdType.AUTO)
    private Long id;

    // 字段没标注解，MyBatis-Plus 默认按"驼峰转下划线"映射
    //   userName (Java) <-> user_name (DB)
    // 这里 name / email 是单词，直接同名映射
    private String name;
    private String email;

    // articleCount → article_count
    private Integer articleCount;

    // @TableField(fill = ...): 自动填充字段
    // INSERT: 插入时自动填值（在 AutoFillHandler 里设逻辑）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // INSERT_UPDATE: 插入和更新时都自动填
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // @TableLogic: 逻辑删除字段
    // deleteById 实际是 UPDATE 把 deleted 改成 1
    // 所有查询自动加 WHERE deleted = 0
    @TableLogic
    private Integer deleted;

    // @Version: 乐观锁字段
    // updateById 时 SQL 自动加 WHERE version = ?，并把 version + 1
    // 多人同时修改时只有第一个成功，其他更新 0 行
    @Version
    private Integer version;

    // ====== getter / setter ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getArticleCount() { return articleCount; }
    public void setArticleCount(Integer articleCount) { this.articleCount = articleCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
