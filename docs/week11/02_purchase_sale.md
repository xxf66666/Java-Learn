# Week 11 §02 · 采购订单 + 销售订单

> ERP 的"主流程"：进货 + 卖货。同一套模式两个方向。

---

## 1. 单据通用模式：主表 + 明细表

```
pur_order          ← 单据头：单号、客户、状态、总金额
   ↓ 1:N
pur_order_item     ← 单据明细：物料、数量、单价
```

```sql
CREATE TABLE pur_order (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no     VARCHAR(32) NOT NULL,
    supplier_id  BIGINT,
    supplier_name VARCHAR(128),
    warehouse_id BIGINT NOT NULL,
    total_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    status       VARCHAR(16)   NOT NULL DEFAULT 'DRAFT',   -- DRAFT / APPROVED / DONE / VOIDED
    remark       VARCHAR(255),
    created_at   DATETIME      DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT,
    approved_at  DATETIME,
    approved_by  BIGINT,
    deleted      TINYINT       DEFAULT 0,
    UNIQUE KEY uk_no (order_no, deleted),
    INDEX idx_status (status),
    INDEX idx_created (created_at)
);

CREATE TABLE pur_order_item (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id     BIGINT NOT NULL,
    material_id  BIGINT NOT NULL,
    quantity     DECIMAL(18,4) NOT NULL,
    price        DECIMAL(18,2) NOT NULL,
    amount       DECIMAL(18,2) NOT NULL,
    INDEX idx_order (order_id)
);
```

销售订单结构一模一样，只是字段名换成 `customer_id` 和表名 `sal_order` / `sal_order_item`。

---

## 2. 状态机

```
DRAFT (草稿)
   ↓ 提审
APPROVED (已审核) ─── 入库/出库 ──→ DONE (已完成)
   ↓
VOIDED (作废)
```

**状态流转规则**
- 只有 `DRAFT` 能改 / 删
- 只有 `DRAFT` / `APPROVED` 能作废（已 `DONE` 的要走"红冲"流程）
- 入库 / 出库**只能基于 `APPROVED` 单据**触发

代码里强校验：

```java
public void approve(Long id) {
    PurOrder o = mapper.selectById(id);
    if (!"DRAFT".equals(o.getStatus())) {
        throw new BusinessException(50002, "只有草稿可审核");
    }
    o.setStatus("APPROVED");
    o.setApprovedAt(LocalDateTime.now());
    o.setApprovedBy(SecurityUtils.currentUserId());
    mapper.updateById(o);
}
```

---

## 3. 关键操作：审核 + 入库

```java
@Service
public class PurchaseService {

    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long orderId) {
        PurOrder o = orderMapper.selectById(orderId);
        if (!"APPROVED".equals(o.getStatus())) {
            throw new BusinessException(50003, "只有已审核的单可入库");
        }

        List<PurOrderItem> items = itemMapper.selectList(
            new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, orderId));

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
}
```

整段在一个 `@Transactional` 里：
- 任意明细入库失败 → 整单回滚
- 库存表 / 流水表 / 订单状态 三者一致

销售 `outbound` 同理：调 `stockService.outbound()`，库存不足时事务回滚。

---

## 4. 接口设计

```http
### 采购
POST /api/business/purchase                     新建采购单（DRAFT）
GET  /api/business/purchase/{id}                查详情（含明细）
GET  /api/business/purchase/page                分页 + 搜索
PUT  /api/business/purchase/{id}/approve        审核
PUT  /api/business/purchase/{id}/void           作废
POST /api/business/purchase/{id}/inbound        入库（库存+）

### 销售
POST /api/business/sale                         新建销售单
PUT  /api/business/sale/{id}/approve
POST /api/business/sale/{id}/outbound           出库（库存-）
```

---

## 5. 请求格式示例

```json
POST /api/business/purchase
{
  "supplierId": 1,
  "supplierName": "苹果供应商",
  "warehouseId": 1,
  "remark": "12 月备货",
  "items": [
    {"materialId": 1, "quantity": 100, "price": 4500.00},
    {"materialId": 2, "quantity": 50,  "price": 2500.00}
  ]
}
```

后端 Service 拿到后：
1. 算 `total_amount = ∑ (price × quantity)`
2. 生成单号 `PO20251201-0001`
3. 同一事务里：插主表 → 拿 ID → 批量插明细

---

## 6. 完整业务流程脚本（最终验收）

```http
### 1. 登录
POST /api/login {"username":"admin","password":"admin123"}

### 2. 创建仓库 + 物料
POST /api/business/warehouse {"code":"W01","name":"主仓库"}
POST /api/business/material {"code":"M0001","name":"iPhone 15","unit":"件","price":5999}

### 3. 创建采购单
POST /api/business/purchase
{
  "warehouseId": 1, "supplierName": "苹果",
  "items": [{"materialId":1,"quantity":10,"price":4500}]
}

### 4. 审核 + 入库
PUT /api/business/purchase/1/approve
POST /api/business/purchase/1/inbound

### 5. 查库存：应该有 10 个
GET /api/business/stock?materialId=1

### 6. 销售出库
POST /api/business/sale
{
  "warehouseId": 1, "customerName": "门店 A",
  "items": [{"materialId":1,"quantity":3,"price":5999}]
}
PUT /api/business/sale/1/approve
POST /api/business/sale/1/outbound

### 7. 查库存：应该剩 7
GET /api/business/stock?materialId=1

### 8. 查流水：应该 2 条（+10 / -3）
GET /api/business/stock/1/logs
```

---

## 7. 自查

- [ ] 走完上面 8 步全流程，库存 / 流水都正确
- [ ] 把销售订单数量改成 100（超过库存），看回滚正确
- [ ] 已审核的单不能再改，已完成的单不能作废
- [ ] 操作日志（`sys_operation_log`）里有相应记录
