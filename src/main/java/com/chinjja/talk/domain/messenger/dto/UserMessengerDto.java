package com.chinjja.talk.domain.messenger.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMessengerDto {
	private String objectType;
	private String command;
	private Object data;
}
