# Week 11 · ERP 业务模块

> 目标：把 ERP 真正"跑业务"——物料 / 仓库 / 库存 / 采购入库 / 销售出库的最小闭环。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_material_warehouse.md`](00_material_warehouse.md) | 物料档案 + 仓库 / 库位 |
| 01 | [`01_stock.md`](01_stock.md) | 库存 + 库存流水（出入库都留痕） |
| 02 | [`02_purchase_sale.md`](02_purchase_sale.md) | 采购订单 / 销售订单 + 状态机 + 编码规则 |

## 配套代码

→ [`../../code/project/erp-business/`](../../code/project/erp-business/)
→ 新增 SQL：[`../../scripts/erp-business.sql`](../../scripts/erp-business.sql)

## 本周里程碑

完成下面这个完整流程：

```
1. 录入物料：M0001 "iPhone 15"  M0002 "iPad Air"
2. 录入仓库：W01 "主仓库"
3. 下采购单 → 审核 → 入库   → 库存 +10 + 流水
4. 下销售单 → 审核 → 出库   → 库存 -3 + 流水
5. 看库存：剩 7  看流水：2 条
```

> 不强求做财务对账、生产 BOM 等深水区，做完上面 5 步就达标。
