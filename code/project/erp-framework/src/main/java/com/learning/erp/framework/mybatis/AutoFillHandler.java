package com.learning.erp.framework.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
// 我们自己的当前用户工具
import com.learning.erp.common.util.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ERP 的自动填充：比 Week 7 多了 createdBy / updatedBy
 *
 * 比 Week 7 演进的点：
 *  - 时间字段 ✅
 *  - "谁创建 / 谁修改" 字段（从 SecurityUtils 拿当前 userId）
 */
@Component
public class AutoFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject mo) {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();

        // 时间字段：插入时同时填两个
        strictInsertFill(mo, "createdAt", LocalDateTime.class, now);
        strictInsertFill(mo, "updatedAt", LocalDateTime.class, now);

        // 用户字段：只有登录态下才填（系统初始化 / 定时任务可能没登录）
        Long uid = SecurityUtils.currentUserId();
        if (uid != null) {
            strictInsertFill(mo, "createdBy", Long.class, uid);
            strictInsertFill(mo, "updatedBy", Long.class, uid);
        }
    }

    @Override
    public void updateFill(MetaObject mo) {
        // 更新时只动 updatedAt + updatedBy
        strictUpdateFill(mo, "updatedAt", LocalDateTime.class, LocalDateTime.now());

        Long uid = SecurityUtils.currentUserId();
        if (uid != null) {
            strictUpdateFill(mo, "updatedBy", Long.class, uid);
        }
    }
}
