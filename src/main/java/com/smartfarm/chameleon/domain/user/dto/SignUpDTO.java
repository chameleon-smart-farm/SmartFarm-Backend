package com.smartfarm.chameleon.domain.user.dto;

import lombok.Data;

@Data
public class SignUpDTO {

    // 농장 아이디
    private int house_id;

    // 키우는 작물
    private String house_crop;

    // 농장 이름
    private String house_name;

    // 사용자 이름
    private String user_name;

    // 사용자 아이디
    private String user_id;

    // 사용자 비밀번호
    private String user_pwd;

    // 사용자 관심작물
    private String faw_crop;
    
}
