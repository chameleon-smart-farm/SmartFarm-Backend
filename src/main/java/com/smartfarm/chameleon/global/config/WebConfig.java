package com.smartfarm.chameleon.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8077", "https://moduleup.cloud")   // local, 배포 환경의 프론트 엔드 주소
                .allowedMethods("*")            // 모든 HTTP Method 허용
                .allowedHeaders("*")            // 모든 HTTP 헤더 허용
                .allowCredentials(true);  // 쿠키 인증 요청 허용
    }
}
