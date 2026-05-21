package com.learning.product;

import com.learning.common.Result;
// @Valid 触发 Bean Validation 校验
import jakarta.validation.Valid;
// Spring Web 注解
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API 控制器 - 商品 CRUD 五件套
 *
 * @RestController = @Controller + @ResponseBody
 * @RequestMapping 类级别前缀：所有方法的 URL 都以 /api/products 开头
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // 构造器注入（final + 单构造器）
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    /**
     * 列表
     * @GetMapping → 处理 GET /api/products
     * @RequestParam: 绑定 URL 查询参数 ?name=xxx
     * required=false: 允许不传
     */
    @GetMapping
    public Result<List<Product>> list(@RequestParam(required = false) String name) {
        return Result.ok(service.list(name));
    }

    /**
     * 按 id 查
     * @PathVariable: 从 URL 路径里拿 {id}
     */
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        return Result.ok(service.getById(id));
    }

    /**
     * 新增
     * @PostMapping → POST /api/products
     * @RequestBody: 把请求体 JSON 反序列化成 Product 对象（Jackson 自动处理）
     * @Valid: 触发 Product 字段上的校验注解；失败抛 MethodArgumentNotValidException
     */
    @PostMapping
    public Result<Product> create(@RequestBody @Valid Product p) {
        return Result.ok(service.create(p));
    }

    /**
     * 修改
     * PUT 用于"全量更新"；PATCH 用于"局部更新"，这里简化只用 PUT
     */
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody @Valid Product p) {
        return Result.ok(service.update(id, p));
    }

    /**
     * 删除
     * 返回 Result<Void>：表示无业务数据，仅成功 / 失败状态
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}
