package com.chinjja.talk.domain.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chinjja.talk.domain.auth.common.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	private final JwtTokenProvider jwtTokenService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		
		if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			var jwtToken = requestTokenHeader.substring(7);
			try {
				var username = jwtTokenService.getUsernameFromToken(jwtToken);
				if(SecurityContextHolder.getContext().getAuthentication() == null) {
					var userDetails = userDetailsService.loadUserByUsername(username);
					if(jwtTokenService.validateAccessToken(jwtToken)) {
						var token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						token.setDetails(new WebAuthenticationDetails(request));
						SecurityContextHolder.getContext().setAuthentication(token);
					}
				}
			} catch(IllegalArgumentException e) {
				log.debug("Unable to get JWT Token");
			} catch(ExpiredJwtException e) {
				log.debug("JWT Token has expired");
			} catch(UsernameNotFoundException e) {
				log.debug("username not found", e);
			}
		} else {
			log.debug("JWT Token does ont begin with Bearer String");
		}
		
		filterChain.doFilter(request, response);
	}

}
