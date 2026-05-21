package com.learning.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.blog.common.BusinessException;
import com.learning.blog.entity.Article;
import com.learning.blog.entity.User;
import com.learning.blog.mapper.ArticleMapper;
import com.learning.blog.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    public ArticleService(ArticleMapper articleMapper, UserMapper userMapper) {
        this.articleMapper = articleMapper;
        this.userMapper = userMapper;
    }

    /** 发文章 + 用户文章数 +1 —— 原子性事务示例 */
    @Transactional(rollbackFor = Exception.class)
    public Long publish(Long userId, String title, String content) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(40401, "用户不存在");

        Article a = new Article();
        a.setUserId(userId);
        a.setTitle(title);
        a.setContent(content);
        a.setViewCount(0);
        articleMapper.insert(a);

        user.setArticleCount((user.getArticleCount() == null ? 0 : user.getArticleCount()) + 1);
        userMapper.updateById(user);

        return a.getId();
    }

    @Transactional(readOnly = true)
    public Page<Article> listPage(int page, int size, String titleKeyword) {
        var wrapper = new LambdaQueryWrapper<Article>()
            .like(StringUtils.hasText(titleKeyword), Article::getTitle, titleKeyword)
            .orderByDesc(Article::getCreatedAt);
        return articleMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Transactional(readOnly = true)
    public Article getDetail(Long id) {
        Article a = articleMapper.selectById(id);
        if (a == null) throw new BusinessException(40401, "文章不存在");
        return a;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Article a = articleMapper.selectById(id);
        if (a == null) throw new BusinessException(40401, "文章不存在");
        articleMapper.deleteById(id);
        // 同步减用户的发文数
        User u = userMapper.selectById(a.getUserId());
        if (u != null && u.getArticleCount() != null && u.getArticleCount() > 0) {
            u.setArticleCount(u.getArticleCount() - 1);
            userMapper.updateById(u);
        }
    }
}
