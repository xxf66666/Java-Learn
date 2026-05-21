package com.learning.blog.controller;

import com.learning.blog.common.Result;
import com.learning.blog.entity.Comment;
import com.learning.blog.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService service;
    public CommentController(CommentService service) { this.service = service; }

    @PostMapping
    public Result<Long> create(@RequestBody Map<String, Object> body) {
        Long articleId = ((Number) body.get("articleId")).longValue();
        Long userId = ((Number) body.get("userId")).longValue();
        String content = (String) body.get("content");
        return Result.ok(service.create(articleId, userId, content));
    }

    @GetMapping
    public Result<List<Comment>> listByArticle(@RequestParam Long articleId) {
        return Result.ok(service.listByArticle(articleId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}
