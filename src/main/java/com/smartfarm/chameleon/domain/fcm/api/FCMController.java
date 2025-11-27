package com.smartfarm.chameleon.domain.fcm.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.fcm.application.FCMService;
import com.smartfarm.chameleon.domain.fcm.dto.FCMMessageDTO;
import com.smartfarm.chameleon.domain.fcm.dto.FCMTokenDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;


@Slf4j
@RequestMapping("/fcm")
@RestController
@Tag(name = "FCM API", description = "Firebase Cloud Messaging 관련 API")
public class FCMController {

    @Autowired
    private FCMService fcmService;

    @PostMapping("/send_message")
    @Operation(summary = "알림 전송" , description = "Firebase 서버에 알림 전송")
    public void send_message (@RequestBody FCMMessageDTO fcmMessageDTO) {
        
        log.info("FCMController : 알림 전송 API");

        fcmService.send_message(fcmMessageDTO);
        
    }
    

    @PutMapping("/update_fcm_token")
    @Operation(summary = "사용자 fcm_token 등록" , description = "사용자 기기별 token을 저장, 사용자와 기기는 1:1 관계")
    public void update_device_token(@AuthenticationPrincipal(expression = "ID") String USER_ID, @RequestBody FCMTokenDTO fcm_data) {
        
        log.info("FCMController : 사용자 fcm_token 등록 API");

        fcm_data.setUser_id(USER_ID);
        fcmService.update_device_token(fcm_data);
    }
    
}
