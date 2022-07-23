package com.chinjja.talk.domain.chat.converter;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.chinjja.talk.domain.chat.dto.ChatUserDto;
import com.chinjja.talk.domain.chat.model.ChatUser;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatUserToChatUserDtoConverter implements Converter<ChatUser, ChatUserDto> {
	private final ModelMapper modelMapper;
	
	@Override
	public ChatUserDto convert(ChatUser source) {
		return modelMapper.map(source, ChatUserDto.class);
	}
}
