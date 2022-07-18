package com.chinjja.talk.domain.auth.dto;

import com.chinjja.talk.domain.auth.model.Token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LoginResponse {
	boolean emailVerified;
	Token token;
}
