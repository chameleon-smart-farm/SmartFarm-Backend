package com.smartfarm.chameleon.domain.house_machine.dto;

import lombok.Data;

@Data
public class MachineListDTO {
    
    private int     user_device_id;
    private String  user_device_name;
    private String  device_type_short_name;
    private int     user_device_activate;
}
