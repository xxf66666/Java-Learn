package com.learning.product;

import com.learning.common.BusinessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
// 并发安全的 HashMap
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版商品服务。Week 7 替换为 MyBatis-Plus + MySQL。
 *
 * @Service 把这个类注册成 Spring Bean，可被注入到 Controller
 */
@Service
public class ProductService {

    // ConcurrentHashMap：多线程并发请求时仍然安全
    // Map<Long, Product>: id → 商品
    private final Map<Long, Product> store = new ConcurrentHashMap<>();

    // 自增 id：AtomicLong 保证多线程递增不冲突
    private final AtomicLong seq = new AtomicLong();

    /** 新增 */
    public Product create(Product p) {
        long id = seq.incrementAndGet();      // 原子 +1
        p.setId(id);

        LocalDateTime now = LocalDateTime.now();    // 当前时间
        p.setCreatedAt(now);
        p.setUpdatedAt(now);

        store.put(id, p);
        return p;
    }

    /** 修改：根据 id 查出来，更新字段 */
    public Product update(Long id, Product upd) {
        Product cur = store.get(id);
        if (cur == null) throw new BusinessException(40401, "商品不存在 id=" + id);

        // 增量更新 cur 的字段
        cur.setName(upd.getName());
        cur.setPrice(upd.getPrice());
        cur.setStock(upd.getStock());
        cur.setUpdatedAt(LocalDateTime.now());

        return cur;
    }

    /** 删除 */
    public void delete(Long id) {
        // Map.remove 返回被删的 value；null 说明本来就没有
        if (store.remove(id) == null) throw new BusinessException(40401, "商品不存在 id=" + id);
    }

    /** 按 id 查 */
    public Product getById(Long id) {
        Product p = store.get(id);
        if (p == null) throw new BusinessException(40401, "商品不存在 id=" + id);
        return p;
    }

    /** 列表（可选按名搜索） */
    public List<Product> list(String namePattern) {
        // values() 拿到所有 value 的 Collection 视图
        // 用 stream 链式过滤 + 排序 + 收集
        var stream = store.values().stream();

        // hasLength: name 非 null 且非空才过滤
        if (namePattern != null && !namePattern.isBlank()) {
            // contains 是模糊匹配（"iPhone" 包含 "Phone"）
            stream = stream.filter(p -> p.getName().contains(namePattern));
        }

        // Comparator.comparing(取键函数)：按取出来的字段排序
        return stream.sorted(Comparator.comparing(Product::getId)).toList();
    }
}
