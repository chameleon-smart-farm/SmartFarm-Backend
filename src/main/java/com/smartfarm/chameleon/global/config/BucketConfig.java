package com.smartfarm.chameleon.global.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class BucketConfig {

    private Map<String, Bucket> limit_map = new ConcurrentHashMap<>();

    // IP 호출
    private String getIP (HttpServletRequest httpServletRequest){
        return httpServletRequest.getRemoteAddr();
    }

    // Bucket 반환
    public Bucket resolveBucket(HttpServletRequest httpServletRequest){
        return limit_map.computeIfAbsent(getIP(httpServletRequest), k -> CreateBucket());
    }

    private Bucket CreateBucket(){
        
        //120초에 60개의 토큰씩 충전
        final Refill refill = Refill.intervally(60, Duration.ofSeconds(120));

        //버킷의 최대 크기는 60개
        final Bandwidth limit = Bandwidth.classic(60, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
    
}
