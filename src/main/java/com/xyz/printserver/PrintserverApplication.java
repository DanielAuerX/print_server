package com.xyz.printserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrintserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrintserverApplication.class, args);
	}

}
/*
todo:
+ login
+ check pictures
+ error page for too many requests
+ what happens after 200 and 400/500
+ display if printer is online?
 */