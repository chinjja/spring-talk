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
public class ChatDto {
	long id;
	boolean visible;
	boolean joinable;
	String title;
	String description;
	UserDto owner;
	Instant createdAt;
}
