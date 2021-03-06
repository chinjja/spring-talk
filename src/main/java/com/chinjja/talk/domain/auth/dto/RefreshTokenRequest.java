package com.chinjja.talk.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RefreshTokenRequest {
	String accessToken;
	String refreshToken;
}
