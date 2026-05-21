package com.learning.erp.system.log.service;

import com.learning.erp.framework.log.OperationLogEvent;
import com.learning.erp.framework.log.OperationLogSink;
import com.learning.erp.system.log.entity.SysOperationLog;
import com.learning.erp.system.log.mapper.SysOperationLogMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OperationLogSinkImpl implements OperationLogSink {

    private final SysOperationLogMapper mapper;

    public OperationLogSinkImpl(SysOperationLogMapper mapper) { this.mapper = mapper; }

    @Override
    @Async
    public void persist(OperationLogEvent ev) {
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
        mapper.insert(log);
    }
}
