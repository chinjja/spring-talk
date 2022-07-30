package com.chinjja.talk.domain.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
	@Id
	@GeneratedValue
	private Long id;

	@OneToOne(optional = false)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User user;
	
	@Column(nullable = false)
	private String accessToken;
	
	@Column(nullable = false)
	private String refreshToken;
}
