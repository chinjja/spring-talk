package com.chinjja.talk.domain.auth.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chinjja.talk.domain.auth.exception.RefreshTokenException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class AuthControllerAdvice {
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String onHandler(IllegalArgumentException e) {
		log.info("bad request: {}", e.getMessage());
		return e.getMessage();
	}
	
	@ExceptionHandler(RefreshTokenException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public String onHandler(RefreshTokenException e) {
		log.info("unauthorized: {}", e.getMessage());
		return e.getMessage();
	}
}
