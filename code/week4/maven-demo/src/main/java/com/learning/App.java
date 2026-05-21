package com.learning;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        log.info("App 启动，args = {}", (Object) args);

        String name = args.length > 0 ? args[0] : "world";

        if (StringUtils.isBlank(name)) {
            log.warn("name 是空，用默认值");
            name = "world";
        }

        System.out.println("Hello, " + StringUtils.capitalize(name) + "!");
    }
}
