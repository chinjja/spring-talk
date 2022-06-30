package com.chinjja.talk.domain.auth.common;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class JwtProperties {
	@Value("${jwt.accessSecret}")
	private String accessSecret;
	@Value("${jwt.refreshSecret}")
	private String refreshSecret;
	
	private Duration accessExpiration = Duration.ofHours(2);
	private Duration refreshExpiration = Duration.ofDays(14);
}
