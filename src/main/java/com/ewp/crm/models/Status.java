package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class Status implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "status_id")
	private Long id;

	@Column(name = "status_name", nullable = false)
	private String name;

	@OneToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "status_client",
			joinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))},
			inverseJoinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))})
	private List<Client> clients;

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

		if (id != null ? !id.equals(status.id) : status.id != null) return false;
		return name.equals(status.name);
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

}
