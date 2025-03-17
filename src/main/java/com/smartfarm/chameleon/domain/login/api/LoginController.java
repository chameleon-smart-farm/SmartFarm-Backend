package com.smartfarm.chameleon.domain.login.api;

import org.springframework.web.bind.annotation.RestController;
import com.smartfarm.chameleon.domain.login.application.LoginService;
import com.smartfarm.chameleon.domain.login.dto.TokenDTO;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.global.jwt.JwtTokenProvider;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Slf4j
@RestController
@Tag(name = "로그인", description = "로그인과 관련된 API")
public class LoginController {

    @Autowired
    private LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO userDTO ) {

        log.info("login api");

        Optional<TokenDTO> token = loginService.login(userDTO);
        
        if(token.isPresent()){

            log.info("로그인 성공!");

            return new ResponseEntity<>(token.get(), HttpStatus.OK);
        }else{

            log.info("로그인 실패..!");

            return new ResponseEntity<>(null);
        }
    }


    @GetMapping("/api/logout")
    public void logout(@RequestHeader("Authorization") String access_token) {
    
        log.info("logout api");

        loginService.logout(access_token.substring(7));

    }

    @GetMapping("/test")
    public void test(@RequestHeader("Authorization") String access_token, @RequestHeader("REFRESH_TOKEN") String refresh_token) {
        
        log.info("Access Token : " + access_token + ", Refresh Token : " + refresh_token);

    }
    
    
    
    
}
