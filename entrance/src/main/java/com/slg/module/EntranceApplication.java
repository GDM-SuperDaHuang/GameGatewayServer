package com.slg.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
//@ComponentScan(basePackages = {"com.slg.module","com.slg.module.handle"})
//@Slf4j
//@ComponentScan
public class EntranceApplication {
	public static void main(String[] args) {
		System.out.println("服务器开始启动.......");
		SpringApplication.run(EntranceApplication.class, args);
	}

}
