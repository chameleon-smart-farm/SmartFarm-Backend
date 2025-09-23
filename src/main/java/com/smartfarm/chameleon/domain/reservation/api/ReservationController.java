package com.smartfarm.chameleon.domain.reservation.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfarm.chameleon.domain.reservation.application.ReservationService;
import com.smartfarm.chameleon.domain.reservation.dto.ReservationDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/reservation")
@Tag(name = "농장 예약 API", description = "농장 기기 예약 CRUD")
@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // 예약 정보 리스트 조회
    @GetMapping("/list/{house_id}")
    @Operation(summary = "예약 정보 리스트 조회" , description = "예약 목록을 조회하는 API")
    public ResponseEntity<List<ReservationDTO>> read_reservation_list(@RequestHeader("Authorization") String access_token, @PathVariable("house_id") int house_id){
        log.info("Reservation Controller : 예약 정보 리스트 조회");
        
        return new ResponseEntity<>(reservationService.read_reservation_list(house_id), HttpStatus.OK);

    }
    
    //예약 정보 단일 조회
    @GetMapping("/{house_id}/{reservation_id}")
    @Operation(summary = "예약 정보 단일 조회" , description = "단일 예약을 조회하는 API")
    public ResponseEntity<ReservationDTO> read_reservation(@RequestHeader("Authorization") String access_token, @PathVariable("house_id") int house_id, @PathVariable("reservation_id") Integer reservation_id){
        log.info("Reservation Controller : 예약 정보 단일 조회");
        return new ResponseEntity<>(reservationService.read_reservation(house_id, reservation_id), HttpStatus.OK);
    }

    // 예약 정보 DB에 저장
    @PostMapping("/insert/{house_id}")
    @Operation(summary = "예약 정보 입력" , description = "예약 정보를 DB에 저장하는 API")
    public void create_reservation(@RequestHeader("Authorization") String access_token, @PathVariable("house_id") int house_id, @RequestBody ReservationDTO reservationDTO){
        log.info("Reservation Controller : 예약 정보 DB에 저장");
        reservationService.create_reservation(house_id, reservationDTO);
    }
    
    // 예약 정보 업데이트
    @PutMapping("/update/{house_id}")
    @Operation(summary = "예약 정보 수정" , description = "예약 정보를 수정하는 API")
    public void update_reservation( @RequestHeader("Authorization") String access_token, @PathVariable("house_id") int house_id, @RequestBody ReservationDTO reservationDTO){
        log.info("Reservation Controller : 예약 정보 업데이트");
        reservationService.update_reservation(house_id, reservationDTO);
    }
    
    // 예약 정보 삭제
    @DeleteMapping("/delete/{house_id}/{reservation_id}")
    @Operation(summary = "예약 정보 삭제" , description = "예약 정보를 삭제하는 API")
    public void delete_reservation( @RequestHeader("Authorization") String access_token, @PathVariable("house_id") int house_id, @PathVariable("reservation_id") int reservation_id){
        log.info("Reservation Controller : 예약 정보 삭제");
        reservationService.delete_reservation(house_id, reservation_id);
    }
    
}
