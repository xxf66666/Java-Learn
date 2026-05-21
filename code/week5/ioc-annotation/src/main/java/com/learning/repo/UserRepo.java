package com.learning.repo;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版用户仓库。生产环境会接 MyBatis-Plus / JPA。
 */
@Repository
public class UserRepo {

    private final Map<Long, String> store = new HashMap<>();
    private final AtomicLong seq = new AtomicLong();

    public Long save(String name) {
        long id = seq.incrementAndGet();
        store.put(id, name);
        return id;
    }

    public Optional<String> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    public int count() { return store.size(); }
}
