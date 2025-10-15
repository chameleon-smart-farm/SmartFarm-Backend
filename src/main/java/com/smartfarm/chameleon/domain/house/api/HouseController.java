package com.smartfarm.chameleon.domain.house.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.house.application.HouseService;
import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Slf4j
@RequestMapping("/house")
@Tag(name = "농장 정보 API", description = "농장 서버에 저장되어 있는 농장 정보 조회")
@RestController
public class HouseController {
    
    @Autowired
    private HouseService houseService;

    @GetMapping("/info")
    @Operation(summary = "농장 정보 조회" , description = "사용자가 보유한 모든 농장의 농장 아이디, 농장 이름, 키우는 작물, 주소가 저장된 List를 반환하는 API")
    public ResponseEntity<List<HouseInfoDTO>> read_house(@AuthenticationPrincipal(expression = "PK") int USER_PK) {
        log.info("HouseController : 농장 정보 조회 API ");
        return new ResponseEntity<>(houseService.read_house(USER_PK), HttpStatus.OK);
    }

    @GetMapping("/name_list")
    @Operation(summary = "농장 이름 리스트 반환" , description = "농장 이름 리스트를 반환하는 API")
    public ResponseEntity<List<HouseInfoDTO>> read_house_name_list(@AuthenticationPrincipal(expression = "PK") int USER_PK) {
        log.info("HouseController : 농장 이름 리스트 반환 API ");
        return new ResponseEntity<>(houseService.read_house_name_list(USER_PK), HttpStatus.OK);
    }
    
    @PutMapping("/update")
    @Operation(summary = "농장 정보 수정" , description = "농장 아이디로 농장 이름과 키우는 작물 수정하는 API")
    public void update_house_name(@RequestBody HouseInfoDTO houseInfoDto ) {
        log.info("HouseController : 농장 정보 수정 API ");
        houseService.update_house_name(houseInfoDto);
    }

}
