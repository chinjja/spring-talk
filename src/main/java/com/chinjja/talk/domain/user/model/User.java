package com.chinjja.talk.domain.user.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "USERS")
public class User implements UserDetails {
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(unique = true, nullable = false)
	@Email
	private String username;
	
	@Column(nullable = false)
	@JsonIgnore
	@ToString.Exclude
	private String password;
	
	@ElementCollection
	@JsonIgnore
	@Singular
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Set<String> roles;
	
	private String photoId;
	private String name;
	private String state;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toSet());
	}
	
	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}
	
}
