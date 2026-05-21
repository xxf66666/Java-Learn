# Week 12 §00 · EasyExcel 导入导出

> 阿里出品，内存友好（流式读写），ERP 报表场景标配。

---

## 1. 引入

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.4</version>
</dependency>
```

---

## 2. 导出：定义 VO + 注解

```java
public class MaterialExportVO {

    @ExcelProperty("物料编码")
    @ColumnWidth(20)
    private String code;

    @ExcelProperty("名称")
    @ColumnWidth(30)
    private String name;

    @ExcelProperty("单位")
    @ColumnWidth(10)
    private String unit;

    @ExcelProperty("价格")
    @ColumnWidth(15)
    @NumberFormat("#,##0.00")
    private BigDecimal price;

    @ExcelProperty(value = "状态", converter = StatusConverter.class)
    @ColumnWidth(10)
    private Integer status;

    // getter / setter
}
```

---

## 3. 导出接口

```java
@GetMapping("/export")
public void export(HttpServletResponse response) throws IOException {
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    response.setHeader("Content-Disposition",
        "attachment;filename=" + URLEncoder.encode("物料.xlsx", StandardCharsets.UTF_8));

    List<MaterialExportVO> data = service.listAllForExport();

    EasyExcel.write(response.getOutputStream(), MaterialExportVO.class)
             .sheet("物料")
             .doWrite(data);
}
```

---

## 4. 导入：定义监听器

```java
public class MaterialImportListener implements ReadListener<MaterialImportVO> {

    private static final int BATCH = 100;
    private final List<MaterialImportVO> buffer = new ArrayList<>();
    private final MaterialService service;

    public MaterialImportListener(MaterialService service) { this.service = service; }

    @Override
    public void invoke(MaterialImportVO row, AnalysisContext ctx) {
        buffer.add(row);
        if (buffer.size() >= BATCH) {
            service.batchImport(buffer);
            buffer.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext ctx) {
        if (!buffer.isEmpty()) {
            service.batchImport(buffer);
            buffer.clear();
        }
    }
}
```

```java
@PostMapping("/import")
public Result<Integer> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
    EasyExcel.read(file.getInputStream(), MaterialImportVO.class,
        new MaterialImportListener(materialService))
        .sheet().doRead();
    return Result.ok();
}
```

---

## 5. 模板下载

让用户先下个空表格、填好上传：

```java
@GetMapping("/template")
public void template(HttpServletResponse response) throws IOException {
    setExcelHeaders(response, "物料导入模板.xlsx");
    EasyExcel.write(response.getOutputStream(), MaterialImportVO.class)
             .sheet("模板")
             .doWrite(new ArrayList<>());
}
```

---

## 6. 校验 + 容错

导入时常见问题：
- 字段类型错（"abc" 放价格列）
- 必填字段空
- 唯一约束冲突（编码重复）

实操：**别让一条错误阻塞整批**。Listener 里 try-catch 单行，错误堆到 `errors` 列表，最后返回给前端：

```java
@Override
public void invoke(...) {
    try {
        validate(row);
        service.create(row);
    } catch (Exception e) {
        errors.add(new ImportError(currentRowNum, e.getMessage()));
    }
}
```

---

## 7. 自查

- [ ] 物料管理加导出按钮，能下载 xlsx
- [ ] 物料管理加导入按钮，上传 xlsx 后能插入数据库
- [ ] 导入时故意写一行编码重复，看是否其它行正常入库
- [ ] 加模板下载接口
