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

/**
 * 库存核心服务：入库 / 出库
 *
 * 重点：
 *  - 用 BigDecimal 保证数量精确（不能用 double，会有浮点误差）
 *  - 事务保证"改库存 + 写流水"原子
 *  - 乐观锁防止并发超卖（依赖 Stock 实体上的 @Version）
 */
@Service
public class StockService {

    // 两个 Mapper：库存表 + 流水表
    private final StockMapper stockMapper;
    private final StockLogMapper stockLogMapper;

    public StockService(StockMapper stockMapper, StockLogMapper stockLogMapper) {
        this.stockMapper = stockMapper;
        this.stockLogMapper = stockLogMapper;
    }

    /**
     * 入库：库存 +qty，并写一条流水
     *
     * @Transactional(rollbackFor = Exception.class)
     *   方法入事务，任意异常回滚
     */
    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long materialId, Long warehouseId, BigDecimal qty,
                        String bizType, String bizNo, String remark) {
        // 入库数量必须 > 0
        // BigDecimal 比较用 compareTo（==、equals 都不行）
        // > 0 表示 qty > BigDecimal.ZERO
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "入库数量必须大于 0");
        }

        // 找当前库存（按物料 + 仓库定位）
        Stock stock = stockMapper.selectByMatAndWh(materialId, warehouseId);

        if (stock == null) {
            // 第一次入这个物料 + 仓库，新建一行
            stock = new Stock();
            stock.setMaterialId(materialId);
            stock.setWarehouseId(warehouseId);
            stock.setQuantity(qty);
            stockMapper.insert(stock);
        } else {
            // 已有记录：累加数量
            // BigDecimal 是不可变类，add 返回新对象
            stock.setQuantity(stock.getQuantity().add(qty));

            // updateById 在 SQL 里会带 WHERE version = ?（@Version 注解的作用）
            // 返回值 = 0 说明 version 不匹配（有人在我之前先改了）
            int rows = stockMapper.updateById(stock);
            if (rows == 0) throw new BusinessException(40002, "库存被并发修改，请重试");
        }

        // 写一条流水
        writeLog(materialId, warehouseId, 1, qty, stock.getQuantity(), bizType, bizNo, remark);
    }

    /**
     * 出库：库存 -qty，并写一条流水
     */
    @Transactional(rollbackFor = Exception.class)
    public void outbound(Long materialId, Long warehouseId, BigDecimal qty,
                         String bizType, String bizNo, String remark) {

        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "出库数量必须大于 0");
        }

        Stock stock = stockMapper.selectByMatAndWh(materialId, warehouseId);

        // 库存不存在 或 余量不够 → 抛业务异常
        // BigDecimal.compareTo 返回 -1 / 0 / 1
        if (stock == null || stock.getQuantity().compareTo(qty) < 0) {
            throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT,
                "物料 " + materialId + " 当前库存 " + (stock == null ? 0 : stock.getQuantity()) + "，需要 " + qty);
        }

        // subtract = 减法
        stock.setQuantity(stock.getQuantity().subtract(qty));
        int rows = stockMapper.updateById(stock);
        if (rows == 0) throw new BusinessException(40002, "库存被并发修改，请重试");

        // 流水里 direction = -1 表示出库
        writeLog(materialId, warehouseId, -1, qty, stock.getQuantity(), bizType, bizNo, remark);
    }

    /**
     * 写流水的私有方法，避免入库/出库代码重复
     */
    private void writeLog(Long materialId, Long warehouseId, int direction,
                          BigDecimal qty, BigDecimal after,
                          String bizType, String bizNo, String remark) {
        StockLog log = new StockLog();
        log.setMaterialId(materialId);
        log.setWarehouseId(warehouseId);
        log.setDirection(direction);
        log.setQuantity(qty);
        log.setQuantityAfter(after);       // 变动后余量，方便追溯
        log.setBizType(bizType);            // 业务类型：PURCHASE / SALE / ADJUST
        log.setBizNo(bizNo);                // 关联单据号
        log.setRemark(remark);
        stockLogMapper.insert(log);
    }
}
