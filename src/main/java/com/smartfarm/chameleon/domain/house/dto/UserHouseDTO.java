package com.smartfarm.chameleon.domain.house.dto;

import lombok.Data;

@Data
public class UserHouseDTO {

    // 사용자 인덱스 아이디
    private String id;

    // 사용자 농장의 백엔드 주소
    private String house_back_url;

    // 사용자 농장 아이디
    private int house_id;

    // 사용자 농자 이름
    private String house_name;

    
}
