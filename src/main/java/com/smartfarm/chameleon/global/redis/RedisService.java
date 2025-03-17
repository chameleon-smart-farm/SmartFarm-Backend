package com.smartfarm.chameleon.global.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * redis에 key, value 형태로 값을 저장
     * 
     * @param user_id : user_id
     * @param refresh_token : refresh_token
     * @param expiredTime : refresh_token 절대적 만료 기간
     */
    public void setData(String user_id, String refresh_token, Long expiredTime){
        redisTemplate.opsForValue().set(user_id, refresh_token, expiredTime, TimeUnit.MILLISECONDS);
    }

    /**
     * redis에서 key를 통해 value 가져오기
     * filter에서 refresh_token이 db에 존재하는지 확인
     * 
     * @param key : user_id
     * @return : refresh_token
     */
    public String getData(String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    /**
     * redis에서 값 지우기
     * 사용자가 로그아웃 시 refresh_token 삭제
     * 
     * @param key : user_id
     */
    public void deleteData(String key){
        redisTemplate.delete(key);
    }
}