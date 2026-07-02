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
import com.smartfarm.chameleon.domain.house_status.dto.StatusDTO;
import com.smartfarm.chameleon.domain.house_status.dto.TemDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



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
    public ResponseEntity<HouseWeatherDTO> get_weather_info(@PathVariable("house_id") int house_id) {
        
        log.info("UserController : 농장 기상청 데이터 반환 API");

        // 현재 시각을 hh 형식으로 변환
        LocalDateTime now = LocalDateTime.now();
        String cur_time = now.format(DateTimeFormatter.ofPattern("HH"));

        return new ResponseEntity<>(houseStatusService.read_weather_info(house_id, cur_time), HttpStatus.OK);
    }

    @GetMapping("/get_in_tem_info")
    @Operation(summary = "(MQTT) 농장 내부 온도 데이터 조회" , description = "OPC UA에서 PLC의 내부 온도 데이터를 MQTT 메시지로 전달 받는 API")
    public ResponseEntity<TemDTO> get_in_tem() {

        log.debug("HouseStatusController : MQTT 테스트 API");

        return new ResponseEntity<>(houseStatusService.read_in_tem().get(), HttpStatus.OK);
    }

    @GetMapping("/{sensor_kind}/get_double_sensor_info/{house_id}")
    @Operation(summary = "농장 센서 데이터 조회" , description = "각 센서의 데이터를 double 형태로 반환받는 API")
    public ResponseEntity<StatusDTO> get_double_sensor_info(@PathVariable String sensor_kind, @PathVariable int house_id) {

        log.debug("HouseStatusController : MQTT 농장 센서 데이터 조회");

        return new ResponseEntity<>(houseStatusService.read_double_house_status(sensor_kind, house_id).get(), HttpStatus.OK);
    }

    @GetMapping("/get_system_mode_info/{house_id}")
    @Operation(summary = "시스템(제어) 모드 조회" , description = "시스템(제어) 모드를 string 형태로 반환받는 API")
    public ResponseEntity<StatusDTO> get_string_sensor_info(@PathVariable int house_id) {

        log.debug("HouseStatusController : MQTT 농장 시스템(제어) 모드 조회");

        return new ResponseEntity<>(houseStatusService.read_system_mode_status(house_id).get(), HttpStatus.OK);
    }


    @PostMapping("/mqtt_test")
    public void mqtt_test (@RequestBody String test) {
        
        houseStatusService.mqtt_test(test);
        
    }
    
    
}
