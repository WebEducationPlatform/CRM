package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Status implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "status_id")
	private Long id;

	@Column(name = "status_name", nullable = false, unique = true)
	private String name;

	@Basic
	private Boolean isInvisible = false;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "status_clients",
			joinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))},
			inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))})
	private List<Client> clients;

	public Status(String name, Boolean isInvisible) {
		this.name = name;
		this.isInvisible = isInvisible;
	}

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

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public void addClient(Client client) {
		if (this.clients == null) {
			this.clients = new ArrayList<>();
		}
		this.clients.add(client);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Status)) return false;
		Status status = (Status) o;
		return Objects.equals(id, status.id) &&
				Objects.equals(name, status.name) &&
				Objects.equals(isInvisible, status.isInvisible);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, isInvisible);
	}

	@Override
	public String toString() {
		return name;
	}


	public Boolean getInvisible() {
		return isInvisible;
	}

	public void setInvisible(Boolean invisible) {
		isInvisible = invisible;
	}
}
