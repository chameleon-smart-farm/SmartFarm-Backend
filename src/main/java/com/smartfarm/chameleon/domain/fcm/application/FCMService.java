package com.smartfarm.chameleon.domain.fcm.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.smartfarm.chameleon.domain.fcm.dao.FCMMapper;
import com.smartfarm.chameleon.domain.fcm.dto.FCMMessageDTO;
import com.smartfarm.chameleon.domain.fcm.dto.FCMTokenDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FCMService {
    
    @Autowired
    private FCMMapper fcmMapper;

    public void send_message(FCMMessageDTO messageDTO) {
        
        // 사용자의 Firebase 토큰 값을 조회
        String fcm_token = fcmMapper.get_fcm_token(messageDTO.getUser_id());
        log.info("fcm_token : {}", fcm_token);
        log.info("body : {}", messageDTO.getBody());

        // 메시지 구성
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(messageDTO.getTitle())
                .setBody(messageDTO.getBody())
                .build())
            .setToken(fcm_token)
            .build();

        try {
            // 메시지 전송
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCMService - send_message : 메시지 전송 성공 : " + response);

        } catch (FirebaseMessagingException e) {
            log.error("FCMService - send_message : 메시지 전송 실패 : " + e.getMessage());
        }
        
    }

    /**
     * 사용자 아이디에 해당하는 user에 fcm token을 저장
     * 추후 알림을 전송할 때 user 구분용으로 사용
     * 
     * @param user_id
     * @param device_token
     */
    public void update_device_token(FCMTokenDTO fcm_data){
        fcmMapper.update_device_token(fcm_data);
    }

}
