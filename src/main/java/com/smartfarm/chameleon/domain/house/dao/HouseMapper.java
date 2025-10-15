package com.smartfarm.chameleon.domain.house.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.smartfarm.chameleon.domain.house.dto.HouseInfoDTO;
import com.smartfarm.chameleon.domain.house.dto.UserHouseDTO;

@Mapper
public interface HouseMapper {

    // 사용자 pk로 농장 아이디, 농장 백엔드 주소 받아오기
    public List<UserHouseDTO> read_back_url_list(int id);

    // 사용자 pk로 사용자가 보유한 농장 이름 리스트 반환
    public List<HouseInfoDTO> read_house_name_list(int id);

    // 농장 아이디로 농장 이름 변경
    public void update_house_name(HouseInfoDTO houseInfoDto);

    // 농장 아이디로 농장의 백엔드 주소 가져오기
    public String read_back_url(int house_id);

}
