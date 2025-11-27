package com.smartfarm.chameleon.domain.fcm.dao;

import org.apache.ibatis.annotations.Mapper;

import com.smartfarm.chameleon.domain.fcm.dto.FCMTokenDTO;

@Mapper
public interface FCMMapper {

    // 사용자 아이디로 FCM 토큰 조회
    public String get_fcm_token(String user_id);

    // fcm 토큰 입력
    public void update_device_token(FCMTokenDTO fcm_data);

}