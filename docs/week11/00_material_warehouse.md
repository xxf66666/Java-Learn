# Week 11 §00 · 物料 + 仓库

> ERP 业务的"主语"和"地点"。

---

## 1. 物料档案 `mat_material`

```sql
CREATE TABLE mat_material (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(64)  NOT NULL,         -- 物料编码 M0001 / SKU
    name        VARCHAR(128) NOT NULL,
    category    VARCHAR(64),                    -- 简单：字符串分类；进阶：树形 mat_category 表
    unit        VARCHAR(16)   DEFAULT '件',      -- 计量单位（个 / 件 / 箱 / 米）
    spec        VARCHAR(255),                   -- 规格型号
    price       DECIMAL(10,2) DEFAULT 0,         -- 参考单价
    status      TINYINT       DEFAULT 1,
    remark      VARCHAR(255),
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT       DEFAULT 0,
    UNIQUE KEY uk_code (code, deleted)
);
```

**字段设计要点**
- `code` 是业务编码（用户可见、可搜），不要用 `id`（数据库自增 ID）
- 计量单位 `unit` 严重影响业务，开始就要明确
- 启用 `status` 0/1（物料可能下架但不能物理删，因为历史订单关联它）

---

## 2. 仓库 `wms_warehouse`

```sql
CREATE TABLE wms_warehouse (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    code        VARCHAR(64)  NOT NULL,         -- W01 / W02
    name        VARCHAR(64)  NOT NULL,
    address     VARCHAR(255),
    status      TINYINT      DEFAULT 1,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0,
    UNIQUE KEY uk_code (code, deleted)
);
```

进阶版还会有"库位"`wms_location`（仓库下细分到货架/格子），本仓库简化掉。

---

## 3. 编码规则

物料 / 仓库 / 单据，每种业务对象都该有易记的"业务编码"：

| 类型 | 规则示例 |
|------|---------|
| 物料 | `M` + 4 位流水：`M0001` `M0002`（或按分类前缀 `EM0001` 电子料） |
| 仓库 | `W` + 2 位 |
| 客户 | `C` + 4 位 |
| 供应商 | `S` + 4 位 |
| 采购单 | `PO` + `yyyyMMdd` + 4 位流水：`PO20251201-0001` |
| 销售单 | `SO` + 同上 |
| 入库单 | `IN` + 同上 |
| 出库单 | `OUT` + 同上 |

实现方式：用 Redis 的 `INCR` 算每天的流水号，或加一张 `seq_table` 表。

```java
public String nextOrderNo(String prefix) {
    String day = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    String key = "seq:" + prefix + ":" + day;
    Long seq = redis.opsForValue().increment(key);
    redis.expire(key, Duration.ofDays(2));
    return prefix + day + String.format("-%04d", seq);
}
```

---

## 4. CRUD 接口

物料 / 仓库的接口和 Week 7 博客几乎一样：
- 列表（分页 + 模糊搜索）
- 查详情
- 新增
- 修改
- 删除（逻辑删）

会写一份就会写所有。代码量大，下面只看物料 Service 的关键节选：

```java
@Service
public class MaterialService {

    private final MaterialMapper mapper;

    public Long create(MaterialCreateReq req) {
        // 唯一性校验
        var exist = mapper.selectOne(new LambdaQueryWrapper<Material>()
            .eq(Material::getCode, req.getCode()));
        if (exist != null) throw new BusinessException(30002, "物料编码已存在");

        Material m = new Material();
        BeanUtils.copyProperties(req, m);
        m.setStatus(1);
        mapper.insert(m);
        return m.getId();
    }

    public Page<Material> page(MaterialQueryReq req) {
        return mapper.selectPage(new Page<>(req.getPage(), req.getSize()),
            new LambdaQueryWrapper<Material>()
                .like(StringUtils.hasText(req.getCode()), Material::getCode, req.getCode())
                .like(StringUtils.hasText(req.getName()), Material::getName, req.getName())
                .eq(req.getCategory() != null, Material::getCategory, req.getCategory())
                .orderByDesc(Material::getId));
    }
}
```

完整代码见 [`code/project/erp-business/`](../../code/project/erp-business/)。

---

## 5. 自查

- [ ] 创建 2 个仓库 / 5 个物料
- [ ] 按物料名模糊搜索能命中
- [ ] 物料编码重复时拒绝创建
- [ ] 用 Redis 实现一个日流水号生成器
