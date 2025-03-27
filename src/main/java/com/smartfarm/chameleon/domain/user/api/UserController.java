package com.smartfarm.chameleon.domain.user.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.domain.user.application.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Slf4j
@RequestMapping("/user")
@Tag(name = "사용자", description = "사용자과 관련된 API")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @Operation(summary = "사용자 정보 반환" , description = "이름, 관심 작물, 아이디 정보를 반환하는 API")
    public ResponseEntity<UserDTO> read_user(@RequestHeader("Authorization") String access_token) {

        log.info("UserController : 사용자 정보 반환 API");

        return new ResponseEntity<>(userService.read_user(access_token.substring(7)), HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "사용자 정보 수정" , description = "이름, 관심 작물을 수정하는 API")
    public void update_user(@RequestHeader("Authorization") String access_token, @RequestBody UserDTO userDTO) {
        
        log.info("UserController : 사용자 정보 수정 API");

        userService.update_user(access_token.substring(7), userDTO);
    }
    
    
}
