package com.smartfarm.chameleon.domain.login.api;

import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.login.application.LoginService;
import com.smartfarm.chameleon.domain.login.dto.TokenDTO;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;






@Slf4j
@RestController
@Tag(name = "로그인 API", description = "로그인, 로그아웃, 사용자 이름 조회 등 token을 사용한 기능")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private Bucket bucket;

    @PostMapping("/login")
    @Operation(summary = "로그인" , description = "사용자 아이디, 비밀번호를 받고 Access Token, Refresh Token을 반환하는 API")
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO userDTO, HttpServletResponse response ) {

        log.info("LoginController : 로그인 API");

        Optional<TokenDTO> token = loginService.login(userDTO);

        ConsumptionProbe use_bucket = bucket.tryConsumeAndReturnRemaining(1);

        if(token.isPresent() && use_bucket.isConsumed() ){

            log.info("LoginController : 로그인 성공");

            ResponseCookie cookie = ResponseCookie.from("REFRESH_TOKEN", token.get().getRefresh_token())
                                                .path("/")
                                                .httpOnly(true)
                                                .sameSite("Strict")
                                                .secure(true)
                                                .maxAge(7 * 24 * 60 * 60)
                                                .build();

            response.addHeader("Set-Cookie", cookie.toString());

            // 기존의 TokenDTO에서 refresh_token 제거
            token.get().setRefresh_token(null);

            return new ResponseEntity<>(token.get(), HttpStatus.OK);
        }else if(token.isPresent() && !use_bucket.isConsumed()){

            // 다음 리필까지 남은 시간(다음 초기화 시간)을 나노초에서 초로 변환
            long nanoRateTime = use_bucket.getNanosToWaitForRefill();
            long secondRateWait = (long) Math.ceil((double) nanoRateTime / 1_000_000_000.0);

            // 응답 헤더에 추가
            response.addHeader("X-RateLimit-Limit", String.valueOf(60)); // 요청 제한 횟수 
            response.addHeader("X-Ratelimit-Retry-After", String.valueOf(secondRateWait)); // 다음 요청까지 남은 시간

            log.info("LoginController : 로그인 실패");
            return new ResponseEntity<>(null, HttpStatus.TOO_MANY_REQUESTS);

        }else{    

            log.info("LoginController : 로그인 실패");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/api/logout")
    @Operation(summary = "로그아웃" , description = "Redis에서 Refresh Token 삭제")
    public void logout(@AuthenticationPrincipal(expression = "ID") String USER_ID) {
    
        log.info("LoginController : 로그아웃 API");

        loginService.logout(USER_ID);

    }

    @GetMapping("/get_name")
    @Operation(summary = "사용자 이름 조회" , description = "SecurityContext에 저장돼 있는 사용자 이름 반환")
    public ResponseEntity<String> get_name(@AuthenticationPrincipal(expression = "NAME") String USER_NAME) {

        log.info("LoginController : 사용자 이름 조회 API");

        return new ResponseEntity<>(USER_NAME, HttpStatus.OK);
    }  

    @GetMapping("/test")
    public void test() {
        
        log.info("server loading test");

    }
    
    
    
    
}
