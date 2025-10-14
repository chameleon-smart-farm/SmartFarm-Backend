package com.smartfarm.chameleon.domain.user.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.domain.user.application.UserService;
import com.smartfarm.chameleon.domain.user.dto.SignUpDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RequestMapping("/user")
@Tag(name = "사용자 API", description = "사용자 정보 CRU와 회원가입 기능")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @Operation(summary = "사용자 정보 반환" , description = "이름, 관심 작물, 아이디 정보를 반환하는 API")
    public ResponseEntity<UserDTO> read_user(@AuthenticationPrincipal(expression = "ID") String USER_ID,
                                                @AuthenticationPrincipal(expression = "NAME") String USER_NAME,
                                                @AuthenticationPrincipal(expression = "FAW_CROP") String USER_FAW_CROP) {

        log.info("UserController : 사용자 정보 반환 API");
        log.debug("user_id : " + USER_ID);

        return new ResponseEntity<>(userService.read_user(USER_ID, USER_NAME, USER_FAW_CROP), HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "사용자 정보 수정" , description = "이름, 관심 작물을 수정하는 API")
    public void update_user(@RequestHeader("Authorization") String access_token, @RequestBody UserDTO userDTO) {
        
        log.info("UserController : 사용자 정보 수정 API");

        userService.update_user(access_token.substring(7), userDTO);
    }

    @PostMapping("/serial")
    @Operation(summary = "시리얼 번호 확인" , description = "시리얼 번호가 올바른지 확인하고 house_id를 반환하는 API")
    public ResponseEntity<Integer> validate_serial(@RequestBody String serial) {
        return new ResponseEntity<>(userService.validate_serial(serial),HttpStatus.OK);
    }

    @PostMapping("/sign_up")
    @Operation(summary = "사용자 회원 가입" , description = "사용자 이름, 아이디, 비밀번호, 관심 작물, 농장 아이디를 입력받고 농장 아이디로 농장 이름(회사 서버)과 키우는 작물(농장 서버)을 입력(수정)하는 API")
    public void putMethodName(@RequestBody SignUpDTO signUpDTO) {
        userService.sign_up(signUpDTO);
    }
    
    
    
}
