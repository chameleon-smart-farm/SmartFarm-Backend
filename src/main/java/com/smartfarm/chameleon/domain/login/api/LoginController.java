package com.smartfarm.chameleon.domain.login.api;

import org.springframework.web.bind.annotation.RestController;
import com.smartfarm.chameleon.domain.login.application.LoginService;
import com.smartfarm.chameleon.domain.login.dto.FCMTokenDTO;
import com.smartfarm.chameleon.domain.login.dto.TokenDTO;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

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
import org.springframework.web.bind.annotation.PutMapping;






@Slf4j
@RestController
@Tag(name = "로그인 API", description = "로그인, 로그아웃, 사용자 이름 조회 등 token을 사용한 기능")
public class LoginController {

    @Autowired
    private LoginService loginService;


    @PostMapping("/login")
    @Operation(summary = "로그인" , description = "사용자 아이디, 비밀번호를 받고 Access Token, Refresh Token을 반환하는 API")
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO userDTO, HttpServletResponse response ) {

        log.info("LoginController : 로그인 API");

        Optional<TokenDTO> token = loginService.login(userDTO);

        if(token.isPresent()){

            log.info("LoginController : 로그인 성공");

            // refresh token은 쿠키에 담아서 response에 추가
            // Cookie cookie = new Cookie("REFRESH_TOKEN", token.get().getRefresh_token());
            // cookie.setPath("/");                 // 모든 경로에서 접근 가능
            // cookie.setHttpOnly(true);       // JavaScript에서 접근 불가
            // cookie.set
            // cookie.setSecure(true);             // HTTPS 통신에서만 전송
            // cookie.setMaxAge(7 * 24 * 60 * 60);      // 유효기간 7일
            // response.addCookie(cookie);

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

    @PutMapping("/fcm")
    @Operation(summary = "사용자 fcm_token 등록" , description = "사용자 기기별 token을 저장, 사용자와 기기는 1:1 관계")
    public void update_device_token(@AuthenticationPrincipal(expression = "ID") String USER_ID, @RequestBody FCMTokenDTO fcm_data) {
        
        log.info("LoginController : 사용자 fcm_token 등록 API");

        fcm_data.setUser_id(USER_ID);
        loginService.update_device_token(fcm_data);
    }
    

    @GetMapping("/test")
    public void test() {
        
        log.info("server loading test");

    }
    
    
    
    
}
