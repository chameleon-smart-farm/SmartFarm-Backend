package com.smartfarm.chameleon.domain.house.api;

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

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/house")
@RestController
public class HouseController {
    
    @Autowired
    private HouseService houseService;

    @GetMapping("/info")
    @Operation(summary = "농장 정보 조회" , description = "키우는 작물, 주소를 조회하는 API")
    public ResponseEntity<List<HouseInfoDTO>> read_house(@RequestHeader("Authorization") String access_token) {
        return new ResponseEntity<>(houseService.read_house(access_token.substring(7)), HttpStatus.OK);
    }

}
