package com.learning.erp.business.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.erp.business.stock.entity.Stock;
import com.learning.erp.business.stock.entity.StockLog;
import com.learning.erp.business.stock.mapper.StockLogMapper;
import com.learning.erp.business.stock.mapper.StockMapper;
import com.learning.erp.common.result.PageResult;
import com.learning.erp.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/stock")
public class StockController {

    private final StockMapper stockMapper;
    private final StockLogMapper logMapper;

    public StockController(StockMapper stockMapper, StockLogMapper logMapper) {
        this.stockMapper = stockMapper;
        this.logMapper = logMapper;
    }

    @GetMapping
    public Result<List<Stock>> list(@RequestParam(required = false) Long materialId,
                                     @RequestParam(required = false) Long warehouseId) {
        return Result.ok(stockMapper.selectList(new LambdaQueryWrapper<Stock>()
            .eq(materialId != null, Stock::getMaterialId, materialId)
            .eq(warehouseId != null, Stock::getWarehouseId, warehouseId)));
    }

    @GetMapping("/{materialId}/logs")
    public Result<PageResult<StockLog>> logs(@PathVariable Long materialId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Page<StockLog> p = logMapper.selectPage(new Page<>(page, size),
            new LambdaQueryWrapper<StockLog>()
                .eq(StockLog::getMaterialId, materialId)
                .orderByDesc(StockLog::getId));
        return Result.ok(new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize()));
    }
}
