package com.smartfarm.chameleon.global.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Configuration
public class BucketConfig {

    @Bean
    public Bucket bucket(){
        
        //120초에 60개의 토큰씩 충전
        final Refill refill = Refill.intervally(60, Duration.ofSeconds(120));

        //버킷의 최대 크기는 60개
        final Bandwidth limit = Bandwidth.classic(60, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
}
