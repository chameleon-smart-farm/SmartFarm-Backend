package com.smartfarm.chameleon.domain.login.dao;

import org.apache.ibatis.annotations.Mapper;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

@Mapper
public interface LoginMapper {
    
    // 사용자 로그인
    public UserDTO login (UserDTO userDTO);

    // 사용자 이름 반환
    public String read_user_name (String user_id);

}
