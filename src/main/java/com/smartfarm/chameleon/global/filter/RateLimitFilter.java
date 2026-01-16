package com.smartfarm.chameleon.global.filter;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartfarm.chameleon.global.config.BucketConfig;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final BucketConfig bucketConfig;

    public RateLimitFilter(BucketConfig bucketConfig){
        this.bucketConfig = bucketConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        Bucket bucket = bucketConfig.resolveBucket(request);
        ConsumptionProbe use_bucket = bucket.tryConsumeAndReturnRemaining(1);

        log.debug("접속 IP 주소 : {}", request.getRemoteAddr());

        if(use_bucket.isConsumed()){
            log.info("RateLimit 통과");
        }else{

            log.info("RateLimit 제한");

            // 다음 리필까지 남은 시간(다음 초기화 시간)을 나노초에서 초로 변환
            long nanoRateTime = use_bucket.getNanosToWaitForRefill();
            long secondRateWait = (long) Math.ceil((double) nanoRateTime / 1_000_000_000.0);

            response.setStatus(HttpStatus.SC_TOO_MANY_REQUESTS); // 429 상태 코드
            response.addHeader("X-RateLimit-Limit", String.valueOf(60)); // 요청 제한 횟수 
            response.addHeader("X-Ratelimit-Retry-After", String.valueOf(secondRateWait)); // 다음 요청까지 남은 시간
            response.setContentType("text/plain;charset=UTF-8" );
            response.getWriter().write("너무 많은 요청이 시도되었습니다.");
            response.getWriter().flush();
            return;
        }

        // 다음 필터로 넘기기
        filterChain.doFilter(request, response); 
    }
    
}
