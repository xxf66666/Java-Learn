package com.learning.erp.business.sale.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.erp.business.sale.dto.SalOrderCreateReq;
import com.learning.erp.business.sale.entity.SalOrder;
import com.learning.erp.business.sale.entity.SalOrderItem;
import com.learning.erp.business.sale.mapper.SalOrderItemMapper;
import com.learning.erp.business.sale.mapper.SalOrderMapper;
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
public class SaleService {

    private final SalOrderMapper orderMapper;
    private final SalOrderItemMapper itemMapper;
    private final StockService stockService;

    public SaleService(SalOrderMapper orderMapper, SalOrderItemMapper itemMapper, StockService stockService) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.stockService = stockService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(SalOrderCreateReq req) {
        if (req.getItems() == null || req.getItems().isEmpty()) throw new BusinessException(400, "明细不能为空");
        if (req.getWarehouseId() == null) throw new BusinessException(400, "仓库不能为空");

        BigDecimal total = BigDecimal.ZERO;
        for (var it : req.getItems()) total = total.add(it.getPrice().multiply(it.getQuantity()));

        SalOrder o = new SalOrder();
        o.setOrderNo(nextOrderNo());
        o.setCustomerId(req.getCustomerId());
        o.setCustomerName(req.getCustomerName());
        o.setWarehouseId(req.getWarehouseId());
        o.setTotalAmount(total);
        o.setStatus("DRAFT");
        o.setRemark(req.getRemark());
        orderMapper.insert(o);

        for (var it : req.getItems()) {
            SalOrderItem item = new SalOrderItem();
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
        SalOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.SALE_ORDER_NOT_FOUND);
        if (!"DRAFT".equals(o.getStatus())) throw new BusinessException(60002, "只有草稿可审核");
        o.setStatus("APPROVED");
        o.setApprovedAt(LocalDateTime.now());
        o.setApprovedBy(SecurityUtils.currentUserId());
        orderMapper.updateById(o);
    }

    @Transactional(rollbackFor = Exception.class)
    public void outbound(Long id) {
        SalOrder o = orderMapper.selectById(id);
        if (o == null) throw new BusinessException(ErrorCode.SALE_ORDER_NOT_FOUND);
        if (!"APPROVED".equals(o.getStatus())) throw new BusinessException(60003, "只有已审核的单可出库");

        List<SalOrderItem> items = itemMapper.selectList(
            new LambdaQueryWrapper<SalOrderItem>().eq(SalOrderItem::getOrderId, id));

        for (SalOrderItem item : items) {
            stockService.outbound(
                item.getMaterialId(),
                o.getWarehouseId(),
                item.getQuantity(),
                "SALE",
                o.getOrderNo(),
                "销售出库");
        }

        o.setStatus("DONE");
        orderMapper.updateById(o);
    }

    public List<SalOrderItem> listItems(Long orderId) {
        return itemMapper.selectList(new LambdaQueryWrapper<SalOrderItem>().eq(SalOrderItem::getOrderId, orderId));
    }

    private String nextOrderNo() {
        String day = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        long count = orderMapper.selectCount(new LambdaQueryWrapper<SalOrder>()
            .likeRight(SalOrder::getOrderNo, "SO" + day));
        return "SO" + day + String.format("-%04d", count + 1);
    }
}
