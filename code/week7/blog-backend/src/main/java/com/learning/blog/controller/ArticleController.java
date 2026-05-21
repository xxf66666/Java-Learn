package com.learning.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.blog.common.Result;
import com.learning.blog.entity.Article;
import com.learning.blog.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService service;
    public ArticleController(ArticleService service) { this.service = service; }

    /**
     * 分页列表 + 可选标题关键字搜索
     *
     * defaultValue: 没传时用默认值（page=1, size=10）
     */
    @GetMapping
    public Result<Page<Article>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String title) {
        return Result.ok(service.listPage(page, size, title));
    }

    /** 按 id 查 */
    @GetMapping("/{id}")
    public Result<Article> get(@PathVariable Long id) {
        return Result.ok(service.getDetail(id));
    }

    /**
     * 发文：示范用 Map<String, Object> 接收 JSON
     * 真实项目应该用专门的 DTO（如 ArticlePublishReq）
     *
     * (Number) cast：JSON 数字反序列化默认是 Integer / Long，这里统一转 Long
     */
    @PostMapping
    public Result<Long> publish(@RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        return Result.ok(service.publish(userId, title, content));
    }

    /** 删除文章 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}
