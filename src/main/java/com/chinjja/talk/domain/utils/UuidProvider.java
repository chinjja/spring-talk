package com.chinjja.talk.domain.utils;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UuidProvider {
	public UUID random() {
		return UUID.randomUUID();
	}
}
