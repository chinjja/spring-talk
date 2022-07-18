package com.chinjja.talk.infra.mail.dto;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TransactionalMailMessage {
	User to;
	String subject;
	String text;
}
