package com.smartfarm.chameleon.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private int id;
    private String user_name;
    private String user_id;
    private String user_pwd;
    private String faw_crop;
       
}
