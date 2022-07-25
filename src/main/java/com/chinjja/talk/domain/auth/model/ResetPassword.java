package com.chinjja.talk.domain.auth.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ResetPassword {
	@Id
	@Email
	String email;
	
	@Column(nullable = false, unique = true)
	String uuid;
	
	@Column(nullable = false)
	Instant issuedAt;
}
