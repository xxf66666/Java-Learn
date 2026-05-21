package com.learning.erp.business.stock.service;

import com.learning.erp.business.stock.entity.Stock;
import com.learning.erp.business.stock.entity.StockLog;
import com.learning.erp.business.stock.mapper.StockLogMapper;
import com.learning.erp.business.stock.mapper.StockMapper;
import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class StockService {

    private final StockMapper stockMapper;
    private final StockLogMapper stockLogMapper;

    public StockService(StockMapper stockMapper, StockLogMapper stockLogMapper) {
        this.stockMapper = stockMapper;
        this.stockLogMapper = stockLogMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long materialId, Long warehouseId, BigDecimal qty,
                        String bizType, String bizNo, String remark) {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "入库数量必须大于 0");
        }

        Stock stock = stockMapper.selectByMatAndWh(materialId, warehouseId);
        if (stock == null) {
            stock = new Stock();
            stock.setMaterialId(materialId);
            stock.setWarehouseId(warehouseId);
            stock.setQuantity(qty);
            stockMapper.insert(stock);
        } else {
            stock.setQuantity(stock.getQuantity().add(qty));
            int rows = stockMapper.updateById(stock);
            if (rows == 0) throw new BusinessException(40002, "库存被并发修改，请重试");
        }

        writeLog(materialId, warehouseId, 1, qty, stock.getQuantity(), bizType, bizNo, remark);
    }

    @Transactional(rollbackFor = Exception.class)
    public void outbound(Long materialId, Long warehouseId, BigDecimal qty,
                         String bizType, String bizNo, String remark) {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "出库数量必须大于 0");
        }
        Stock stock = stockMapper.selectByMatAndWh(materialId, warehouseId);
        if (stock == null || stock.getQuantity().compareTo(qty) < 0) {
            throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                "物料 " + materialId + " 当前库存 " + (stock == null ? 0 : stock.getQuantity()) + "，需要 " + qty);
        }

        stock.setQuantity(stock.getQuantity().subtract(qty));
        int rows = stockMapper.updateById(stock);
        if (rows == 0) throw new BusinessException(40002, "库存被并发修改，请重试");

        writeLog(materialId, warehouseId, -1, qty, stock.getQuantity(), bizType, bizNo, remark);
    }

    private void writeLog(Long materialId, Long warehouseId, int direction,
                          BigDecimal qty, BigDecimal after,
                          String bizType, String bizNo, String remark) {
        StockLog log = new StockLog();
        log.setMaterialId(materialId);
        log.setWarehouseId(warehouseId);
        log.setDirection(direction);
        log.setQuantity(qty);
        log.setQuantityAfter(after);
        log.setBizType(bizType);
        log.setBizNo(bizNo);
        log.setRemark(remark);
        stockLogMapper.insert(log);
    }
}
