package com.spring.config;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.google.gson.Gson;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.spring")
public class AppConfig {
	
	
	@Bean
	public Gson gson() {
		return new Gson();
	}
	
	
	
}
