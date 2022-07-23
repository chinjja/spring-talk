package com.chinjja.talk;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalkApplication.class, args);
	}

	@Bean
	ModelMapper modelMapper() {
		var mapper = new ModelMapper();
		mapper.getConfiguration().setSkipNullEnabled(true);
		return mapper;
	}
}
