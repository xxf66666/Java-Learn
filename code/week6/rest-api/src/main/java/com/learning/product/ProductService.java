package com.learning.product;

import com.learning.common.BusinessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版商品服务。Week 7 替换为 MyBatis-Plus + MySQL。
 */
@Service
public class ProductService {

    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong();

    public Product create(Product p) {
        long id = seq.incrementAndGet();
        p.setId(id);
        LocalDateTime now = LocalDateTime.now();
        p.setCreatedAt(now);
        p.setUpdatedAt(now);
        store.put(id, p);
        return p;
    }

    public Product update(Long id, Product upd) {
        Product cur = store.get(id);
        if (cur == null) throw new BusinessException(40401, "商品不存在 id=" + id);
        cur.setName(upd.getName());
        cur.setPrice(upd.getPrice());
        cur.setStock(upd.getStock());
        cur.setUpdatedAt(LocalDateTime.now());
        return cur;
    }

    public void delete(Long id) {
        if (store.remove(id) == null) throw new BusinessException(40401, "商品不存在 id=" + id);
    }

    public Product getById(Long id) {
        Product p = store.get(id);
        if (p == null) throw new BusinessException(40401, "商品不存在 id=" + id);
        return p;
    }

    public List<Product> list(String namePattern) {
        var stream = store.values().stream();
        if (namePattern != null && !namePattern.isBlank()) {
            stream = stream.filter(p -> p.getName().contains(namePattern));
        }
        return stream.sorted(Comparator.comparing(Product::getId)).toList();
    }
}
