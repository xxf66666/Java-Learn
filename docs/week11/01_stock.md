# Week 11 §01 · 库存 + 库存流水

> ERP 最容易出 bug 的地方，必须从设计上就严密。

---

## 1. 两表设计

### 1.1 库存当前值

```sql
CREATE TABLE wms_stock (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id     BIGINT NOT NULL,
    warehouse_id    BIGINT NOT NULL,
    quantity        DECIMAL(18,4) NOT NULL DEFAULT 0,    -- 当前库存
    version         INT NOT NULL DEFAULT 0,               -- 乐观锁
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_mat_wh (material_id, warehouse_id),
    INDEX idx_material (material_id)
);
```

**关键**
- 主键不是业务键，但 `(material_id, warehouse_id)` 必须**唯一**
- `quantity` 用 `DECIMAL`（不要用 `float` / `double`，ERP 钱和量都要精确）
- 加 `version` 字段做乐观锁，防止并发扣减时多扣

### 1.2 库存流水（每次出入库都留痕）

```sql
CREATE TABLE wms_stock_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id     BIGINT NOT NULL,
    warehouse_id    BIGINT NOT NULL,
    direction       TINYINT NOT NULL,                  -- 1 入库 / -1 出库
    quantity        DECIMAL(18,4) NOT NULL,             -- 本次变动数量（>0）
    quantity_after  DECIMAL(18,4) NOT NULL,             -- 变动后余额
    biz_type        VARCHAR(32),                        -- PURCHASE / SALE / ADJUST
    biz_no          VARCHAR(64),                        -- 关联单据号 PO20251201-0001
    remark          VARCHAR(255),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,
    INDEX idx_mat (material_id),
    INDEX idx_biz (biz_type, biz_no),
    INDEX idx_created (created_at)
);
```

**为什么必须有流水**
- 业务追溯：客户问"我上周入的 50 个 iPhone 怎么不见了"，靠流水回答
- 财务核账：库存 - 上期 = 本期所有流水之和
- 修 bug：库存对不上时，按流水重算

---

## 2. 出入库的标准服务

```java
@Service
public class StockService {

    @Transactional(rollbackFor = Exception.class)
    public void inbound(Long materialId, Long warehouseId, BigDecimal qty,
                        String bizType, String bizNo, String remark) {
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "入库数量必须大于 0");
        }

        // 1. 找当前库存（带行锁；乐观锁也可）
        Stock stock = stockMapper.selectByMatAndWh(materialId, warehouseId);
        if (stock == null) {
            // 第一次入库：插入
            stock = new Stock();
            stock.setMaterialId(materialId);
            stock.setWarehouseId(warehouseId);
            stock.setQuantity(qty);
            stockMapper.insert(stock);
        } else {
            // 已有：累加
            stock.setQuantity(stock.getQuantity().add(qty));
            stockMapper.updateById(stock);     // version 自动 +1
        }

        // 2. 写流水
        StockLog log = new StockLog();
        log.setMaterialId(materialId);
        log.setWarehouseId(warehouseId);
        log.setDirection(1);
        log.setQuantity(qty);
        log.setQuantityAfter(stock.getQuantity());
        log.setBizType(bizType);
        log.setBizNo(bizNo);
        log.setRemark(remark);
        stockLogMapper.insert(log);
    }

    @Transactional(rollbackFor = Exception.class)
    public void outbound(Long materialId, Long warehouseId, BigDecimal qty,
                         String bizType, String bizNo, String remark) {
        Stock stock = stockMapper.selectByMatAndWh(materialId, warehouseId);
        if (stock == null || stock.getQuantity().compareTo(qty) < 0) {
            throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT);
        }
        stock.setQuantity(stock.getQuantity().subtract(qty));
        int rows = stockMapper.updateById(stock);
        if (rows == 0) {
            // 乐观锁失败：说明被并发改了
            throw new BusinessException(40002, "库存被并发修改，请重试");
        }

        StockLog log = new StockLog();
        log.setMaterialId(materialId);
        log.setWarehouseId(warehouseId);
        log.setDirection(-1);
        log.setQuantity(qty);
        log.setQuantityAfter(stock.getQuantity());
        log.setBizType(bizType);
        log.setBizNo(bizNo);
        log.setRemark(remark);
        stockLogMapper.insert(log);
    }
}
```

---

## 3. 并发扣库存的方案对比

| 方案 | 实现 | 优点 | 缺点 |
|------|------|------|------|
| **数据库行锁** | `SELECT ... FOR UPDATE` | 强一致 | 高并发性能差 |
| **乐观锁** (`@Version`) | 更新时 `WHERE version = ?` | 性能好 | 失败要重试 |
| **Redis 分布式锁** | `SETNX` + Lua 释放 | 性能极好 | 复杂、要处理 Redis 故障 |
| **库存预扣 + MQ 异步** | 下单先扣 Redis，异步同步 DB | 极致性能 | 极复杂、最终一致 |

学习阶段用**乐观锁**最合适——MyBatis-Plus 一个 `@Version` 注解就搞定。

---

## 4. 接口

```http
GET  /api/business/stock?materialId=1                    查指定物料库存
GET  /api/business/stock/page                            分页查
GET  /api/business/stock/{materialId}/logs?page=1&size=10   流水
POST /api/business/stock/adjust                          手动盘点调整（要权限）
```

---

## 5. 自查

- [ ] 给 5 个物料各入库 10 个，看 wms_stock 5 行 / wms_stock_log 5 行
- [ ] 出库 3 个，看 stock 减 3、流水多 1 行
- [ ] 出库超过库存 → 报错"库存不足"
- [ ] 用两个线程同时出库（postman 双开），看 version 乐观锁是否生效
