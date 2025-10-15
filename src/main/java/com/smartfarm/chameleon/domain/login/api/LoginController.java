package com.smartfarm.chameleon.domain.login.api;

import org.springframework.web.bind.annotation.RestController;
import com.smartfarm.chameleon.domain.login.application.LoginService;
import com.smartfarm.chameleon.domain.login.dto.TokenDTO;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;





@Slf4j
@RestController
@Tag(name = "로그인 API", description = "로그인, 로그아웃, 사용자 이름 조회 등 token을 사용한 기능")
public class LoginController {

    @Autowired
    private LoginService loginService;


    @PostMapping("/login")
    @Operation(summary = "로그인" , description = "사용자 아이디, 비밀번호를 받고 Access Token, Refresh Token을 반환하는 API")
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO userDTO ) {

        log.info("LoginController : 로그인 api");

        Optional<TokenDTO> token = loginService.login(userDTO);

        if(token.isPresent()){

            log.info("LoginController : 로그인 성공");
            return new ResponseEntity<>(token.get(), HttpStatus.OK);
        }else{

            log.info("LoginController : 로그인 실패");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/api/logout")
    @Operation(summary = "로그아웃" , description = "Redis에서 Refresh Token 삭제")
    public void logout(@AuthenticationPrincipal(expression = "ID") String USER_ID) {
    
        log.info("LoginController : 로그아웃 api");

        loginService.logout(USER_ID);

    }

    @GetMapping("/get_name")
    @Operation(summary = "사용자 이름 조회" , description = "SecurityContext에 저장돼 있는 사용자 이름 반환")
    public ResponseEntity<String> get_name(@AuthenticationPrincipal(expression = "NAME") String USER_NAME) {

        log.info("LoginController : 사용자 이름 조회 api");

        return new ResponseEntity<>(USER_NAME, HttpStatus.OK);
    }
    

    @GetMapping("/test")
    public void test(@RequestHeader("Authorization") String access_token, @RequestHeader("REFRESH_TOKEN") String refresh_token) {
        
        log.info("Access Token : " + access_token + ", Refresh Token : " + refresh_token);

    }
    
    
    
    
}
