package com.chinjja.talk.domain.user.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Friend {
	@EmbeddedId
	private Id id;
	
	@MapsId("user_id")
	@ManyToOne
	private User user;
	
	@MapsId("other_id")
	@ManyToOne
	private User other;
	
	public Friend(User user, User other) {
		this.id = new Id(user.getId(), other.getId());
		this.user = user;
		this.other = other;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Id implements Serializable {
		@Column(name = "user_id")
		private long userId;
		
		@Column(name = "other_id")
		private long otherId;
	}
}
