package com.chinjja.talk.converter;

import org.springframework.core.convert.converter.Converter;

import com.chinjja.talk.domain.chat.model.Chat;

public interface StringToChatConverter extends Converter<String, Chat> {

}
