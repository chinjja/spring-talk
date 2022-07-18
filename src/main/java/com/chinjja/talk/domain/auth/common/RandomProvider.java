package com.chinjja.talk.domain.auth.common;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomProvider {
	private final Random rand = new Random();
	public int nextInt() {
		return rand.nextInt(1000000);
	}
}
