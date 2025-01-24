package com.lb.book_worm_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BookWormApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookWormApiApplication.class, args);
	}

}
