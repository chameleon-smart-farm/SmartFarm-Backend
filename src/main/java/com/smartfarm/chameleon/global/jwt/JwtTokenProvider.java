package com.smartfarm.chameleon.global.jwt;

import lombok.extern.slf4j.Slf4j;

import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;
import com.smartfarm.chameleon.domain.login.dto.TokenDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Slf4j
@Component
public class JwtTokenProvider {

    // secretKey 생성
    private SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // 토큰 만료 기간
    private final long ACCESS_EXPIRATION_TIME = 2*60*60*1000;
    private final long REFRESH_EXPIRATION_TIME = 7*24*60*60*1000;

    // access_token 생성
    public TokenDTO createAccessToken (String userid){

        String access_token = Jwts.builder()
                            .subject(userid)
                            .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))  // 제한기간 설정
                            .issuedAt(new Date())   // token 발급날짜
                            .signWith(secretKey)
                            .compact(); 

        TokenDTO token = new TokenDTO();
        token.setAccess_token(access_token);
        token.setRefresh_token(createRefreshToken(userid));

        return token;

    }

    // refresh_token 생성
    public String createRefreshToken (String userid){

        String refresh_token = Jwts.builder()
                            .subject(userid)
                            .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))  // 제한기간 설정
                            .issuedAt(new Date())   // token 발급날짜
                            .signWith(secretKey)
                            .compact(); 

        return refresh_token;

    }

    // 토큰 검증
    public boolean validateToken (String token) {

        try{

            Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getBody();

            log.info("token 만료시간 : " + claims.getExpiration());
            log.info("token 검증 결과 : " + claims.getExpiration().before(new Date()));
            
            return !claims.getExpiration().before(new Date());

        }catch(Exception e){
            return false;
        }

    }

    // 사용자 아이디 반환
    public String getUserID(String token) {
        
        String userid = Jwts.parser()
                                .verifyWith(secretKey)
                                .build()
                                .parseSignedClaims(token)
                                .getBody().getSubject();

        return userid;                       

    }

    // 유효기간 반환
    public Date getExpiration(String token){
        
        Date expiration = Jwts.parser()
                                .verifyWith(secretKey)
                                .build()
                                .parseSignedClaims(token)
                                .getBody().getExpiration();

        return expiration;  
    }

}
