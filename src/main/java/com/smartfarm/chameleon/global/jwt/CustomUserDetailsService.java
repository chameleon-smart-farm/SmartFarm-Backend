package com.smartfarm.chameleon.global.jwt;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.login.dao.LoginMapper;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public CustomUserDetail loadUserByUsername(String user_id) throws UsernameNotFoundException {
        
        UserDTO user = loginMapper.login(UserDTO.builder().user_id(user_id).build());

        return Optional.ofNullable(user)
                    .map(this::createUserDetails)
                    .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

    }

    /**
     * 사용자 DB pk(Id), 사용자 아이디(User_id), 
     * 사용자 이름(user_name), 사용자 관심 작물(user_faw_crop)
     * 을 저장한 CustomUserDetails를 반환한다.
     * 
     * @param user : LoginMapper에서 가져온 UserDTO 객체
     * @return : 위의 항목이 담긴 CustomUserDetail 반환
     */
    private CustomUserDetail createUserDetails(UserDTO user){

        return CustomUserDetail.builder()
                                .ID(user.getUser_id())
                                .PK(user.getId())
                                .NAME(user.getUser_name())
                                .FAW_CROP(user.getFaw_crop())
                                .AUTHORITY("general")
                                .build();
    }
}
