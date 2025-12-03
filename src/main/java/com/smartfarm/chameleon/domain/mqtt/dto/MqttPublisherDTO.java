package com.smartfarm.chameleon.domain.mqtt.dto;

import lombok.Data;

@Data
public class MqttPublisherDTO {
    
    private String topic;
    private String message;

}
