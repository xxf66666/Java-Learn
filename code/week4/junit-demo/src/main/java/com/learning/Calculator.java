package com.learning;

/**
 * 被测对象：一个简单的计算器。
 * 配套测试在 src/test/java/com/learning/CalculatorTest.java
 */
public class Calculator {

    // 加：两个 int 相加，结果还是 int
    public int add(int a, int b) { return a + b; }

    // 减
    public int sub(int a, int b) { return a - b; }

    // 乘
    public int mul(int a, int b) { return a * b; }

    /**
     * 除：除数为 0 时主动抛 IllegalArgumentException
     * 这是 unchecked 异常（RuntimeException 子类），编译器不强制 try-catch
     */
    public int divide(int a, int b) {
        // 业务校验：不允许除以 0
        if (b == 0) throw new IllegalArgumentException("除数不能为 0");
        // int / int 是整数除法（截断），10 / 3 = 3
        return a / b;
    }
}
