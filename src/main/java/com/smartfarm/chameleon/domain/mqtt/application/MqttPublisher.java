package com.smartfarm.chameleon.domain.mqtt.application;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.mqtt.dto.MqttPublisherDTO;

import lombok.extern.slf4j.Slf4j;
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

    private String message = "메시지가 발송 불안정, 다시 시도하겠습니다.";

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
        MqttMessage mqttMessage = new MqttMessage(mqttPublisherDTO.getTopic(), message.getBytes(StandardCharsets.UTF_8),
                    QualityOfService.AT_LEAST_ONCE, false);
        try {
            
            // MQTT 메시지 발행
            connection.publish(mqttMessage).get();

        } catch (Exception e) {
            log.debug("MQTT - 메시지 발행 실패");
        }

        // 모든 connect가 해제될 때까지 기다리는 메서드 (절대 주석 해제하지 말 것)
        // CrtResource.waitForNoResources();

    }

}
