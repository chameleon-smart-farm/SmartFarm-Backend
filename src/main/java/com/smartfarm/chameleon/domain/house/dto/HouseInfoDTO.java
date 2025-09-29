package com.smartfarm.chameleon.domain.house.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class HouseInfoDTO implements Serializable {
    
    // 농장 아이디
    private int house_id;

    // 키우는 작물
    private String house_crop;

    // 농장 주소
    private String house_add;

    // 농장 이름
    private String house_name;
}
