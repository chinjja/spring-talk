package com.chinjja.talk.domain.user.converter;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.chinjja.talk.domain.user.dto.UserDto;
import com.chinjja.talk.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserToUserDtoConverter implements Converter<User, UserDto> {
	private final ModelMapper modelMapper;
	@Override
	public UserDto convert(User source) {
		return modelMapper.map(source, UserDto.class);
	}
}
