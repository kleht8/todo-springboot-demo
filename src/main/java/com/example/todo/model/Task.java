package com.example.todo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@DynamicUpdate
@DynamicInsert
public class Task {
	
	public Task() {
		
	}
	
	public Task(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Task(Long id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public Task(Long id, String name, String description, Boolean done) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.done = done;
	}
	
	@JsonProperty(access = Access.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
	private Long id;
	
	@NotNull
	@Size(min = 4, max = 63, message="Name length must be between 4 and 64 characters")
	private String name;
	
	@Column(name = "description")
	@Type(type="text")
	private String description;
	
	@JsonProperty(access = Access.READ_ONLY)
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private Date createdAt = new Date();

	@JsonProperty(access = Access.READ_ONLY)
	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "updated_at", nullable = false) 
	private Date updatedAt;
	
	@Column(name = "done", nullable = false)
	private Boolean done = false;
	

	
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Long getId() {
		return id;
	}

	
	
	
}
