package com.learning.service;

import com.learning.aspect.LogTime;
import org.springframework.stereotype.Service;

/**
 * 业务 Service：注意里面**没有任何**日志代码
 * 日志是由 LogAspect 通过 AOP 自动插入的
 */
@Service
public class CalcService {

    // 这个方法在 service 包下，会被 LogAspect 的"切点 1"拦截
    public int add(int a, int b) { return a + b; }

    // 这个方法会触发异常分支
    public int divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("除数不能为 0");
        return a / b;
    }

    /**
     * 这个方法既在 service 包下（切点 1），又标了 @LogTime（切点 2）
     * → 两个切面都会拦它（看日志会有 SERVICE 和 @LogTime 两组）
     */
    @LogTime
    public long fib(int n) {
        // 经典斐波那契：迭代版，O(n)
        if (n < 2) return n;
        long a = 0, b = 1;
        // 注意循环里 t 是局部变量，每轮重新声明也没问题
        for (int i = 2; i <= n; i++) { long t = a + b; a = b; b = t; }
        return b;
    }
}
