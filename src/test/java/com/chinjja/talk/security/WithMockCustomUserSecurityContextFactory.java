package com.chinjja.talk.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.chinjja.talk.domain.user.model.User;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser mock) {
		var principal = User.builder()
				.id(mock.id())
				.username(mock.username())
				.password(mock.password())
				.build();
		var auth = new UsernamePasswordAuthenticationToken(principal, mock.password(), principal.getAuthorities());
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		return context;
	}
	
}
