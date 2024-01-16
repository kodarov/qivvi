package com.kodarovs.qivvi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestQivviApplication {

    public static void main(String[] args) {
        SpringApplication.from(QivviApplication::main).with(TestQivviApplication.class).run(args);
    }

}
