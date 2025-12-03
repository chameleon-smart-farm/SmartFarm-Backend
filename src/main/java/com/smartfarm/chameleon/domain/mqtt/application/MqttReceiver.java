package com.smartfarm.chameleon.domain.mqtt.application;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MqttReceiver implements MqttCallback {
    
    @Autowired
    private MqttClient mqttClient;

    @PostConstruct
    public void init(){
        mqttClient.setCallback(this);
    }
    
    
    // 연결이 끊어졌을 때 처리 로직
    @Override
    public void connectionLost(Throwable cause) {
        
        log.info("MqttReceiver - connectionLost : 연결이 끊어졌습니다.");
        throw new UnsupportedOperationException("연결이 끊어졌습니다.");
    }

    // 메시지가 도착했을 때 처리 로직
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.info("MqttReceiver - messageArrived : 메시지가 도착했습니다.");
        log.info("MqttReceiver - messageArrived : topic : {} / message : {}", topic, message);
    }

    // 구독 신청
    public boolean subscribe(final String topic) throws Exception {

        if (topic != null) {
            mqttClient.subscribe(topic, 0);
        }

        return true;
    }

    // 메시지 전송이 완료되었을 때 처리 로직
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("MqttReceiver - deliveryComplete : 메시지 전송이 완료되었습니다.");
    }
    
}
