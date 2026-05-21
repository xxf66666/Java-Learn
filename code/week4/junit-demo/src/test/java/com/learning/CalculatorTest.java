package com.learning;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculator 测试")
class CalculatorTest {

    Calculator calc;

    @BeforeEach
    void setup() { calc = new Calculator(); }

    @Test
    @DisplayName("加法")
    void testAdd() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void testDivide() {
        assertEquals(5, calc.divide(10, 2));
    }

    @Test
    @DisplayName("除以 0 抛异常")
    void testDivideByZero() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> calc.divide(10, 0));
        assertTrue(ex.getMessage().contains("不能为 0"));
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
        "1, 2, 3",
        "10, 20, 30",
        "-5, 5, 0",
        "0, 0, 0"
    })
    void testAddParam(int a, int b, int expected) {
        assertEquals(expected, calc.add(a, b));
    }

    @Test
    @DisplayName("多个断言一起执行")
    void testAll() {
        assertAll(
            () -> assertEquals(5, calc.add(2, 3)),
            () -> assertEquals(1, calc.sub(3, 2)),
            () -> assertEquals(6, calc.mul(2, 3)),
            () -> assertEquals(5, calc.divide(10, 2))
        );
    }
}
