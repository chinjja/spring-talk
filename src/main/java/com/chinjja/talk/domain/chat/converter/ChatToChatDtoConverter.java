package com.chinjja.talk.domain.chat.converter;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.chinjja.talk.domain.chat.dto.ChatDto;
import com.chinjja.talk.domain.chat.model.Chat;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatToChatDtoConverter implements Converter<Chat, ChatDto> {
	private final ModelMapper modelMapper;
	
	@Override
	public ChatDto convert(Chat source) {
		return modelMapper.map(source, ChatDto.class);
	}
}
