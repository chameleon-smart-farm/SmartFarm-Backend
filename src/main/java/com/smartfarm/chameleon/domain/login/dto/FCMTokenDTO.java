package com.smartfarm.chameleon.domain.login.dto;

import lombok.Data;

@Data
public class FCMTokenDTO {
    private String user_id;
    private String device_token;
}
