package com.chinjja.talk.domain.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class CurrentContextPathProvider {
	public String getCurrentContextPath() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
	}
}
