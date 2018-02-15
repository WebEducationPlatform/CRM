package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "statuses")
public class Status implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "status_id")
	private Long id;

	@Column(name = "status_name", nullable = false)
	private String name;


	public Status(String name) {
		this.name = name;
	}

	public Status() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Status)) return false;

		Status status = (Status) o;

		return name.equals(status.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;

	}
}
