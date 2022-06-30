package com.chinjja.talk.domain.auth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chinjja.talk.domain.auth.services.AuthService;

@Configuration
public class InitAdminConfig {
	@Bean
	CommandLineRunner initAdmin(AuthService authService) {
		return args -> {
			authService.initAdmin();
		};
	}
}
