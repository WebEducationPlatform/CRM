package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "status")
public class Status implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Long id;

	@Column(name = "status_name", nullable = false, unique = true)
	private String name;

	@Basic
	@Column(name = "is_invisible")
	private Boolean isInvisible = false;

	@Basic
	@Column(name = "position")
	private Long position;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "status_clients",
			joinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))},
			inverseJoinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))})
	private List<Client> clients;

	@Column (name = "create_student")
	private boolean createStudent;

	@Column(name = "trial_offset")
	private Integer trialOffset;

	@Column(name = "next_payment_offset")
	private Integer nextPaymentOffset;

	public Status(String name, Boolean isInvisible, Long position, boolean createStudent, Integer trialOffset, Integer nextPaymentOffset) {
		this.name = name;
		this.isInvisible = isInvisible;
		this.position = position;
		this.createStudent = createStudent;
		this.trialOffset = trialOffset;
		this.nextPaymentOffset = nextPaymentOffset;
	}

	public Status(String name) {
		this.name = name;
		this.createStudent = false;
		this.trialOffset = 0;
		this.nextPaymentOffset = 0;
	}

	public Status() {
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
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

	public boolean isCreateStudent() {
		return createStudent;
	}

	public void setCreateStudent(boolean createStudent) {
		this.createStudent = createStudent;
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

	public Integer getTrialOffset() {
		return trialOffset;
	}

	public void setTrialOffset(Integer trialOffset) {
		this.trialOffset = trialOffset;
	}

	public Integer getNextPaymentOffset() {
		return nextPaymentOffset;
	}

	public void setNextPaymentOffset(Integer nextPaymentOffset) {
		this.nextPaymentOffset = nextPaymentOffset;
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
