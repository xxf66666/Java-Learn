package com.learning;

// JUnit 5 的核心注解全在 org.junit.jupiter.api
import org.junit.jupiter.api.*;
// 参数化测试相关
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// import static：把静态方法导入当前命名空间，调用时不用写类名
// 等价于让 assertEquals 直接可用，否则要写 Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.*;

// @DisplayName: 在测试报告里显示这个名字（更可读）
@DisplayName("Calculator 测试")
class CalculatorTest {

    // 测试用的字段；每个 @Test 之前都会重新初始化（看 @BeforeEach）
    Calculator calc;

    // @BeforeEach: 每个测试方法运行之前都跑一次
    // 用来准备测试夹具（fixture）
    @BeforeEach
    void setup() {
        calc = new Calculator();
    }

    // @Test 标记一个测试方法
    @Test
    @DisplayName("加法")
    void testAdd() {
        // assertEquals(预期, 实际) — 不相等就 fail
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void testDivide() {
        assertEquals(5, calc.divide(10, 2));
    }

    @Test
    @DisplayName("除以 0 抛异常")
    void testDivideByZero() {
        // assertThrows(预期异常类型, Lambda) — Lambda 跑起来必须抛出指定异常
        // 不抛或抛错类型测试失败；返回值是捕到的异常对象
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> calc.divide(10, 0));

        // 再断言异常消息内容
        assertTrue(ex.getMessage().contains("不能为 0"));
    }

    // @ParameterizedTest: 参数化测试，会跑多次，每次参数不同
    // name = "{0} + {1} = {2}": 显示名模板，{n} 是参数索引
    @ParameterizedTest(name = "{0} + {1} = {2}")
    // @CsvSource: 数据源，每行是一组参数
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
        // assertAll(Executable...): 跑所有 Lambda
        // 关键：前面失败也会继续跑后面的，最后汇总报告
        // 而连续写 assertEquals 时第一个失败就 stop
        assertAll(
            () -> assertEquals(5, calc.add(2, 3)),
            () -> assertEquals(1, calc.sub(3, 2)),
            () -> assertEquals(6, calc.mul(2, 3)),
            () -> assertEquals(5, calc.divide(10, 2))
        );
    }
}
