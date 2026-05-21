package com.learning.erp.business.purchase.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.erp.business.purchase.dto.PurOrderCreateReq;
import com.learning.erp.business.purchase.entity.PurOrder;
import com.learning.erp.business.purchase.entity.PurOrderItem;
import com.learning.erp.business.purchase.mapper.PurOrderItemMapper;
import com.learning.erp.business.purchase.mapper.PurOrderMapper;
import com.learning.erp.business.stock.service.StockService;
import com.learning.erp.common.exception.BusinessException;
import com.learning.erp.common.exception.ErrorCode;
import com.learning.erp.common.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 采购服务：创建单 / 审核 / 入库
 *
 * 状态机：DRAFT → APPROVED → DONE / VOIDED
 */
@Service
public class PurchaseService {

    // 三个依赖：单据头表、明细表、库存服务（用于入库）
    private final PurOrderMapper orderMapper;
    private final PurOrderItemMapper itemMapper;
    private final StockService stockService;

    public PurchaseService(PurOrderMapper orderMapper,
                           PurOrderItemMapper itemMapper,
                           StockService stockService) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.stockService = stockService;
    }

    /**
     * 创建采购单（状态 DRAFT）
     * 主表 + 明细表是一对多关系，必须在同一事务里
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(PurOrderCreateReq req) {
        // 业务校验
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BusinessException(400, "明细不能为空");
        }
        if (req.getWarehouseId() == null) {
            throw new BusinessException(400, "仓库不能为空");
        }

        // 计算总金额：明细单价 × 数量 累加
        // BigDecimal.ZERO 是常量 0
        BigDecimal total = BigDecimal.ZERO;
        for (PurOrderCreateReq.Item it : req.getItems()) {
            // multiply = 乘法，add = 加法（BigDecimal 不可变）
            total = total.add(it.getPrice().multiply(it.getQuantity()));
        }

        // 构造主单并插入（顺便拿自增 id）
        PurOrder o = new PurOrder();
        o.setOrderNo(nextOrderNo());            // 生成单号
        o.setSupplierId(req.getSupplierId());
        o.setSupplierName(req.getSupplierName());
        o.setWarehouseId(req.getWarehouseId());
        o.setTotalAmount(total);
        o.setStatus("DRAFT");                    // 初始状态：草稿
        o.setRemark(req.getRemark());
        orderMapper.insert(o);

        // 插明细：每条明细单独 insert
        // 真实生产建议用 IService.saveBatch 一次性批量插入
        for (PurOrderCreateReq.Item it : req.getItems()) {
            PurOrderItem item = new PurOrderItem();
            item.setOrderId(o.getId());           // 关联主单 id
            item.setMaterialId(it.getMaterialId());
            item.setQuantity(it.getQuantity());
            item.setPrice(it.getPrice());
            item.setAmount(it.getPrice().multiply(it.getQuantity()));
            itemMapper.insert(item);
        }
        return o.getId();
    }

    /**
     * 审核：DRAFT → APPROVED
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        PurOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);

        // 状态机校验：只有草稿状态可以审核
        if (!"DRAFT".equals(o.getStatus())) throw new BusinessException(50002, "只有草稿可审核");

        o.setStatus("APPROVED");
        o.setApprovedAt(LocalDateTime.now());
        o.setApprovedBy(SecurityUtils.currentUserId());
        orderMapper.updateById(o);
    }

    /**
     * 入库：APPROVED → DONE，并把所有明细数量加到库存
     *
     * 这是最关键的事务：
     *  - 库存表会被多次更新（一条明细一次）
     *  - 流水表会插多条
     *  - 单据状态最后改 DONE
     * 任何一步失败 → 整个事务回滚 → 库存 / 流水 / 状态都恢复
     */
    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long id) {
        PurOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);
        if (!"APPROVED".equals(o.getStatus())) throw new BusinessException(50003, "只有已审核的单可入库");

        // 查所有明细
        List<PurOrderItem> items = itemMapper.selectList(
            new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, id));

        // 增强 for 遍历每个明细，调 stockService.inbound 入库
        for (PurOrderItem item : items) {
            stockService.inbound(
                item.getMaterialId(),
                o.getWarehouseId(),
                item.getQuantity(),
                "PURCHASE",                    // bizType
                o.getOrderNo(),                 // 单号
                "采购入库");
        }

        // 全部入库成功，单据状态置为已完成
        o.setStatus("DONE");
        orderMapper.updateById(o);
    }

    /**
     * 查单据详情：通过数组"出参"返回明细（Java 没有真的 out 参数，用数组模拟）
     * 这种写法不太优雅，更好的方式是定义专门的 VO 类
     */
    public PurOrder getDetail(Long id, List<PurOrderItem>[] itemsHolder) {
        PurOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);
        List<PurOrderItem> items = itemMapper.selectList(
            new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, id));
        if (itemsHolder != null && itemsHolder.length > 0) itemsHolder[0] = items;
        return o;
    }

    public List<PurOrderItem> listItems(Long orderId) {
        return itemMapper.selectList(new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, orderId));
    }

    /**
     * 生成单号：PO20251201-0001 风格
     *
     * 简化实现：按今日已有的 PO 单数量 +1
     * 高并发场景需要用 Redis INCR 或数据库 sequence
     */
    private String nextOrderNo() {
        // BASIC_ISO_DATE = yyyyMMdd（无分隔符）
        String day = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        // 数当天已有的单数
        long count = orderMapper.selectCount(new LambdaQueryWrapper<PurOrder>()
            .likeRight(PurOrder::getOrderNo, "PO" + day));        // likeRight = LIKE 'POxxxx%'

        // String.format("%04d", n) = 4 位数字，不足前面补 0
        return "PO" + day + String.format("-%04d", count + 1);
    }
}
