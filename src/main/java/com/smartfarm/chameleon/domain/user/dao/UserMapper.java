package com.smartfarm.chameleon.domain.user.dao;

import org.apache.ibatis.annotations.Mapper;

import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.domain.user.dto.SignUpDTO;

@Mapper
public interface UserMapper {

    // 사용자 이름, 관심 작물 수정
    public void update_user(UserDTO userDTO);

    // 시리얼 번호 확인 후 house_id 반환
    public int validate_serial(String serial);

    // 사용자 회원가입 : 사용자 이름, 아이디, 비밀번호, 관심 작물 입력
    public void sign_up(SignUpDTO signUpDTO);
    
}
