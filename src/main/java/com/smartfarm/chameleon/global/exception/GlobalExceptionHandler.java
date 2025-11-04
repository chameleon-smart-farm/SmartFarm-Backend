package com.smartfarm.chameleon.global.exception;

import java.net.ConnectException;
import java.net.MalformedURLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<String> handleConnectException(ConnectException e){
        
        log.error("GlobalExceptionHandler : ConnectException 발생 " );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("서버 내부 오류입니다.");
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<String> handleMalformedURLException(MalformedURLException e){
        log.error(" : MalformedURLException 발생");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("올바른 요청이 아닙니다.");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException e){
        log.error(" : IOException 발생");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("처리 과정 중 오류가 발생했습니다. 요청을 다시 확인해 주세요.");
    }
    
}
