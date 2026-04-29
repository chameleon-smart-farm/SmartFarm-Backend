package com.smartfarm.chameleon.global.config;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.HostResolver;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

@Slf4j
@Configuration
public class MQTTConfig {

    @Value("${mqtt.aws.iot.client-id}")
    private String clientId;

    @Value("${mqtt.aws.iot.cert-path}")
    private String certPath;  // 사물 인증서 경로

    @Value("${mqtt.aws.iot.private-key-path}")
    private String keyPath;   // 사물 개인 키 경로

    @Value("${mqtt.aws.iot.endpoint}")
    private String endpoint;  // iot core 엔드 포인트

    @Value("${mqtt.aws.iot.port}")
    private Integer port;

    @Value("${mqtt.aws.iot.rootCa-path}")
    private String rootCaPath;  // rootCa 경로
    
    private final String topic = "core/topic/tocloud";
    private final String message = "Hello I'm Spring Boot";

    @Bean
    public MqttClientConnection awsIotMqtt(){

        // 연결이 끊어졌을 때, 재연결 될 때의 콜백
        MqttClientConnectionEvents callbacks = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                if (errorCode != 0) {
                    log.debug("MQTT Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode));
                }
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                log.debug("MQTT Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        };

        // 네트워크 연결
        EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
        HostResolver resolver = new HostResolver(eventLoopGroup);
        ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);

        // 인증서, 키를 바탕으로 connection builder 생성
        AwsIotMqttConnectionBuilder builder =
                AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath);

        // http 통신 시 rootCa도 필요
        if (rootCaPath != null) {
           builder.withCertificateAuthorityFromPath(null, rootCaPath);
        }

        // builder 옵션 설정
        builder.withBootstrap(clientBootstrap)
                .withConnectionEventCallbacks(callbacks)
                .withClientId(clientId)
                .withEndpoint(endpoint)
                .withPort(port.shortValue())
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);

        try {
            MqttClientConnection connection = builder.build();
            connection.connect().join();
            log.debug("MQTT6 - Connected to AWS IoT Core!");

            // 구독
            connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (msg) -> {

                String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
                log.debug("MQTT - Received: " + payload);

                // FCMMessageDTO fcmMessageDTO = new FCMMessageDTO();
                // fcmMessageDTO.setBody(message.toString());
                // fcmMessageDTO.setUser_id("test");
                // fcmMessageDTO.setTitle("OPC UA에서 보내셨습니다 ^^");

                // fcmService.send_message(fcmMessageDTO);

            }).get();

            return connection;
        } catch (Exception e) {
            throw new RuntimeException("MQTT - AWS IoT 연결 실패", e);
        }


    }

}
