package com.richard.demo;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 名字无所谓，但是要有SpringBootApplication注解
 * 要有main方法，调用SpringApplication.run
 *
 * @author richard.xu03@sap.com
 * @version $Id: DemoApplication.java, v 0.1 May 23, 2020 3:36:39 PM richard.xu Exp $
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.richard.demo.dao") // 自动扫描mapper
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
