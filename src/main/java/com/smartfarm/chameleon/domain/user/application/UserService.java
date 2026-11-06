package com.smartfarm.chameleon.domain.user.application;

import java.util.Optional;

import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartfarm.chameleon.domain.house.application.HouseService;
import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.domain.user.dao.UserMapper;
import com.smartfarm.chameleon.domain.user.dto.SignUpDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

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
    public Optional<UserDTO> read_user(String USER_ID, String USER_NAME, String USER_FAW_CROP){

        try {
            
            return Optional.of(UserDTO.builder()
                        .user_id(USER_ID)
                        .user_name(USER_NAME)
                        .faw_crop(USER_FAW_CROP)
                        .build());

        } catch (Exception e) {
            log.error("사용자 정보 반환 실패 : {}", e.getClass());

            return Optional.empty();
        }
    }

    /**
     * 사용자 이름, 관심 작물 수정
     * SecurityContext에 저장된 user_pk를 userDTO에 저장
     * 
     * @param user_pk
     * @param userDTO
     */
    public Optional<String> update_user(int user_pk, UserDTO userDTO){
        
        try {
            
            userDTO.setId(user_pk);

            userMapper.update_user(userDTO);

            return Optional.of("사용자 정보 수정 완료");

        } catch (Exception e) {
            log.error("사용자 정보 수정 실패 : {}", e.getClass());

            return Optional.empty();
        }
    }
    
    /**
     * 사용자가 입력한 serial번호를 받아서 해당하는 농장이 있는지 확인
     * 
     * @param serial
     * @return : 해당하는 농장 아이디
     */
    public Optional<Integer> validate_serial(String serial){

        try {

            int house_id = userMapper.validate_serial(serial);
            return Optional.of(house_id);

        }catch(BindingException e){

            log.error("시리얼 번호가 일치하지 않습니다.");
            return Optional.empty();
        }
 
    }

    /**
     * 사용자 회원가입 
     * - 사용자 이름, 아이디, 비밀번호, 관심 작물 입력
     * - 농장 아이디로 농장 이름과 키우는 작물 수정
     * 
     * @param signUpDTO
     */
    @Transactional
    public Optional<String> sign_up(SignUpDTO signUpDTO){

        try {
            
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

            // 사용자 회원가입 : 사용자의 농장 추가 및 농장 아이디로 농장 이름과 키우는 작물 수정
            houseService.add_house(signUpDTO.getUser_pk(), houseInfoDTO);

            return Optional.of("회원가입 성공");

        } catch (Exception e) {
            log.error("회원가입 실패 : {}", e.getClass());

            return Optional.empty();
        }
    }
}
