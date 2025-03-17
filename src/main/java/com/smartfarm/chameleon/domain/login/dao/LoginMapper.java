package com.smartfarm.chameleon.domain.login.dao;

import org.apache.ibatis.annotations.Mapper;
import com.smartfarm.chameleon.domain.login.dto.UserDTO;

@Mapper
public interface LoginMapper {
    
    public UserDTO login (UserDTO userDTO);

}
