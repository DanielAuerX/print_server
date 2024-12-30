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
+ display if printer is online?
+ print file (check if printer available, aks for configurations?, print)
+ figure out max file size
 */