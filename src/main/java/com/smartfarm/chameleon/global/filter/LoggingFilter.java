package com.smartfarm.chameleon.global.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class LoggingFilter extends OncePerRequestFilter {
    
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 추적용 trace_id 생성
        String traceId = UUID.randomUUID().toString();
        MDC.put("trace_id", traceId);

        // request를 ContentCachingRequestWrapper로 wrapping
        ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request, 1024);

        // request에 속성으로 저장 : 추후 ExceptionHandler에서 사용할 수 있도록 request attribute에 저장한다.
        // ExceptionHandler에서는 HttpServletRequest request로 request를 받기 때문에 ContentCachingRequestWrapper이 아닐 수도 있다.
        request.setAttribute("CONTENT_CACHING_REQUEST_WRAPPER", cachedRequest);

        filterChain.doFilter(cachedRequest, response);

        // 테스트용 - 꼭 doFilter 이후에 로그를 찍어야 캐싱된 데이터를 읽을 수 있다.
        // log.info("uri : {}, body : {}", cachedRequest.getRequestURI(), new String(cachedRequest.getContentAsByteArray(), StandardCharsets.UTF_8) );
        
    }
    
}
