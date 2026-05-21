package com.learning.erp.business.sale.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.erp.business.sale.dto.SalOrderCreateReq;
import com.learning.erp.business.sale.entity.SalOrder;
import com.learning.erp.business.sale.mapper.SalOrderMapper;
import com.learning.erp.business.sale.service.SaleService;
import com.learning.erp.common.annotation.SysLog;
import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import com.learning.erp.common.result.PageResult;
import com.learning.erp.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/business/sale")
public class SaleController {

    private final SaleService service;
    private final SalOrderMapper orderMapper;

    public SaleController(SaleService service, SalOrderMapper orderMapper) {
        this.service = service;
        this.orderMapper = orderMapper;
    }

    @SysLog(module = "销售", operation = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody SalOrderCreateReq req) {
        return Result.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        SalOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.SALE_ORDER_NOT_FOUND);
        Map<String, Object> m = new HashMap<>();
        m.put("order", o);
        m.put("items", service.listItems(id));
        return Result.ok(m);
    }

    @GetMapping("/page")
    public Result<PageResult<SalOrder>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String status) {
        Page<SalOrder> p = orderMapper.selectPage(new Page<>(page, size),
            new LambdaQueryWrapper<SalOrder>()
                .eq(status != null && !status.isBlank(), SalOrder::getStatus, status)
                .orderByDesc(SalOrder::getId));
        return Result.ok(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @SysLog(module = "销售", operation = "审核")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        service.approve(id);
        return Result.ok();
    }

    @SysLog(module = "销售", operation = "出库")
    @PostMapping("/{id}/outbound")
    public Result<Void> outbound(@PathVariable Long id) {
        service.outbound(id);
        return Result.ok();
    }
}
