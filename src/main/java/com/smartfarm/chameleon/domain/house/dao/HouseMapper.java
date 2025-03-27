package com.smartfarm.chameleon.domain.house.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.smartfarm.chameleon.domain.house.dto.UserHouseDTO;

@Mapper
public interface HouseMapper {
    
    // 사용자 아이디로 사용자 index id 받아오기
    public int read_index(String user_id);

    // 사용자 index id로 농장 아이디, 농장 백엔드 주소 받아오기
    public List<UserHouseDTO> read_back_url(int id);

}
