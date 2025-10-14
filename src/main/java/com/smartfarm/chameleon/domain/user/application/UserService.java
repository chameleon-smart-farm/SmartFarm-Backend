package com.smartfarm.chameleon.domain.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartfarm.chameleon.domain.house.application.HouseService;
import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.domain.user.dao.UserMapper;
import com.smartfarm.chameleon.domain.user.dto.SignUpDTO;
import com.smartfarm.chameleon.global.jwt.JwtTokenProvider;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private HouseService houseService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * 사용자 아이디, 사용자 이름, 사용자 관심 작물을
     * UserDTO에 저장해 반환
     * 
     * @param USER_ID : 사용자 아이디
     * @param USER_NAME : 사용자 이름
     * @param USER_FAW_CROP : 사용자 관심 작물
     * @return : 위의 항목이 담긴 UserDTO 반환
     */
    public UserDTO read_user(String USER_ID, String USER_NAME, String USER_FAW_CROP){

        return UserDTO.builder()
                        .user_id(USER_ID)
                        .user_name(USER_NAME)
                        .faw_crop(USER_FAW_CROP)
                        .build();
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

    /**
     * 사용자 회원가입 
     * - 사용자 이름, 아이디, 비밀번호, 관심 작물 입력
     * - 농장 아이디로 농장 이름과 키우는 작물 수정
     * 
     * @param signUpDTO
     */
    @Transactional
    public void sign_up(SignUpDTO signUpDTO){

        // 사용자 비밀번호 암호화
        String encode_pwd = encoder.encode(signUpDTO.getUser_pwd());
        signUpDTO.setUser_pwd(encode_pwd);

        // 사용자 회원가입 : 사용자 이름, 아이디, 비밀번호, 관심 작물 입력
        userMapper.sign_up(signUpDTO);

        // 농장 아이디, 농장 이름, 키우는 작물이 담긴 DTO 생성
        HouseInfoDTO houseInfoDTO = new HouseInfoDTO();
        houseInfoDTO.setHouse_id(signUpDTO.getHouse_id());
        houseInfoDTO.setHouse_name(signUpDTO.getHouse_name());
        houseInfoDTO.setHouse_crop(signUpDTO.getHouse_crop());

        // 사용자 회원가입 : 농장 아이디로 농장 이름과 키우는 작물 수정
        houseService.update_house_name(houseInfoDTO);

    }
}
