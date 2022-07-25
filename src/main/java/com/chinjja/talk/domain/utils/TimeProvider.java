package com.chinjja.talk.domain.utils;

import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class TimeProvider {
	public Instant now() {
		return Instant.now();
	}
}
