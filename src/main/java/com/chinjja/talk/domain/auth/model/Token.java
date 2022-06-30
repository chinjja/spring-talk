package com.chinjja.talk.domain.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.chinjja.talk.domain.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;

	@JsonIgnore
	@OneToOne(optional = false)
	private User user;
	
	@Column(nullable = false)
	private String accessToken;
	
	@Column(nullable = false)
	private String refreshToken;
}
