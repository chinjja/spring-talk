package com.chinjja.talk.domain.chat.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ChatControllerAdvice {
	@ExceptionHandler({
		IllegalArgumentException.class,
		NullPointerException.class
		})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String onHandler(Exception e) {
		log.info("bad request: {}", e.getMessage());
		return e.getMessage();
	}
}
