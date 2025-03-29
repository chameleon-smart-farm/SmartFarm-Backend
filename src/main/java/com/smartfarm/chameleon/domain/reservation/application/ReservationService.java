package com.smartfarm.chameleon.domain.reservation.application;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartfarm.chameleon.domain.house.dao.HouseMapper;
import com.smartfarm.chameleon.domain.reservation.dto.ReservationDTO;
import com.smartfarm.chameleon.global.toHouse.HttpHouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationService {

    @Autowired
    private HouseMapper houseMapper;


    @Autowired
    private HttpHouse httpHouse;

    // 예약 정보 리스트 조회
    public List<ReservationDTO> read_reservation_list(int house_id){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String get_url = houseMapper.read_back_url(house_id) + "/reservation/list";

        // get 요청
        JSONArray res_result = (JSONArray) httpHouse.get_http_connection(get_url).get();

        // 결과를 담을 List
        List<ReservationDTO> result = new ArrayList<>();

        // List 처리
        for(Object o : res_result){

            // List 안의 요소를 JsonObject로 변환
            JSONObject jsonObject = (JSONObject) o;

            ReservationDTO reservationDTO = new ReservationDTO();
            reservationDTO.setReservation_id(Integer.parseInt(jsonObject.get("reservation_id").toString()));
            reservationDTO.setReservation_hour(jsonObject.get("reservation_hour").toString());
            reservationDTO.setReservation_min(jsonObject.get("reservation_min").toString());
            reservationDTO.setReservation_day(Integer.parseInt(jsonObject.get("reservation_day").toString()));
        
            result.add(reservationDTO);
        }

        // 결과 출력
        for(ReservationDTO r : result){
            log.info(r.toString());
        }

        return result;

    }

    // 예약 정보 단일 조회
    public ReservationDTO read_reservation(int house_id, int reservation_id){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String get_url = houseMapper.read_back_url(house_id) + "/reservation/" + reservation_id;

        // get 요청
        JSONObject res_result = (JSONObject) httpHouse.get_http_connection(get_url).get();
       
        // 결과 생성
        ReservationDTO result = new ReservationDTO();
        result.setReservation_id(Integer.parseInt(res_result.get("reservation_id").toString()));
        result.setReservation_hour(res_result.get("reservation_hour").toString());
        result.setReservation_min(res_result.get("reservation_min").toString());
        result.setReservation_day(Integer.parseInt(res_result.get("reservation_day").toString()));
    
        return result;
    }

    // 예약 정보 DB에 저장
    public void create_reservation(int house_id, ReservationDTO reservationDTO){
        
        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String post_url = houseMapper.read_back_url(house_id) + "/reservation/insert"; 

        // post 요청
        httpHouse.post_http_connection(post_url, reservationDTO);

    }

    // 예약 정보 업데이트
    public void update_reservation(int house_id, ReservationDTO reservationDTO){
        
        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String put_url = houseMapper.read_back_url(house_id) + "/reservation/update"; 

        // post 요청
        httpHouse.put_http_connection(put_url, reservationDTO);

    }

    // 예약 정보 삭제
    public void delete_reservation(int house_id, int reservation_id){

        // 농장 아이디로 농장의 백엔드 주소 가져오기
        String del_url = houseMapper.read_back_url(house_id) + "/reservation/delete/" + reservation_id;

        // del 요청
        httpHouse.delete_http_connection(del_url);
        
    }
    
}
