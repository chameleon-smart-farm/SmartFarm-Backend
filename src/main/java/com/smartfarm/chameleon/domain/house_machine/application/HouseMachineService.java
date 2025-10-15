package com.smartfarm.chameleon.domain.house_machine.application;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.house.dao.HouseMapper;
import com.smartfarm.chameleon.domain.house_machine.dto.HouseMachineDTO;
import com.smartfarm.chameleon.global.toHouse.HttpHouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HouseMachineService {

    @Autowired
    private HttpHouse httpHouse;

    @Autowired
    private HouseMapper houseMapper;

    /**
     * 농장 서버에 모터 상태를 조회 요청을 보내는 메서드
     * 
     * @param house_id
     * @return
     */
    public HouseMachineDTO get_motor_status(int house_id){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String get_url = houseMapper.read_back_url(house_id) + "/house_machine/motor/status";

        // get 요청 - 모터 상태
        JSONObject res_result = (JSONObject) httpHouse.get_http_connection(get_url).get();

        // 결과 생성
        HouseMachineDTO result = new HouseMachineDTO();
        result.setMotor_status(Boolean.parseBoolean(res_result.get("motor_status").toString()));

        // 결과 출력
        log.info("get_motor_status - 모터 상태 : " + result.isMotor_status());

        return result;
    }
    
    /**
     * 농장 서버에 모터 on/off 요청을 보내는 메서드
     * 
     * @param house_id
     * @param status
     */
    public void motor_on_off(int house_id, HouseMachineDTO status){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String post_url = houseMapper.read_back_url(house_id) + "/house_machine/motor/on_off";

        // post 요청 - 모터 on/off
        httpHouse.post_http_connection(post_url, status);

    }
}
