package com.learning;

public class Calculator {

    public int add(int a, int b) { return a + b; }

    public int sub(int a, int b) { return a - b; }

    public int mul(int a, int b) { return a * b; }

    public int divide(int a, int b) {
        if (b == 0) throw new IllegalArgumentException("除数不能为 0");
        return a / b;
    }
}
