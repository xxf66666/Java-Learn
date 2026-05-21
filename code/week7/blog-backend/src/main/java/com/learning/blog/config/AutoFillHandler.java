package com.learning.blog.config;

// 实现这个接口 + @Component 就能挂到 MyBatis-Plus 的填充流程上
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
// MetaObject 是 MyBatis 提供的"对象元数据"封装，用来反射读写实体字段
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充处理器：让实体的 createdAt / updatedAt 字段自动有值
 *
 * 工作流程：
 *  - 实体字段标 @TableField(fill = FieldFill.INSERT) 时
 *  - MyBatis-Plus 在 insert 之前会调 insertFill
 *  - 我们在这里给字段赋值
 */
@Component
public class AutoFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject mo) {
        // 当前时间，插入时填给 createdAt 和 updatedAt
        LocalDateTime now = LocalDateTime.now();

        // strictInsertFill: 严格模式填值
        //   参数 (MetaObject, 字段名, 字段类型, 值)
        //   只有字段当前是 null 才填，已有值不动
        strictInsertFill(mo, "createdAt", LocalDateTime.class, now);
        strictInsertFill(mo, "updatedAt", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject mo) {
        // 更新时只更新 updatedAt
        strictUpdateFill(mo, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
