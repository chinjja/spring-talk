package com.chinjja.talk.domain.auth.event;

import com.chinjja.talk.domain.user.model.User;

import lombok.Value;

@Value
public class VerifyCodeSent {
	User to;
	String code;
}
