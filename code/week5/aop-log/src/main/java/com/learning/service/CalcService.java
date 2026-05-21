package com.learning.service;

import com.learning.aspect.LogTime;
import org.springframework.stereotype.Service;

@Service
public class CalcService {

    public int add(int a, int b) { return a + b; }

    public int divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("除数不能为 0");
        return a / b;
    }

    @LogTime
    public long fib(int n) {
        if (n < 2) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) { long t = a + b; a = b; b = t; }
        return b;
    }
}
