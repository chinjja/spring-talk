package com.chinjja.talk.domain.chat.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
public class DirectChat {
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne(optional = false)
	private Chat chat;
	
	@ManyToOne(optional = false)
	private User user1;
	
	@ManyToOne(optional = false)
	private User user2;
}
