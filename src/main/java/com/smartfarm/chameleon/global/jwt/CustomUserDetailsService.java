package com.smartfarm.chameleon.global.jwt;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.login.dao.LoginMapper;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String userID) throws UsernameNotFoundException {
        
        UserDTO user_info = new UserDTO();
        user_info.setUser_id(userID);

        UserDTO user = loginMapper.login(user_info);

        return Optional.ofNullable(user)
                    .map(this::createUserDetails)
                    .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

    }

    /**
     * 사용자 이름과 비밀번호를 저장한 User 객체를 반환
     * ! 사용자 이름과 비밀번호는 필수 값임!
     * 
     * @param user : DB에서 찾아온 User 정보
     * @return
     */
    private UserDetails createUserDetails(UserDTO user){

        return User.builder()
                    .username(user.getUser_name())
                    .password(encoder.encode(user.getUser_pwd()))
                    .build();
    }

    
}
