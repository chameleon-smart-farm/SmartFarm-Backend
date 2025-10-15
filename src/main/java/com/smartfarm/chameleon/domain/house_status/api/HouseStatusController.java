package com.smartfarm.chameleon.domain.house_status.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.house_status.application.HouseStatusService;
import com.smartfarm.chameleon.domain.house_status.dto.HouseWeatherDTO;
import com.smartfarm.chameleon.domain.house_status.dto.TemDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "농장 상태 확인 API", description = "농장의 각종 상태 데이터 반환")
@RestController
@RequestMapping("/house_status")
public class HouseStatusController {

    @Autowired
    private HouseStatusService houseStatusService;

    @GetMapping("/tem_info/{house_id}")
    @Operation(summary = "농장 온도 데이터 조회", description = "농장의 가장 최근 측정 온도값과 기상청의 온도 데이터, 가장 최근 3시간의 평균 온도 데이터를 함께 반환")
    public ResponseEntity<TemDTO> get_tem_data(@PathVariable("house_id") int house_id) {
        log.info("UserController : 농장 온도 데이터 조회 API");
        return new ResponseEntity<>(houseStatusService.get_tem_data(house_id), HttpStatus.OK);
    }

    @GetMapping("/get_weather_info/{house_id}")
    @Operation(summary = "농장 기상청 데이터 반환" , description = "온도, 습도, 풍속, 하늘 상태, 강수 상태 정보를 반환하는 API")
    public ResponseEntity<HouseWeatherDTO> read_weather_info(@PathVariable("house_id") int house_id) {
        
        log.info("UserController : 농장 기상청 데이터 반환 API");

        // 현재 시각을 hh 형식으로 변환
        LocalDateTime now = LocalDateTime.now();
        String cur_time = now.format(DateTimeFormatter.ofPattern("HH"));

        return new ResponseEntity<>(houseStatusService.read_weather_info(house_id, cur_time), HttpStatus.OK);
    }
}
