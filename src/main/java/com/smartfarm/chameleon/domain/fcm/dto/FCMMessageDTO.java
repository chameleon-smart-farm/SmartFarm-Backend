package com.smartfarm.chameleon.domain.fcm.dto;

import lombok.Data;

@Data
public class FCMMessageDTO {
    
    private String user_id;
    private String title;
    private String body;
    
}
