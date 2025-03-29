package com.smartfarm.chameleon.domain.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.domain.user.dao.UserMapper;
import com.smartfarm.chameleon.global.jwt.JwtTokenProvider;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자 정보 : 이름, 관심 작물, 아이디 정보 반환
     * access_token에서 사용자 아이디를 가져와 DB에서 정보 찾기
     * 
     * @param access_token
     * @return
     */
    public UserDTO read_user(String access_token){

        // 사용자 아이디
        String user_id = jwtTokenProvider.getUserID(access_token);

        return userMapper.read_user(user_id);
    }

    /**
     * 사용자 이름, 관심 작물 수정
     * access_token에서 사용자 아이디를 가져와 userDTO에 추가
     * 
     * @param access_token
     * @param userDTO : 사용자의 수정 정보 저장
     */
    public void update_user(String access_token, UserDTO userDTO){
        
        // 사용자 아이디
        String user_id = jwtTokenProvider.getUserID(access_token);
        userDTO.setUser_id(user_id);

        userMapper.update_user(userDTO);
    }
    
    /**
     * 사용자가 입력한 serial번호를 받아서 해당하는 농장이 있는지 확인
     * 
     * @param serial
     * @return : 해당하는 농장 아이디
     */
    public int validate_serial(String serial){
        return userMapper.validate_serial(serial);
    }
}
