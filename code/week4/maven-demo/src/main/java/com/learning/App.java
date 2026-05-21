// package 必须和文件路径对应：
// src/main/java/com/learning/App.java → package com.learning
package com.learning;

// 来自第三方依赖 commons-lang3 的工具类
// 看 pom.xml 里加了 org.apache.commons:commons-lang3
import org.apache.commons.lang3.StringUtils;
// SLF4J 日志门面（接口）
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    // 静态最终字段 + 大写命名 = 约定俗成的"常量"
    // Logger 一类一个，名字通常用类对象
    // LoggerFactory.getLogger 根据类名定位日志配置
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // log.info 用 {} 占位符 + 参数；优势：日志级别关闭时不会做字符串拼接
        // (Object) args 显式转 Object 是为了避免 SLF4J 把 args 数组当作多个参数
        log.info("App 启动，args = {}", (Object) args);

        // 三元 ? : 给个默认值
        String name = args.length > 0 ? args[0] : "world";

        // commons-lang3 工具方法：判断字符串是 null / 空 / 全是空格
        if (StringUtils.isBlank(name)) {
            log.warn("name 是空，用默认值");
            name = "world";
        }

        // capitalize：首字母大写（"hello" → "Hello"）
        System.out.println("Hello, " + StringUtils.capitalize(name) + "!");
    }
}
