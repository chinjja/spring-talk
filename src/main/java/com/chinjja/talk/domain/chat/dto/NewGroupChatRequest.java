package com.chinjja.talk.domain.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NewGroupChatRequest {
	private String title;
	private List<String> usernameList;
}
