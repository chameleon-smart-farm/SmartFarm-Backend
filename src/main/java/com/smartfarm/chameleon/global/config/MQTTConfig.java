package com.smartfarm.chameleon.global.config;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.smartfarm.chameleon.domain.fcm.application.FCMService;
import com.smartfarm.chameleon.domain.fcm.dto.FCMMessageDTO;

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

    @Autowired
    private FCMService fcmService;
    
    // 구독 topic +/+로 모든 device로부터 MQTT 메시지를 받게 함.
    private final String topic = "core/topic/tocloud/+/+";

    // 누적된 request 수, Map의 key로 동작한다.
    private int request_count = 0;

    // CompletableFuture 객체가 저장될 request_list
    private Map<Integer, CompletableFuture> request_list = new HashMap<Integer, CompletableFuture>();

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

                // 메시지 UTF-8로 decode
                String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
                log.debug("MQTT - Received: topic : {} , payload : {}", msg.getTopic(), payload);

                // 메시지 JsonObject로 변환
                JSONParser jsonParser = new JSONParser();
                try {
                    
                    Object obj_payload = jsonParser.parse(payload);
                    JSONObject result = (JSONObject) obj_payload;

                    if(Objects.isNull(result.get("request_id")) || Integer.parseInt(result.get("request_id").toString()) == 0  ){

                        // request_id가 없거나 0이라면 OPC UA에서 먼저 보내는 메시지이므로 앱으로 PUSH 알림

                        FCMMessageDTO fcmMessageDTO = new FCMMessageDTO();
                        fcmMessageDTO.setBody(result.get("value").toString());
                        fcmMessageDTO.setUser_id("test");
                        fcmMessageDTO.setTitle("OPC UA에서 보내셨습니다 ^^");

                        log.debug("MQTT - 메시지 발행하겠습니다.");

                        fcmService.send_message(fcmMessageDTO);

                    }else{

                        // log.debug("MQTT - request_list 확인 : {}", request_list.values());
                        
                        // request_id가 있다면 RESTful API의 응답이므로 해당 RESTful API를 찾아서 응답 반환
                        CompletableFuture future = request_list.get(Integer.parseInt(result.get("request_id").toString()));

                        if(Objects.nonNull(future)){

                            future.complete(result.get("value").toString());
                            log.debug("MQTT - Received: CompletableFuture {}번에게 {}를 전달했습니다.", result.get("request_id").toString(), result.get("value").toString());
                        
                        }else{
                            log.debug("MQTT - Received: CompletableFuture가 null입니다.");
                        }

                        // 요청을 완료했으므로 제거
                        request_list.remove(result.get("request_id"));
          
                    }

                } catch (ParseException e) {
                    log.error("MQTTConfig 구독 - " + e);
                    throw new RuntimeException(e);
                }

            }).get();

            return connection;
        } catch (Exception e) {
            log.debug("MQTT - AWS IoT 연결 실패");
            throw new RuntimeException("MQTT - AWS IoT 연결 실패", e);
        }


    }

    // Map에 request_id와 completableFuture를 지정
    public void add_future(CompletableFuture completableFuture){

        // request_count를 1 증가시킨 후 Map에 저장
        request_list.put(++request_count, completableFuture);

        log.debug("MQTT - request_id {}에 completablaFuture 를 저장완료!", request_count);
    }

    // MQTT 메시지를 보낼 때 request_id를 지정하기 위해 호출
    public int return_count(){
        return request_count;
    }



}
