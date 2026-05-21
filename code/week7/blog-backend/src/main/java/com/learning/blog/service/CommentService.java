package com.learning.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.blog.common.BusinessException;
import com.learning.blog.entity.Comment;
import com.learning.blog.mapper.ArticleMapper;
import com.learning.blog.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    public CommentService(CommentMapper commentMapper, ArticleMapper articleMapper) {
        this.commentMapper = commentMapper;
        this.articleMapper = articleMapper;
    }

    public Long create(Long articleId, Long userId, String content) {
        if (articleMapper.selectById(articleId) == null) {
            throw new BusinessException(40401, "文章不存在");
        }
        Comment c = new Comment();
        c.setArticleId(articleId);
        c.setUserId(userId);
        c.setContent(content);
        commentMapper.insert(c);
        return c.getId();
    }

    public List<Comment> listByArticle(Long articleId) {
        return commentMapper.selectList(new LambdaQueryWrapper<Comment>()
            .eq(Comment::getArticleId, articleId)
            .orderByDesc(Comment::getCreatedAt));
    }

    public void delete(Long id) {
        commentMapper.deleteById(id);
    }
}
