package com.smartfarm.chameleon.domain.mqtt.dto;

import lombok.Data;

@Data
public class MqttPublisherDTO {
    
    private String  topic;
    private String  msg;
    private int     int_value;
    private double  dou_value;
    private int     request_id;

}
