package com.smartfarm.chameleon.domain.house_status.dto;

import lombok.Data;

@Data
public class TemAvgDTO {

    // 평균 온도 테이블 id
    private int tem_avg_id;

    // 평균 온도 마지막 측정시간
    private String tem_avg_fin_time;

    // 평균 온도 데이터
    private int tem_avg_data;

    // 외부 평균 데이터 - TBD
    private int tem_outside;
    
}
