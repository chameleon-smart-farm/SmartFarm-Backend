package com.smartfarm.chameleon.domain.login.dao;

import org.apache.ibatis.annotations.Mapper;

import com.smartfarm.chameleon.domain.login.dto.UserDTO;

@Mapper
public interface LoginMapper {
    
    // 사용자 로그인
    public UserDTO login (UserDTO userDTO);

}
