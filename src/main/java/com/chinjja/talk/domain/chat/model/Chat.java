package com.chinjja.talk.domain.chat.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.chinjja.talk.domain.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(indexes = @Index(columnList = "visible"))
public class Chat {
	@Id
	@GeneratedValue
	private Long id;
	
	private boolean visible;
	private boolean joinable;
	
	@Column(nullable = false)
	private String type;
	private String title;
	private String description;
	
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User owner;
	
	@Column(nullable = false)
	private Instant createdAt;
	
	@PrePersist
	private void createdAt() {
		if(createdAt == null) {
			createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
		}
	}
}
