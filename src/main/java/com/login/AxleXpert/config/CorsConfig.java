package com.login.AxleXpert.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration - Disabled: CORS is handled in SecurityConfig to avoid conflicts
public class CorsConfig implements WebMvcConfigurer {

    // @Override - Disabled: CORS is handled in SecurityConfig
    public void addCorsMappings(CorsRegistry registry) {
        // CORS configuration moved to SecurityConfig.java
        // to ensure proper integration with Spring Security
    }
}
