package com.learning.blog.service;

// LambdaQueryWrapper: MyBatis-Plus 提供的"条件构造器"，用方法引用代替字符串字段名
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// Page<T>: 分页对象，传入 selectPage 自动加 LIMIT 子句
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.blog.common.BusinessException;
import com.learning.blog.entity.Article;
import com.learning.blog.entity.User;
import com.learning.blog.mapper.ArticleMapper;
import com.learning.blog.mapper.UserMapper;
import org.springframework.stereotype.Service;
// @Transactional: 声明式事务
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ArticleService {

    // 同时依赖两个 Mapper：文章 + 用户（事务里要联动更新用户的文章数）
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    // 构造器注入两个依赖
    public ArticleService(ArticleMapper articleMapper, UserMapper userMapper) {
        this.articleMapper = articleMapper;
        this.userMapper = userMapper;
    }

    /**
     * 发文章 + 用户文章数 +1 —— 原子性事务示例
     *
     * @Transactional: 方法进入开启事务，正常返回 commit，抛 RuntimeException 自动 rollback
     * rollbackFor = Exception.class: 让 checked 异常也回滚（默认只回滚 RuntimeException）
     */
    @Transactional(rollbackFor = Exception.class)
    public Long publish(Long userId, String title, String content) {
        // 先校验用户存在
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException(40401, "用户不存在");

        // 1) 插入新文章
        Article a = new Article();
        a.setUserId(userId);
        a.setTitle(title);
        a.setContent(content);
        a.setViewCount(0);
        // insert: MyBatis-Plus 自带的 CRUD 方法之一
        // 调用后自增 id 会回填到 a.getId()
        articleMapper.insert(a);

        // 2) 更新用户的文章数（如果上一步成功，但这一步抛异常，事务回滚把文章也删掉）
        // 三元 + null 检查：第一次发文时 articleCount 可能是 null
        user.setArticleCount((user.getArticleCount() == null ? 0 : user.getArticleCount()) + 1);
        userMapper.updateById(user);

        // 返回新文章 id
        return a.getId();
    }

    /**
     * 分页 + 关键字搜索
     * readOnly = true: 标记为只读事务，数据库能做优化
     */
    @Transactional(readOnly = true)
    public Page<Article> listPage(int page, int size, String titleKeyword) {
        // LambdaQueryWrapper 动态条件：
        //   .like(condition, getter, value)
        //   condition 为 true 才加这个 LIKE 子句
        var wrapper = new LambdaQueryWrapper<Article>()
            .like(StringUtils.hasText(titleKeyword), Article::getTitle, titleKeyword)
            // orderByDesc(字段引用): 按字段降序
            .orderByDesc(Article::getCreatedAt);

        // selectPage(分页对象, wrapper)：MyBatis-Plus 的分页查询
        // new Page<>(当前页, 每页条数)
        return articleMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /** 查详情 */
    @Transactional(readOnly = true)
    public Article getDetail(Long id) {
        Article a = articleMapper.selectById(id);
        if (a == null) throw new BusinessException(40401, "文章不存在");
        return a;
    }

    /** 删除文章并联动减用户文章数 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Article a = articleMapper.selectById(id);
        if (a == null) throw new BusinessException(40401, "文章不存在");

        // 逻辑删除：实际 SQL 是 UPDATE article SET deleted = 1 ...
        articleMapper.deleteById(id);

        // 同步减作者的发文数
        User u = userMapper.selectById(a.getUserId());
        if (u != null && u.getArticleCount() != null && u.getArticleCount() > 0) {
            u.setArticleCount(u.getArticleCount() - 1);
            userMapper.updateById(u);
        }
    }
}
