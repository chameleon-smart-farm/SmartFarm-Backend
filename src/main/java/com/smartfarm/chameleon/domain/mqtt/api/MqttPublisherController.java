package com.smartfarm.chameleon.domain.mqtt.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.mqtt.application.MqttPublisher;
import com.smartfarm.chameleon.domain.mqtt.dto.MqttPublisherDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RequestMapping("/mqtt")
@Tag(name = "MQTT API", description = "MQTT 메시지 발행")
@RestController
public class MqttPublisherController {
 
    @Autowired
    private MqttPublisher mqttPublisher;

    @PostMapping("/send_message")
    public void send_message(@RequestBody MqttPublisherDTO mqttPublisherDTO) {
        
        log.debug("MqttPublisherController - send_message API");
        mqttPublisher.sendMessage(mqttPublisherDTO);
        
    }
    

}
