package com.one.flightontime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FlightontimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightontimeApplication.class, args);
	}

}
