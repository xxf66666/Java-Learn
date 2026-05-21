# Week 12 §01 · Dashboard 接口

> 首页统计：今日入库 / 出库 / 库存总值 / 各物料库存饼图。前端用 ECharts 渲染。

---

## 1. 接口设计

```java
@GetMapping("/api/dashboard")
public Result<DashboardVO> get() { ... }

public class DashboardVO {
    private BigDecimal todayInboundAmount;
    private BigDecimal todayOutboundAmount;
    private long todayInboundCount;
    private long todayOutboundCount;
    private BigDecimal totalStockValue;        // ∑ 库存量 × 物料单价
    private List<MaterialStockVO> topMaterials; // 库存前 10
    private List<TrendPoint> last7DaysInbound;
    private List<TrendPoint> last7DaysOutbound;
}
```

---

## 2. SQL：聚合查询

```sql
-- 今日入库金额
SELECT COALESCE(SUM(t.quantity * t.price), 0) AS amount
FROM wms_stock_log t
LEFT JOIN pur_order o ON o.order_no = t.biz_no
LEFT JOIN pur_order_item i ON i.order_id = o.id AND i.material_id = t.material_id
WHERE t.direction = 1 AND DATE(t.created_at) = CURDATE();

-- 各物料库存（前 10）
SELECT s.material_id, m.name, m.unit, s.quantity, m.price, s.quantity * m.price AS amount
FROM wms_stock s
JOIN mat_material m ON m.id = s.material_id
ORDER BY amount DESC
LIMIT 10;

-- 近 7 天入库趋势
SELECT DATE(created_at) AS d, COUNT(*) AS cnt, SUM(quantity) AS qty
FROM wms_stock_log
WHERE direction = 1 AND created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(created_at)
ORDER BY d;
```

---

## 3. 实现思路

Dashboard 查询通常**很重**：跨多表 join + group by。
- 加 Redis 缓存：`@Cacheable(value = "dashboard", key = "'today'")`，过期 1 分钟
- 别用 MyBatis-Plus 的 Wrapper 拼复杂 SQL，直接写 `<select>` XML 或 `@Select` 注解

```java
@Mapper
public interface DashboardMapper {

    @Select("SELECT COALESCE(SUM(quantity_after), 0) FROM wms_stock_log " +
            "WHERE direction = 1 AND DATE(created_at) = CURDATE()")
    BigDecimal todayInboundTotal();

    // ... 其他统计 SQL
}
```

---

## 4. 前端 ECharts（参考代码）

```js
// Vue 3 / Element Plus
import * as echarts from 'echarts';

const chart = echarts.init(document.getElementById('stock-pie'));
chart.setOption({
  title: { text: '物料库存分布' },
  tooltip: { trigger: 'item' },
  series: [{
    type: 'pie',
    data: data.topMaterials.map(m => ({ name: m.name, value: m.amount }))
  }]
});
```

---

## 5. 自查

- [ ] 实现 Dashboard 接口，返回今日入库 / 出库 / 库存总值
- [ ] 给 Dashboard 加 1 分钟 Redis 缓存
- [ ] 跑接口看响应是否一致

## 代码示例

→ [`code/project/erp-business/.../dashboard/`](../../code/project/erp-business/)
