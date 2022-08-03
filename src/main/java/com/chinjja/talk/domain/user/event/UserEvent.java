package com.chinjja.talk.domain.user.event;

import com.chinjja.talk.domain.user.model.User;

import lombok.Value;

@Value
public class UserEvent {
	String type;
	User user;
}
