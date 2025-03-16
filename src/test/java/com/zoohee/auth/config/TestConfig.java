package com.zoohee.auth.config;

import static org.mockito.Mockito.mock;
import com.zoohee.auth.common.jwt.JwtFilter;
import com.zoohee.auth.common.jwt.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CorsFilter;

@TestConfiguration
public class TestConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return mock(JwtUtil.class);
    }

    @Bean
    public JwtFilter jwtFilter() {
        return mock(JwtFilter.class);
    }

    @Bean
    public CorsFilter corsFilter() {
        return mock(CorsFilter.class);
    }
}