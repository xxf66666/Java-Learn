package com.learning.erp.framework.log;

/**
 * 业务模块（erp-system）实现，负责把事件存到数据库。
 * 用 SPI 风格解耦，framework 不依赖 system。
 */
public interface OperationLogSink {
    void persist(OperationLogEvent event);
}
