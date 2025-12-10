package com.wrms.newtype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WRMS NT Application - Counseling Management System
 *
 * This application provides a web-based counseling management system
 * built with Spring Boot, Spring Data JDBC, and Vaadin Flow.
 * 
 * Spring Modulith 기반의 모듈형 아키텍처를 사용합니다.
 */
@SpringBootApplication
@EnableScheduling
public class WrmsNtApplication {

    public static void main(String[] args) {
        SpringApplication.run(WrmsNtApplication.class, args);
    }

}

