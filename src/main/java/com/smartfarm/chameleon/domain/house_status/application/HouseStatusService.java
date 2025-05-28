package com.smartfarm.chameleon.domain.house_status.application;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.house.dao.HouseMapper;
import com.smartfarm.chameleon.domain.house_status.dto.TemAvgDTO;
import com.smartfarm.chameleon.domain.house_status.dto.TemDTO;
import com.smartfarm.chameleon.global.toHouse.HttpHouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HouseStatusService {

    @Autowired
    private HouseMapper houseMapper;
    
    @Autowired
    private HttpHouse httpHouse;

    // 가장 최근에 저장된 온도 데이터와 기상청의 데이터
    // 가장 최근 3시간의 평균 온도 데이터 리스트를 함께 반환
    @Cacheable(value = "get_tem_data")
    public TemDTO get_tem_data(int house_id){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String get_url = houseMapper.read_back_url(house_id) + "/house_status/tem_info";

        // get 요청
        JSONObject tem_result = (JSONObject) httpHouse.get_http_connection(get_url).get();
        
        // 반환할 3시간의 평균 온도 데이터 리스트를 담을 List
        List<TemAvgDTO> list = new ArrayList<>();

        // 결괏값에서 List 가져오기
        JSONArray tem_list = (JSONArray) tem_result.get("avg_list");

        // List 처리
        for(Object o : tem_list){

            // List 안의 요소를 JsonObject로 변환
            JSONObject jsonObject = (JSONObject) o;

            TemAvgDTO temAvgDTO = new TemAvgDTO();
            temAvgDTO.setTem_avg_id(Integer.parseInt(jsonObject.get("tem_avg_id").toString()));
            temAvgDTO.setTem_avg_fin_time(jsonObject.get("tem_avg_fin_time").toString());
            temAvgDTO.setTem_avg_data(Integer.parseInt(jsonObject.get("tem_avg_data").toString()));

            list.add(temAvgDTO);
        }

        // 결과 생성
        TemDTO result = new TemDTO();
        result.setTem_id(Integer.parseInt(tem_result.get("tem_id").toString()));
        result.setTem_day_time(tem_result.get("tem_day_time").toString());
        result.setTem_data(Integer.parseInt(tem_result.get("tem_data").toString()));
        result.setWeather_tem(Integer.parseInt(tem_result.get("weather_tem").toString()));
        result.setAvg_list(list);

        // 결과 출력
        log.info("결과 출력 : " + result.toString());

        return result;

    }
}
