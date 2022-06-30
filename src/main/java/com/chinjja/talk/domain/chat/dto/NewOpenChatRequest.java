package com.chinjja.talk.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NewOpenChatRequest {
	private String title;
	private String description;
	private boolean visible;
}
