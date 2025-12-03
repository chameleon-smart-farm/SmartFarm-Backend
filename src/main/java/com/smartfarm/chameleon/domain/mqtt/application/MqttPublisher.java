package com.smartfarm.chameleon.domain.mqtt.application;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.mqtt.dto.MqttPublisherDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MqttPublisher {
    
    @Autowired
    private MqttClient mqttClient;

    // 사용자에게 메시지 전달
    public void sendMessage(MqttPublisherDTO mqttPublisherDTO) {
        try {
            mqttClient.publish(mqttPublisherDTO.getTopic(), new MqttMessage(mqttPublisherDTO.getMessage().getBytes()));
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

}
