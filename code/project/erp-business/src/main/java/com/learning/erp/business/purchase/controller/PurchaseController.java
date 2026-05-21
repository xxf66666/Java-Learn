package com.learning.erp.business.purchase.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.erp.business.purchase.dto.PurOrderCreateReq;
import com.learning.erp.business.purchase.entity.PurOrder;
import com.learning.erp.business.purchase.entity.PurOrderItem;
import com.learning.erp.business.purchase.mapper.PurOrderMapper;
import com.learning.erp.business.purchase.service.PurchaseService;
import com.learning.erp.common.annotation.SysLog;
import com.learning.erp.common.result.PageResult;
import com.learning.erp.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business/purchase")
public class PurchaseController {

    private final PurchaseService service;
    private final PurOrderMapper orderMapper;

    public PurchaseController(PurchaseService service, PurOrderMapper orderMapper) {
        this.service = service;
        this.orderMapper = orderMapper;
    }

    @SysLog(module = "采购", operation = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody PurOrderCreateReq req) {
        return Result.ok(service.create(req));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        @SuppressWarnings("unchecked")
        List<PurOrderItem>[] holder = new List[1];
        PurOrder o = service.getDetail(id, holder);
        Map<String, Object> m = new HashMap<>();
        m.put("order", o);
        m.put("items", holder[0]);
        return Result.ok(m);
    }

    @GetMapping("/page")
    public Result<PageResult<PurOrder>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String status) {
        Page<PurOrder> p = orderMapper.selectPage(new Page<>(page, size),
            new LambdaQueryWrapper<PurOrder>()
                .eq(status != null && !status.isBlank(), PurOrder::getStatus, status)
                .orderByDesc(PurOrder::getId));
        return Result.ok(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }

    @SysLog(module = "采购", operation = "审核")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        service.approve(id);
        return Result.ok();
    }

    @SysLog(module = "采购", operation = "入库")
    @PostMapping("/{id}/inbound")
    public Result<Void> inbound(@PathVariable Long id) {
        service.inbound(id);
        return Result.ok();
    }
}
