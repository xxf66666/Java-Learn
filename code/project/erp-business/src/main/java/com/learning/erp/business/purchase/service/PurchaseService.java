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

@Service
public class PurchaseService {

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

    @Transactional(rollbackFor = Exception.class)
    public Long create(PurOrderCreateReq req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BusinessException(400, "明细不能为空");
        }
        if (req.getWarehouseId() == null) {
            throw new BusinessException(400, "仓库不能为空");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (PurOrderCreateReq.Item it : req.getItems()) {
            total = total.add(it.getPrice().multiply(it.getQuantity()));
        }

        PurOrder o = new PurOrder();
        o.setOrderNo(nextOrderNo());
        o.setSupplierId(req.getSupplierId());
        o.setSupplierName(req.getSupplierName());
        o.setWarehouseId(req.getWarehouseId());
        o.setTotalAmount(total);
        o.setStatus("DRAFT");
        o.setRemark(req.getRemark());
        orderMapper.insert(o);

        for (PurOrderCreateReq.Item it : req.getItems()) {
            PurOrderItem item = new PurOrderItem();
            item.setOrderId(o.getId());
            item.setMaterialId(it.getMaterialId());
            item.setQuantity(it.getQuantity());
            item.setPrice(it.getPrice());
            item.setAmount(it.getPrice().multiply(it.getQuantity()));
            itemMapper.insert(item);
        }
        return o.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id) {
        PurOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);
        if (!"DRAFT".equals(o.getStatus())) throw new BusinessException(50002, "只有草稿可审核");
        o.setStatus("APPROVED");
        o.setApprovedAt(LocalDateTime.now());
        o.setApprovedBy(SecurityUtils.currentUserId());
        orderMapper.updateById(o);
    }

    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long id) {
        PurOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);
        if (!"APPROVED".equals(o.getStatus())) throw new BusinessException(50003, "只有已审核的单可入库");

        List<PurOrderItem> items = itemMapper.selectList(
            new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, id));

        for (PurOrderItem item : items) {
            stockService.inbound(
                item.getMaterialId(),
                o.getWarehouseId(),
                item.getQuantity(),
                "PURCHASE",
                o.getOrderNo(),
                "采购入库");
        }

        o.setStatus("DONE");
        orderMapper.updateById(o);
    }

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

    /** PO20251201-0001 风格 */
    private String nextOrderNo() {
        String day = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        long count = orderMapper.selectCount(new LambdaQueryWrapper<PurOrder>()
            .likeRight(PurOrder::getOrderNo, "PO" + day));
        return "PO" + day + String.format("-%04d", count + 1);
    }
}
