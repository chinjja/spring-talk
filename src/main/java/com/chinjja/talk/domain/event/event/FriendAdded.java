package com.chinjja.talk.domain.event.event;

import com.chinjja.talk.domain.user.model.User;

import lombok.Value;

@Value
public class FriendAdded {
	User user;
	User friend;
}
