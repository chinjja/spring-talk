package com.chinjja.talk.domain.chat.dto;

import java.time.Instant;

import com.chinjja.talk.domain.user.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChatMessageDto {
	long id;
	UserDto sender;
	String message;
	Instant instant;
}
