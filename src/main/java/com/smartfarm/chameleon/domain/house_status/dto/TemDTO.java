package com.smartfarm.chameleon.domain.house_status.dto;

import java.util.List;

import lombok.Data;

@Data
public class TemDTO {

    // 온도 아이디
    private int tem_id;

    // 온도 측정 일시
    private String tem_day_time;

    // 온도 데이터
    private int tem_data;

    // 기상청의 온도 데이터
    private int weather_tem;

    // 3시간 평균 온도 리스트
    private List<TemAvgDTO> avg_list;
}
