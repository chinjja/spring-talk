package com.chinjja.talk.domain.auth.event;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResetPasswordSent {
	String host;
	String email;
	String uuid;
}
