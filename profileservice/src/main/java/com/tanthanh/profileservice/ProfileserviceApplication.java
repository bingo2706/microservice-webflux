package com.tanthanh.profileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
@ComponentScan({"com.tanthanh.profileservice", "com.tanthanh.commonservice"})
public class ProfileserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfileserviceApplication.class, args);
	}

}
