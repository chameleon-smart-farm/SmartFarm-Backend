package com.smartfarm.chameleon.domain.house.application;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartfarm.chameleon.domain.house.dao.HouseMapper;
import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;
import com.smartfarm.chameleon.domain.house.dto.UserHouseDTO;
import com.smartfarm.chameleon.global.toHouse.HttpHouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HttpHouse httpHouse;
    
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

        // 사용자 pk로 농장 아이디, 농장 이름, 농장 백엔드 주소가 담긴 List 받아오기
        List<UserHouseDTO> url_list = houseMapper.read_back_url_list(user_pk);

        // 결과를 담을 List
        List<HouseInfoDTO> result = new ArrayList<>();

        // 백엔드 요청
        for(UserHouseDTO h : url_list){
            String get_url = h.getHouse_back_url() + "/house/info";

            // get 요청
            JSONObject house_result = (JSONObject) httpHouse.get_http_connection(get_url).get();

            // 결과 생성
            HouseInfoDTO info_result = new HouseInfoDTO();
            info_result.setHouse_id(h.getHouse_id());
            info_result.setHouse_name(h.getHouse_name());
            info_result.setHouse_crop(house_result.get("house_crop").toString());
            info_result.setHouse_add(house_result.get("house_add").toString());

            log.info("HouseService : 농장 정보 조회 서비스 결과 : " + info_result.toString());

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
    @CacheEvict(value = {"read_house_name_list", "read_house_info"})
    @Transactional
    public void update_house_name(HouseInfoDTO houseInfoDto){

        // 농장 아이디로 농장 이름 변경
        houseMapper.update_house_name(houseInfoDto);

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String put_url = houseMapper.read_back_url(houseInfoDto.getHouse_id()) + "/house/update";

        // put 요청 - 키우는 작물 수정
        httpHouse.put_http_connection(put_url, houseInfoDto);

    }

}
