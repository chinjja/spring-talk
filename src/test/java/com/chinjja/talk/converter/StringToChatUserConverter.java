package com.chinjja.talk.converter;

import org.springframework.core.convert.converter.Converter;

import com.chinjja.talk.domain.chat.model.ChatUser;

public interface StringToChatUserConverter extends Converter<String, ChatUser> {

}
