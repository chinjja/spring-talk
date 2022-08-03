package com.chinjja.talk.domain.user.event;

import com.chinjja.talk.domain.user.model.Friend;

import lombok.Value;

@Value
public class FriendEvent {
	String type;
	Friend friend;
}
