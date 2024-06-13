package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${SET_IMPL:default}")
    private String implementation;

    @Bean
    public ExamSystem examSystem() {
        if ("lazy".equalsIgnoreCase(implementation)) {
            return new LazyExamSystem();
        } else {
            return new OptimisticExamSystem();
        }
    }
}
