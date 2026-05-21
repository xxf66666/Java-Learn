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

    @GetMapping
    public Result<Page<Article>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String title) {
        return Result.ok(service.listPage(page, size, title));
    }

    @GetMapping("/{id}")
    public Result<Article> get(@PathVariable Long id) {
        return Result.ok(service.getDetail(id));
    }

    @PostMapping
    public Result<Long> publish(@RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        return Result.ok(service.publish(userId, title, content));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}
