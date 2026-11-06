package com.smartfarm.chameleon.global.exception;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<String> handleConnectException(ConnectException e, HttpServletRequest request){
        
        log.error("GlobalExceptionHandler : ConnectException 발생 " );

        loggingException(e, request);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("서버 내부 오류입니다.");
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<String> handleMalformedURLException(MalformedURLException e, HttpServletRequest request){
        log.error("GlobalExceptionHandler : MalformedURLException 발생");

        loggingException(e, request);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("올바른 요청이 아닙니다.");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e, HttpServletRequest request){
        log.error("GlobalExceptionHandler : IOException 발생");

        loggingException(e, request);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("처리 과정 중 오류가 발생했습니다. 요청을 다시 확인해 주세요.");
    }

    @ExceptionHandler(SSLHandshakeException.class)
    public ResponseEntity<String> handleSSLHandshakeException(SSLHandshakeException e, HttpServletRequest request){
        log.error("GlobalExceptionHandler : SSLHandshakeException 발생");

        loggingException(e, request);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("인증되지 않은 사용자입니다. 요청을 거절합니다.");
    }

    @ExceptionHandler(SSLPeerUnverifiedException.class)
    public ResponseEntity<String> handleSSLPeerUnverifiedException(SSLPeerUnverifiedException e, HttpServletRequest request){
        log.error("GlobalExceptionHandler : SSLPeerUnverifiedException 발생");

        loggingException(e, request);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("인증되지 않은 사용자입니다. 요청을 거절합니다.");
    }

    /* 개별 에러 메서드에서 잡지 못한 에러 처리 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e,HttpServletRequest request){
        
        loggingException(e, request);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부 오류입니다.");

    }

    /* 각 에러의 상세 로그를 남기는 공통 메서드 */
    public void loggingException(Exception e, HttpServletRequest request){

        // request 에서 캐싱된 ContentCachingRequestWrapper 꺼내기
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request.getAttribute("CONTENT_CACHING_REQUEST_WRAPPER");

        // request에서 uri 꺼내기
        String uri = request.getRequestURI();

        // ContentCachingRequestWrapper 에서 body 꺼내기
        String body = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        // 로그에 uri와 body를 추가
        MDC.put("uri", uri);
        MDC.put("body", body);

        log.error("GlobalExceptionHandler : {}", e.getClass());

        MDC.clear();

    }
    
}
