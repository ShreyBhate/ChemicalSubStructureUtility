package com.spring.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

	@Configuration
	public class JdbcConfig {

	    @Autowired
	    private Environment env;

	    @Bean
	    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }

	    @Bean(name = "dataSource")
	    public DataSource dataSource() {
	        BasicDataSource datasource = new BasicDataSource();
	        datasource.setDriverClassName("com.mysql.jdbc.Driver");

//	     
	      datasource.setUrl("jdbc:mysql://192.168.10.6:3306/MEDCHEM");
	      datasource.setUsername("root");
	      datasource.setPassword("root123");
	        return datasource;
	    }

	    @Bean(name = "jdbcTemplate")
	    public JdbcTemplate jdbcTemplate() {
	        return new JdbcTemplate(dataSource());
	    }
	}

