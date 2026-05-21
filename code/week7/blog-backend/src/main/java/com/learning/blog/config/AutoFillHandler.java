package com.learning.blog.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AutoFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject mo) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(mo, "createdAt", LocalDateTime.class, now);
        strictInsertFill(mo, "updatedAt", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject mo) {
        strictUpdateFill(mo, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
