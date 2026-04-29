package com.smartfarm.chameleon.domain.mqtt.application;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.mqtt.dto.MqttPublisherDTO;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class MqttPublisher {
    
    // @Autowired
    // private MqttClient mqttClient;

    // // 사용자에게 메시지 전달
    // public void sendMessage(MqttPublisherDTO mqttPublisherDTO) {
    //     try {
    //         mqttClient.publish(mqttPublisherDTO.getTopic(), new MqttMessage(mqttPublisherDTO.getMessage().getBytes()));
    //     } catch (MqttException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    @Autowired
    private MqttClientConnection connection;

    private String topic = "core/topic/tolocal";
    private String message = "Hello I'm Spring Boot";

    public void sendMessage(MqttPublisherDTO mqttPublisherDTO) {

        // mqttPublisherDTO Json 변환
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            message = objectMapper.writeValueAsString(mqttPublisherDTO);
            log.debug("MQTT - Json 변환 : " + message);
        } catch (JsonProcessingException e) {
            log.debug("MQTT - Json 변환 실패");
        }

        // 발행할 MQTT 메시지 설정
        MqttMessage mqttMessage = new MqttMessage(topic, message.getBytes(StandardCharsets.UTF_8),
                    QualityOfService.AT_LEAST_ONCE, false);
        try {
            
            // MQTT 메시지 발행
            connection.publish(mqttMessage).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        CrtResource.waitForNoResources();

    }

}
