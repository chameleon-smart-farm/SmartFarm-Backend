package com.smartfarm.chameleon.global.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQTTConfig {

    @Value("${mqtt.brokerUrl}")
    private String BROKER_URL;

    @Value("${mqtt.clientId}")
    private String CLIENT_ID;

    @Bean
    public MqttConnectOptions mqttConnectOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);              // 클라이언트와 서버가 재시작 및 재연결 시 상태를 기억한다.
        options.setKeepAliveInterval(30);

        return options;
    }

    @Bean
    public MqttClient mqttClient(){

        try {
            MqttClient client = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
            
            client.connect(mqttConnectOptions());
            client.subscribe("test/#");         // 구독할 topic, #은 와일드 카드

            return client;
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        
    }
    
    
}
