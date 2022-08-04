package com.chinjja.talk.domain.messenger.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessengerDto {
	private UUID chatId;
	private String objectType;
	private String command;
	private Object data;
}
