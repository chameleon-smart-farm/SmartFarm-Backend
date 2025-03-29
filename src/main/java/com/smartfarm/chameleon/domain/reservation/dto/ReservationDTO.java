package com.smartfarm.chameleon.domain.reservation.dto;

import lombok.Data;

@Data
public class ReservationDTO {

    // 예약 번호
    private int reservation_id;
    
    // 예약 시
    private String reservation_hour;
    
    // 예약 분
    private String reservation_min;

    // 예약 요일
    private int reservation_day;
    
}
