package com.learning.erp.business.warehouse.controller;

import com.learning.erp.business.warehouse.entity.Warehouse;
import com.learning.erp.business.warehouse.mapper.WarehouseMapper;
import com.learning.erp.common.annotation.SysLog;
import com.learning.erp.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/warehouse")
public class WarehouseController {

    private final WarehouseMapper mapper;
    public WarehouseController(WarehouseMapper mapper) { this.mapper = mapper; }

    @GetMapping
    public Result<List<Warehouse>> list() {
        return Result.ok(mapper.selectList(null));
    }

    @SysLog(module = "仓库", operation = "新增")
    @PostMapping
    public Result<Long> create(@RequestBody Warehouse w) {
        if (w.getStatus() == null) w.setStatus(1);
        mapper.insert(w);
        return Result.ok(w.getId());
    }
}
