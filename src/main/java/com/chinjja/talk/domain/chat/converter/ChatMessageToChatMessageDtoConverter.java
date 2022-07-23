package com.chinjja.talk.domain.chat.converter;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.chinjja.talk.domain.chat.dto.ChatMessageDto;
import com.chinjja.talk.domain.chat.model.ChatMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageToChatMessageDtoConverter implements Converter<ChatMessage, ChatMessageDto> {
	private final ModelMapper modelMapper;
	@Override
	public ChatMessageDto convert(ChatMessage source) {
		return modelMapper.map(source, ChatMessageDto.class);
	}

}
