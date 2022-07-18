package com.chinjja.talk.domain.auth.config;

import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VerificationCodeConfig {
	@Bean
	Random random() {
		return new Random();
	}
}
