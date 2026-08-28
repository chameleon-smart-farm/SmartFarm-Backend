package com.smartfarm.chameleon.domain.house.application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartfarm.chameleon.domain.house.dao.HouseMapper;
import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;
import com.smartfarm.chameleon.domain.house.dto.UserHouseDTO;
import com.smartfarm.chameleon.domain.mqtt.application.MqttPublisher;
import com.smartfarm.chameleon.domain.mqtt.dto.MqttPublisherDTO;
import com.smartfarm.chameleon.global.config.MQTTConfig;
import com.smartfarm.chameleon.global.toHouse.HttpHouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HttpHouse httpHouse;

    @Autowired
    private MQTTConfig mqttConfig;

    @Autowired
    private MqttPublisher mqttPublisher;

    private final JSONParser parser = new JSONParser();
    
    /**
     * 농장 정보(농장 아이디, 농장 이름, 키우는 작물, 주소) 조회
     * 사용자 pk로 농장 아이디, 농장 이름, 농장 백엔드 주소를 받아온 후
     * 농장 서버에 요청을 보낸다.
     * 
     * @param user_pk
     * @return : 농장 정보(농장 아이디, 농장 이름, 키우는 작물, 주소)가 저장된 List 반환
     */
    @Cacheable(value = "read_house_info")
    public List<HouseInfoDTO> read_house(int user_pk){

        // 사용자 pk로 농장 아이디, 농장 이름, 농장 디바이스 아이디가 담긴 List 받아오기
        List<UserHouseDTO> url_list = houseMapper.read_house_list(user_pk);

        // 결과를 담을 List
        List<HouseInfoDTO> result = new ArrayList<>();

        // 백엔드 요청
        for(UserHouseDTO h : url_list){

            // 결과 생성 1 - 농장 아이디, 농장 이름 저장
            HouseInfoDTO info_result = new HouseInfoDTO();
            info_result.setHouse_id(h.getHouse_id());
            info_result.setHouse_name(h.getHouse_name());
            info_result.setHouse_crop(new String());
            info_result.setHouse_add(new String());

            // CompletableFuture 생성 및 Map에 저장
            CompletableFuture<String> future = new CompletableFuture<String>();
            mqttConfig.add_future(future);

            log.debug("HouseService - read_house : Map에 future 추가 완료");

            // device_id 추출
            String device_id = h.getDevice_id();

            MqttPublisherDTO mqttPublisherDTO = new MqttPublisherDTO();
            mqttPublisherDTO.setTopic("core/topic/tolocal/" + device_id + "/house_info_list");
            mqttPublisherDTO.setMsg("house_info_list");
            mqttPublisherDTO.setRequest_id(mqttConfig.return_count());

            mqttPublisher.sendMessage(mqttPublisherDTO);

            log.debug("HouseService - read_house : MQTT 메시지 발행 후 결과 대기 중");
            
            try {
                // CompletableFuture 가 complete된 후 결과값을 Double로 변환 후 반환
                String data = future.get(10, TimeUnit.SECONDS);

                log.debug("HouseService - read_house : 성공적으로 {}를 전달받았습니다.", data);

                JSONObject json_result = (JSONObject) parser.parse(data);

                // 결과 생성 2 - 농장 작물, 농장 주소 저장
                info_result.setHouse_crop(json_result.get("house_crop").toString());
                info_result.setHouse_add(json_result.get("house_add").toString());

            } catch (InterruptedException e) {
                log.error("HouseService - read_house : InterruptedException 에러 발생");
            } catch (ExecutionException e) {
                log.error("HouseService - read_house : ExecutionException 에러 발생");
            } catch (TimeoutException e) {
                log.error("HouseService - read_house : TimeoutException 에러 발생");
            } catch (ParseException e) {
                log.error("HouseService - read_house : TimeoutException 에러 발생");
            }

            // 결과 리스트에 객체 추가
            result.add(info_result);
            
        }

        // 결과 출력
        for(HouseInfoDTO h : result){
            log.info(h.toString());
        }

        return result;
        
    }

    /**
     * 사용자 pk로 사용자가 보유한 농장 이름 리스트 반환
     * 
     * @param user_pk
     * @return : 농장 이름 List 반환
     */
    @Cacheable(value = "read_house_name_list")
    public List<HouseInfoDTO> read_house_name_list(int user_pk){

        return houseMapper.read_house_name_list(user_pk);
    }

    /**
     * 농장 아이디로 농장 이름과 키우는 작물 수정
     * 농장 이름 변경은 회사 서버에서 수행
     * 농장의 키우는 작물은 농장 서버에서 수행
     * 
     * @param houseInfoDto
     */
    @CacheEvict(value = {"read_house_name_list", "read_house_info"}, key="#p0")
    @Transactional
    public void update_house_name(int user_pk, HouseInfoDTO houseInfoDto){

        // 농장 아이디로 농장 이름 변경
        houseMapper.update_house_name(houseInfoDto);

        // CompletableFuture 생성 및 Map에 저장
        CompletableFuture<String> future = new CompletableFuture<String>();
        mqttConfig.add_future(future);

        log.debug("HouseService - update_house_name : Map에 future 추가 완료");

        // house_id로 device_id 가져오기
        String device_id = houseMapper.read_device_id(houseInfoDto.getHouse_id());

        // MQTT 메시지 발행
        MqttPublisherDTO mqttPublisherDTO = new MqttPublisherDTO();
        mqttPublisherDTO.setTopic("core/topic/tolocal/" + device_id + "/house_info_update");
        mqttPublisherDTO.setMsg("house_info_update");
        mqttPublisherDTO.setRequest_id(mqttConfig.return_count());
        
        mqttPublisher.sendDTO(mqttPublisherDTO, houseInfoDto);

        log.debug("HouseService - update_house_name : MQTT 메시지 발행 후 결과 대기 중");

        try {
            // CompletableFuture 가 complete된 후 결과값을 Double로 변환 후 반환
            String data = future.get(10, TimeUnit.SECONDS).toString();

            log.debug("HouseService - update_house_name : 성공적으로 {}를 전달받았습니다.", data);

        } catch (InterruptedException e) {
            log.error("HouseService - update_house_name : InterruptedException 에러 발생");
        } catch (ExecutionException e) {
            log.error("HouseService - update_house_name : ExecutionException 에러 발생");
        } catch (TimeoutException e) {
            log.error("HouseService - update_house_name : TimeoutException 에러 발생");
        }

    }

    /**
     *  사용자의 농장 추가
     *  - USER_PK와 HOUSE_ID 연결
     *  - update_house_name 호출 - 농장 아이디로 농장 이름과 키우는 작물 수정
     */
    @CacheEvict(value = {"read_house_name_list", "read_house_info"}, key="#p0")
    @Transactional
    public void add_house(int USER_PK, HouseInfoDTO houseInfoDto){

        // USER_PK와 HOSUE_ID DTO에 저장
        UserHouseDTO userHouseDTO = new UserHouseDTO();
        userHouseDTO.setId(Integer.toString(USER_PK));
        userHouseDTO.setHouse_id(houseInfoDto.getHouse_id());

        // USER_PK와 HOSUE_ID 연결
        houseMapper.add_house(userHouseDTO);

        // 농장 아이디로 농장 이름과 키우는 작물 수정
        update_house_name(USER_PK,houseInfoDto);

    }

}
