package com.chinjja.talk.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsernamePasswordRequest {
	private String username;
	private String password;
}
