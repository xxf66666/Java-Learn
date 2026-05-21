package com.learning.erp.framework.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.learning.erp.common.util.SecurityUtils;
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

        Long uid = SecurityUtils.currentUserId();
        if (uid != null) {
            strictInsertFill(mo, "createdBy", Long.class, uid);
            strictInsertFill(mo, "updatedBy", Long.class, uid);
        }
    }

    @Override
    public void updateFill(MetaObject mo) {
        strictUpdateFill(mo, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        Long uid = SecurityUtils.currentUserId();
        if (uid != null) {
            strictUpdateFill(mo, "updatedBy", Long.class, uid);
        }
    }
}
