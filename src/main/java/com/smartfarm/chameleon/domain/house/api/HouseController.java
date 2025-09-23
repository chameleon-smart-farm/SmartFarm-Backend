package com.smartfarm.chameleon.domain.house.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.house.application.HouseService;
import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;
import com.smartfarm.chameleon.domain.house.dto.HouseWeatherDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@Slf4j
@RequestMapping("/house")
@Tag(name = "농장 정보 API", description = "농장 서버에 저장되어 있는 농장 정보 조회")
@RestController
public class HouseController {
    
    @Autowired
    private HouseService houseService;

    @GetMapping("/info")
    @Operation(summary = "농장 정보 조회" , description = "키우는 작물, 주소를 조회하는 API")
    public ResponseEntity<List<HouseInfoDTO>> read_house(@RequestHeader("Authorization") String access_token) {
        return new ResponseEntity<>(houseService.read_house(access_token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/name_list")
    @Operation(summary = "농장 이름 리스트 반환" , description = "농장 이름 리스트를 반환하는 API")
    public ResponseEntity<List<HouseInfoDTO>> read_house_name_list(@RequestHeader("Authorization") String access_token) {
        return new ResponseEntity<>(houseService.read_house_name_list(access_token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/get_weather_info/{house_id}")
    @Operation(summary = "농장 기상청 데이터 반환" , description = "온도, 습도, 풍속, 하늘 상태, 강수 상태 정보를 반환하는 API")
    public ResponseEntity<HouseWeatherDTO> read_weather_info(@RequestHeader("Authorization") String access_token, @PathVariable("house_id") int house_id) {
        
        // 현재 시각을 hh 형식으로 변환
        LocalDateTime now = LocalDateTime.now();
        String cur_time = now.format(DateTimeFormatter.ofPattern("HH"));

        return new ResponseEntity<>(houseService.read_weather_info(house_id, cur_time), HttpStatus.OK);
    }
    
    @PutMapping("/update")
    @Operation(summary = "농장 정보 수정" , description = "농장 아이디로 농장 이름과 키우는 작물 수정하는 API")
    public void update_house_name(@RequestHeader("Authorization") String access_token, @RequestBody HouseInfoDTO houseInfoDto ) {
        houseService.update_house_name(houseInfoDto );
    }

}
