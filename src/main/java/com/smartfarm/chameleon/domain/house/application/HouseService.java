package com.smartfarm.chameleon.domain.house.application;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.house.dao.HouseMapper;
import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;
import com.smartfarm.chameleon.domain.house.dto.UserHouseDTO;
import com.smartfarm.chameleon.global.jwt.JwtTokenProvider;
import com.smartfarm.chameleon.global.toHouse.HttpHouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private HttpHouse httpHouse;
    
    // 농장 정보 조회
    public List<HouseInfoDTO> read_house(String access_token){

        // 사용자 아이디
        String user_id = jwtTokenProvider.getUserID(access_token);
        // 사용자 index id 받아오기
        int id = houseMapper.read_index(user_id);

        // 사용자 index id로 농장 아이디, 농장 백엔드 주소 받아오기
        List<UserHouseDTO> url_list = houseMapper.read_back_url(id);

        // 결과를 담을 List
        List<HouseInfoDTO> result = new ArrayList<>();

        // 백엔드 요청
        for(UserHouseDTO h : url_list){
            String get_url = h.getHouse_back_url() + "/house/info";

            // get 요청
            JSONObject house_result = httpHouse.get_http_connection(get_url).get();

            // 결과 생성
            HouseInfoDTO info_result = new HouseInfoDTO();
            info_result.setHouse_id(h.getHouse_id());
            info_result.setHouse_name(h.getHouse_name());
            info_result.setHouse_crop(house_result.get("house_crop").toString());
            info_result.setHouse_add(house_result.get("house_add").toString());

            log.info("농장 정보 조회 서비스 결과 : " + info_result.toString());

            // 결과 리스트에 객체 추가
            result.add(info_result);
            
        }

        // 결과 출력
        for(HouseInfoDTO h : result){
            log.info(h.toString());
        }

        return result;
        
    }
}
