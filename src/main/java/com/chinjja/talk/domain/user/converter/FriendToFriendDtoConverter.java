package com.chinjja.talk.domain.user.converter;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.chinjja.talk.domain.user.dto.FriendDto;
import com.chinjja.talk.domain.user.model.Friend;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FriendToFriendDtoConverter implements Converter<Friend, FriendDto> {
	private final ModelMapper modelMapper;
	@Override
	public FriendDto convert(Friend source) {
		return modelMapper.map(source, FriendDto.class);
	}
}
