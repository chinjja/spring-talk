package com.chinjja.talk.domain.auth.dto;

import lombok.Value;

@Value
public class RefreshTokenRequest {
	String accessToken;
	String refreshToken;
}
