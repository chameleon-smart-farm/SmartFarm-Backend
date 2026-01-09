package com.smartfarm.chameleon.domain.geocoding.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.geocoding.application.GeocodingService;
import com.smartfarm.chameleon.domain.geocoding.dto.CoordinateDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@RequestMapping("/geocoding")
@Tag(name = "geocoding API", description = "스마트팜 주소를 위도, 경도로 변환")
public class GeocodingController {

    @Autowired
    private GeocodingService geocodingService;

    @GetMapping("/convert")
    @Operation(summary = "주소를 위도, 경도로 변환", description = "query로 전달된 주소를 지번 주소를 기준으로 위도, 경도로 변환해서 전달")
    public ResponseEntity<CoordinateDTO> convertAddressToCoordinate(@RequestParam("query") String query) {
        
        log.info("GeocodingController : convertAddressToCoordinate");

        return new ResponseEntity<>(geocodingService.convertAddressToCoordinate(query), HttpStatus.OK);

        
    }
    
    
}
