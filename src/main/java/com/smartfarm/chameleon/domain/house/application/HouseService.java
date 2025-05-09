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
import com.smartfarm.chameleon.domain.house.dto.HouseWeatherDTO;
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

        // 사용자 index id로 농장 아이디, 농장 이름, 농장 백엔드 주소 받아오기
        List<UserHouseDTO> url_list = houseMapper.read_back_url_list(id);

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

    // 사용자 index id로 사용자가 보유한 농장 이름 리스트 반환
    public List<HouseInfoDTO> read_house_name_list(String access_token){

        // 사용자 아이디
        String user_id = jwtTokenProvider.getUserID(access_token);
        // 사용자 index id 받아오기
        int id = houseMapper.read_index(user_id);

        return houseMapper.read_house_name_list(id);
    }

    // 농장 아이디로 농장 이름과 키우는 작물 수정
    @Transactional
    public void update_house_name(HouseInfoDTO houseInfoDto){

        // 농장 아이디로 농장 이름 변경
        houseMapper.update_house_name(houseInfoDto);

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String put_url = houseMapper.read_back_url(houseInfoDto.getHouse_id()) + "/house/update";

        // put 요청
        httpHouse.put_http_connection(put_url, houseInfoDto);

    }

    // 농장 아이디로 농장의 기상청 데이터 온도, 습도, 풍속, 하늘 상태, 강수 상태 정보를 받아옴
    @Cacheable(value = "weather", key = "#p0")
    public HouseWeatherDTO read_weather_info(int house_id){

        // delete_cache();

        log.info("read_weather_info 메서드 실행");

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String get_url = houseMapper.read_back_url(house_id) + "/get_weather_info";

        // get 요청
        JSONObject weather_result = (JSONObject) httpHouse.get_http_connection(get_url).get();

        log.info(weather_result.toJSONString());

        HouseWeatherDTO houseWeatherDTO = new HouseWeatherDTO();
        houseWeatherDTO.setWeather_hum(weather_result.get("weatherHum").toString());
        houseWeatherDTO.setWeather_status(weather_result.get("weatherStatus").toString());
        houseWeatherDTO.setWeather_preci(weather_result.get("weatherPreci").toString());
        houseWeatherDTO.setWeather_tem(weather_result.get("weatherTem").toString());
        houseWeatherDTO.setWeather_wind(weather_result.get("weatherWind").toString());

        return houseWeatherDTO;
    }

    // 기상청 데이터의 경우 시간이 지나면 기존 데이터는 쓸모가 없으므로
    // 새로운 캐시가 저장될 경우 기존 캐시를 지운다.
    @CacheEvict(value = "weather", allEntries = true )
    public void delete_cache(){};

}
