package com.chinjja.talk.domain.auth.common;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Component;

import com.chinjja.talk.domain.user.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final JwtProperties properties;
	
	public Map<String, Object> getBodyFromToken(String token) {
		var body = token.substring(token.indexOf('.')+1, token.lastIndexOf('.'));
		var decoded = Base64.getDecoder().decode(body);
		var parser = new BasicJsonParser();
		return parser.parseMap(new String(decoded));
	}

	public String getUsernameFromToken(String token) {
		return (String)getBodyFromToken(token).get(Claims.SUBJECT);
	}

	public Date getIssuedAtDateFromToken(String token) {
		var json = getBodyFromToken(token);
		return new Date((Long)(json.get(Claims.ISSUED_AT))*1000);
	}

	public Date getExpirationDateFromToken(String token) {
		var json = getBodyFromToken(token);
		return new Date((Long)(json.get(Claims.EXPIRATION))*1000);
	}

	private Jws<Claims> decodeToken(String secret, String token) {
		return Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token);
	}
	
	public String generateAccessToken(User user) {
		final var now = Instant.now();
		final var exp = now.plus(properties.getAccessExpiration());
		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(exp))
				.signWith(SignatureAlgorithm.HS512, properties.getAccessSecret())
				.compact();
	}
	
	public String generateRefreshToken(User user) {
		final var now = Instant.now();
		final var exp = now.plus(properties.getRefreshExpiration());
		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(exp))
				.signWith(SignatureAlgorithm.HS512, properties.getRefreshSecret())
				.compact();
	}

	public boolean validateAccessToken(String token) {
		var jws = decodeToken(properties.getAccessSecret(), token);
		return jws != null;
	}

	public boolean validateRefreshToken(String token) {
		var jws = decodeToken(properties.getRefreshSecret(), token);
		return jws != null;
	}
}
