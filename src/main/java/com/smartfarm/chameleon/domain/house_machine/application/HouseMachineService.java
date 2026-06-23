package com.smartfarm.chameleon.domain.house_machine.application;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.house.dao.HouseMapper;
import com.smartfarm.chameleon.domain.house_machine.dto.MachineSetDTO;
import com.smartfarm.chameleon.domain.house_machine.dto.MachineStatusDTO;
import com.smartfarm.chameleon.domain.mqtt.application.MqttPublisher;
import com.smartfarm.chameleon.domain.mqtt.dto.MqttPublisherDTO;
import com.smartfarm.chameleon.global.config.MQTTConfig;
import com.smartfarm.chameleon.global.toHouse.HttpHouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HouseMachineService {

    @Autowired
    private HttpHouse httpHouse;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private MQTTConfig mqttConfig;
    @Autowired
    private MqttPublisher mqttPublisher;

    /**
     * 농장 서버에 모터 상태를 조회 요청을 보내는 메서드
     * 
     * @param house_id
     * @return
     */
    public MachineStatusDTO get_motor_status(int house_id){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String get_url = houseMapper.read_back_url(house_id) + "/house_machine/motor/status";

        // get 요청 - 모터 상태
        JSONObject res_result = (JSONObject) httpHouse.get_http_connection(get_url).get();

        // 결과 생성
        MachineStatusDTO result = new MachineStatusDTO();
        result.setValue(Boolean.parseBoolean(res_result.get("motor_status").toString()));

        // 결과 출력
        log.info("get_motor_status - 모터 상태 : " + result.isValue());

        return result;
    }
    
    /**
     * 농장 서버에 모터 on/off 요청을 보내는 메서드
     * 
     * @param house_id
     * @param status
     */
    public void motor_on_off(int house_id, MachineStatusDTO status){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String post_url = houseMapper.read_back_url(house_id) + "/house_machine/motor/on_off";

        // post 요청 - 모터 on/off
        httpHouse.post_http_connection(post_url, status);

    }

    /**
     * 농장에 기기의 상태 반환을 요청하는 메서드
     * 
     * @param machine_kind : 요청할 기기 종류
     * @param house_id : device_id를 알아옴
     * @return : 읽어온 boolean 값을 반환
     */
    public Optional<MachineStatusDTO> read_machine_status(String machine_kind, int house_id){

        // CompletableFuture 생성 및 Map에 저장
        CompletableFuture future = new CompletableFuture<String>();
        mqttConfig.add_future(future);

        log.debug("HouseMachineService - read_machine_status : Map에 future 추가 완료");

        // house_id로 device_id 가져오기
        String device_id = houseMapper.read_device_id(house_id);

        // MQTT 메시지 발행
        MqttPublisherDTO mqttPublisherDTO = new MqttPublisherDTO();
        mqttPublisherDTO.setTopic("core/topic/tolocal/" + device_id + "/" + machine_kind);
        mqttPublisherDTO.setMsg("status");
        mqttPublisherDTO.setRequest_id(mqttConfig.return_count());

        mqttPublisher.sendMessage(mqttPublisherDTO);

        log.debug("HouseStatusService - read_machine_status : MQTT 메시지 발행 후 결과 대기 중");

        try {
            // CompletableFuture 가 complete된 후 결과값을 Double로 변환 후 반환
            String data = future.get(10, TimeUnit.SECONDS).toString();

            log.debug("HouseStatusService - read_machine_status : 성공적으로 {}를 전달받았습니다.", data);

            MachineStatusDTO result = new MachineStatusDTO();
            result.setValue(Boolean.parseBoolean(data));

            return Optional.of(result);

        } catch (InterruptedException e) {
            log.error("HouseStatusService - read_machine_status : InterruptedException 에러 발생");
            return Optional.empty();
        } catch (ExecutionException e) {
            log.error("HouseStatusService - read_machine_status : ExecutionException 에러 발생");
            return Optional.empty();
        } catch (TimeoutException e) {
            log.error("HouseStatusService - read_machine_status : TimeoutException 에러 발생");
            return Optional.empty();
        }

    }

    /**
     * 
     * 사용자 요청에 따라 기기의 상태를 변경하는 메서드
     * 
     * @param machine_kind : 요청할 기기 종류
     * @param house_id : device_id를 알아옴
     * @param status : 사용자가 조작한 boolean 값
     */
    public void update_machine_on_off(String machine_kind, int house_id, MachineStatusDTO status){
        // CompletableFuture 생성 및 Map에 저장
        CompletableFuture future = new CompletableFuture<String>();
        mqttConfig.add_future(future);

        log.debug("HouseMachineService - read_machine_status : Map에 future 추가 완료");

        // house_id로 device_id 가져오기
        String device_id = houseMapper.read_device_id(house_id);

        // MQTT 메시지 발행
        MqttPublisherDTO mqttPublisherDTO = new MqttPublisherDTO();
        mqttPublisherDTO.setTopic("core/topic/tolocal/" + device_id + "/" + machine_kind);
        mqttPublisherDTO.setMsg("operate");
        mqttPublisherDTO.setBool_value(status.isValue());
        mqttPublisherDTO.setRequest_id(mqttConfig.return_count());

        mqttPublisher.sendMessage(mqttPublisherDTO);

        log.debug("HouseStatusService - read_machine_status : MQTT 메시지 발행 후 결과 대기 중");

        try {
            // CompletableFuture 가 complete된 후 결과값을 Double로 변환 후 반환
            String data = future.get(10, TimeUnit.SECONDS).toString();

            log.debug("HouseStatusService - read_machine_status : 성공적으로 {}를 전달받았습니다.", data);

        } catch (InterruptedException e) {
            log.error("HouseStatusService - read_machine_status : InterruptedException 에러 발생");
        } catch (ExecutionException e) {
            log.error("HouseStatusService - read_machine_status : ExecutionException 에러 발생");
        } catch (TimeoutException e) {
            log.error("HouseStatusService - read_machine_status : TimeoutException 에러 발생");
        }
    }

    public Optional<MachineSetDTO> read_user_set_status(String user_set_kind, int house_id){

        // CompletableFuture 생성 및 Map에 저장
        CompletableFuture future = new CompletableFuture<String>();
        mqttConfig.add_future(future);

        log.debug("HouseMachineService - read_user_set_status : Map에 future 추가 완료");

        // house_id로 device_id 가져오기
        String device_id = houseMapper.read_device_id(house_id);

        // MQTT 메시지 발행
        MqttPublisherDTO mqttPublisherDTO = new MqttPublisherDTO();
        mqttPublisherDTO.setTopic("core/topic/tolocal/" + device_id + "/" + user_set_kind);
        mqttPublisherDTO.setMsg("status");
        mqttPublisherDTO.setRequest_id(mqttConfig.return_count());

        mqttPublisher.sendMessage(mqttPublisherDTO);

        log.debug("HouseStatusService - read_user_set_status : MQTT 메시지 발행 후 결과 대기 중");

        try {
            // CompletableFuture 가 complete된 후 결과값을 Double로 변환 후 반환
            double data = Double.parseDouble(future.get(10, TimeUnit.SECONDS).toString());

            log.debug("HouseStatusService - read_user_set_status : 성공적으로 {}를 전달받았습니다.", data);

            MachineSetDTO result = new MachineSetDTO();
            result.setValue(data);
            
            return Optional.of(result);

        } catch (InterruptedException e) {
            log.error("HouseStatusService - read_user_set_status : InterruptedException 에러 발생");
            return Optional.empty();
        } catch (ExecutionException e) {
            log.error("HouseStatusService - read_user_set_status : ExecutionException 에러 발생");
            return Optional.empty();
        } catch (TimeoutException e) {
            log.error("HouseStatusService - read_user_set_status : TimeoutException 에러 발생");
            return Optional.empty();
        }

    }
    public void update_user_setting (String user_set_kind, int house_id, MachineSetDTO set_value){

        // CompletableFuture 생성 및 Map에 저장
        CompletableFuture future = new CompletableFuture<String>();
        mqttConfig.add_future(future);

        log.debug("HouseMachineService - update_user_setting : Map에 future 추가 완료");

        // house_id로 device_id 가져오기
        String device_id = houseMapper.read_device_id(house_id);

        // MQTT 메시지 발행
        MqttPublisherDTO mqttPublisherDTO = new MqttPublisherDTO();
        mqttPublisherDTO.setTopic("core/topic/tolocal/" + device_id + "/" + user_set_kind);
        mqttPublisherDTO.setMsg("operate");
        mqttPublisherDTO.setDou_value(set_value.getValue());
        mqttPublisherDTO.setRequest_id(mqttConfig.return_count());

        mqttPublisher.sendMessage(mqttPublisherDTO);

        log.debug("HouseStatusService - update_user_setting : MQTT 메시지 발행 후 결과 대기 중");

        try {
            // CompletableFuture 가 complete된 후 결과값을 Double로 변환 후 반환
            double data = Double.parseDouble(future.get(10, TimeUnit.SECONDS).toString());

            log.debug("HouseStatusService - update_user_setting : 성공적으로 {}를 전달받았습니다.", data);

        } catch (InterruptedException e) {
            log.error("HouseStatusService - update_user_setting : InterruptedException 에러 발생");
        } catch (ExecutionException e) {
            log.error("HouseStatusService - update_user_setting : ExecutionException 에러 발생");
        } catch (TimeoutException e) {
            log.error("HouseStatusService - update_user_setting : TimeoutException 에러 발생");
        }

    }
}
