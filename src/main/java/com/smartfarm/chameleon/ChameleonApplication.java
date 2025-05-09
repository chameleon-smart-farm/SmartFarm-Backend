package com.smartfarm.chameleon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan("com.smartfarm.chameleon.domain.*.dao")
public class ChameleonApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChameleonApplication.class, args);
	}

}
