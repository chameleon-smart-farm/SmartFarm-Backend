package com.smartfarm.chameleon.domain.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.smartfarm.chameleon.domain.login.dto.UserDTO;

@Mapper
public interface UserMapper {

    // 사용자 정보 : 이름, 관심 작물, 아이디 정보 반환
    public UserDTO read_user(String user_id);

    // 사용자 이름, 관심 작물 수정
    public void update_user(UserDTO userDTO);
    
}
