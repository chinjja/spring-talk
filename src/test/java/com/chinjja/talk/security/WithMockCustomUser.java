package com.chinjja.talk.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
	String id() default "2cc304d8-ee51-43f6-a595-7db7f38d889d";
	String username() default "user";
	String password() default "1234";
}
