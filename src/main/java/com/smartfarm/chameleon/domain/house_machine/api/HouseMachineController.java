package com.smartfarm.chameleon.domain.house_machine.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.house_machine.application.HouseMachineService;
import com.smartfarm.chameleon.domain.house_machine.dto.MachineSetDTO;
import com.smartfarm.chameleon.domain.house_machine.dto.MachineStatusDTO;

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
    public ResponseEntity<MachineStatusDTO> get_motor_status(@PathVariable("house_id") int house_id) {
        log.info("HouseMachineController : 모터 상태 반환 API");
        return new ResponseEntity<>(houseMachineService.get_motor_status(house_id), HttpStatus.OK);
    }

    @PostMapping("/motor/on_off/{house_id}")
    @Operation(summary = "모터 동작", description = "status 값에 따라 모터를 ON/OFF 하는 API")
    public void motor_on_off(@PathVariable("house_id") int house_id, @RequestBody MachineStatusDTO status) {
        log.info("HouseMachineController : 모터 동작 API");
        houseMachineService.motor_on_off(house_id, status);
    }

    @GetMapping("/{machine_kind}/status/{house_id}")
    @Operation(summary = "기기 상태 반환", description = "기기 상태를 반환하는 API")
    public ResponseEntity<MachineStatusDTO> get_machine_status(@PathVariable String machine_kind, @PathVariable int house_id) {
        log.info("HouseMachineController : 기기 상태 반환 API");
        return new ResponseEntity<>(houseMachineService.read_machine_status(machine_kind, house_id).get(), HttpStatus.OK);
    }

    @PostMapping("/{machine_kind}/operate/{house_id}")
    @Operation(summary = "기기 동작 변경", description = "전달된 boolean 값에 따라 기기를 ON/OFF 하는 API")
    public void machine_on_off(@PathVariable String machine_kind, @PathVariable int house_id, @RequestBody MachineStatusDTO status) {
        log.info("HouseMachineController : 기기 동작 API");
        houseMachineService.machine_on_off(machine_kind, house_id, status);
    }

    @GetMapping("/{user_set_kind}/user_status/{house_id}")
    @Operation(summary = "사용자 세팅 반환", description = "사용자 세팅을 반환하는 API")
    public ResponseEntity<MachineSetDTO> get_user_set_status (@PathVariable String user_set_kind, @PathVariable int house_id) {
        log.info("HouseMachineController : 사용자 세팅 반환 API");
        return new ResponseEntity<>(houseMachineService.read_user_set_status(user_set_kind, house_id).get(), HttpStatus.OK);
    }
    
    @PostMapping("/{user_set_kind}/user_operate/{house_id}")
    @Operation(summary = "사용자 세팅 변경", description = "전달된 double 값에 따라 사용자 세팅을 변경하는 API")
    public void update_user_setting (@PathVariable String user_set_kind, @PathVariable int house_id, @RequestBody MachineSetDTO set_value) {
        log.info("HouseMachineController : 기기 동작 API");
        houseMachineService.update_user_setting(user_set_kind, house_id, set_value);
    }
    
}
