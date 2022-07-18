package com.chinjja.talk.domain.auth.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VerificationCode  {
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne(optional = false)
	private User user;
	
	@Column(nullable = false)
	private Instant issuedAt;
	
	@Column(nullable = false)
	private String code;
	
	@PrePersist
	private void issuedAt() {
		if(issuedAt == null) {
			issuedAt = Instant.now();
		}
	}
}
