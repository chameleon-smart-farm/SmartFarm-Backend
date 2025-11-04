package com.smartfarm.chameleon.domain.login.application;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.smartfarm.chameleon.domain.login.dao.LoginMapper;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;
import com.smartfarm.chameleon.domain.login.dto.TokenDTO;
import com.smartfarm.chameleon.global.jwt.JwtTokenProvider;
import com.smartfarm.chameleon.global.redis.RedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisService redisService;


    /**
     * 사용자 아이디를 통해서 사용자 이름, 비밀번호를 가져온다.
     * DB에 사용자가 존재 && 비밀번호가 일치할 경우
     * Access_token과 Refresh Token을 TokenDTO에 담아 return한다.
     * 
     * DB에 사용자가 없거나 비밀번호가 일치하지 않을 때
     * Optional을 사용해 빈 객체를 return 한다.
     * 
     * @param userDTO : 사용자 아이디, 비밀번호 저장
     * @return Optional<TokenDTO> 또는 Optional.empty()
     */
    public Optional<TokenDTO> login (UserDTO userDTO){
        
        UserDTO user = loginMapper.login(userDTO);

        if(user == null){
            log.error("LoginService : 사용자 아이디가 존재하지 않습니다.");
            return Optional.empty();
        }

        if(encoder.matches(userDTO.getUser_pwd(), user.getUser_pwd())){

            // access_token과 refresh_token 생성
            TokenDTO token = jwtTokenProvider.createAccessToken(user.getUser_id());
            
            // redis에 refresh token 저장
            long expiration = jwtTokenProvider.getExpiration(token.getRefresh_token()).getTime();
            redisService.setData(user.getUser_id(), token.getRefresh_token(), expiration );
            
            return Optional.of(token);

        }else{
            log.error("LoginService : 사용자 비밀번호가 일치하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * redis에서 사용자 아이디를 key로 refresh_token을 삭제한다.
     * 
     * @param user_id : 사용자 아이디
     */
    public void logout (String user_id) {

        // 사용자 아이디를 key로 redis에서 refresh_token 삭제
        redisService.deleteData(user_id);

    }
    
}
