package com.learning.erp.system.log.service;

import com.learning.erp.framework.log.OperationLogEvent;
import com.learning.erp.framework.log.OperationLogSink;
import com.learning.erp.system.log.entity.SysOperationLog;
import com.learning.erp.system.log.mapper.SysOperationLogMapper;
// @Async: 让方法异步执行（要先 @EnableAsync）
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志的落地实现
 *
 * 实现 framework 定义的 SPI 接口 OperationLogSink
 * 把切面收集的事件转成实体类 SysOperationLog 入库
 *
 * 异步入库：避免日志慢拖累业务接口
 */
@Component
public class OperationLogSinkImpl implements OperationLogSink {

    private final SysOperationLogMapper mapper;

    public OperationLogSinkImpl(SysOperationLogMapper mapper) { this.mapper = mapper; }

    /**
     * @Async: 这个方法被调用时，Spring 会用单独线程跑
     *   - 调用方立即返回，不阻塞
     *   - 注意：@Async 也依赖 AOP 代理，自调用同样失效
     *   - 主启动类要加 @EnableAsync 才生效
     */
    @Override
    @Async
    public void persist(OperationLogEvent ev) {
        // 把"框架事件对象"转成"系统层实体"
        // 这个转换看起来重复，但保持了模块边界（framework 不知道数据库长啥样）
        SysOperationLog log = new SysOperationLog();
        log.setUserId(ev.getUserId());
        log.setUsername(ev.getUsername());
        log.setModule(ev.getModule());
        log.setOperation(ev.getOperation());
        log.setMethod(ev.getMethod());
        log.setParams(ev.getParams());
        log.setIp(ev.getIp());
        log.setUserAgent(ev.getUserAgent());
        log.setDurationMs(ev.getDurationMs());
        log.setSuccess(ev.getSuccess());
        log.setErrorMsg(ev.getErrorMsg());
        log.setCreatedAt(ev.getCreatedAt());

        // 入库
        mapper.insert(log);
    }
}
