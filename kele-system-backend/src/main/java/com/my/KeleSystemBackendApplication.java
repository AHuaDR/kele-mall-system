package com.my;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.my.mapper")
@SpringBootApplication
public class KeleSystemBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeleSystemBackendApplication.class, args);
	}

}
