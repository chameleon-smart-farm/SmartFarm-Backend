package com.smartfarm.chameleon.domain.house_machine.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.house_machine.application.HouseMachineService;
import com.smartfarm.chameleon.domain.house_machine.dto.HouseMachineDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@Slf4j
@Tag(name = "농장 기기 API", description = "농장의 각종 기기 데이터 상태 조회와 동작 관리")
@RestController
@RequestMapping("/house_machine")
public class HouseMachineController {

    @Autowired
    private HouseMachineService houseMachineService;

    @GetMapping("/motor/status/{house_id}")
    @Operation(summary = "모터 상태 반환", description = "모터 상태를 반환하는 API")
    public ResponseEntity<HouseMachineDTO> get_motor_status(@PathVariable("house_id") int house_id) {
        log.info("HouseMachineController : 모터 상태 반환 API");
        return new ResponseEntity<>(houseMachineService.get_motor_status(house_id), HttpStatus.OK);
    }
    

    @PostMapping("/motor/on_off/{house_id}")
    @Operation(summary = "모터 동작", description = "status 값에 따라 모터를 ON/OFF 하는 API")
    public void motor_on_off(@PathVariable("house_id") int house_id, @RequestBody HouseMachineDTO status) {
        log.info("HouseMachineController : 모터 동작 API");
        houseMachineService.motor_on_off(house_id, status);
    }
    
    
}
