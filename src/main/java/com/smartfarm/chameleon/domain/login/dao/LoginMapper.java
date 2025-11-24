package com.smartfarm.chameleon.domain.login.dao;

import org.apache.ibatis.annotations.Mapper;

import com.smartfarm.chameleon.domain.login.dto.FCMTokenDTO;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

@Mapper
public interface LoginMapper {
    
    // 사용자 로그인
    public UserDTO login (UserDTO userDTO);

    // fcm 토큰 입력
    public void update_device_token(FCMTokenDTO fcm_data);

}
