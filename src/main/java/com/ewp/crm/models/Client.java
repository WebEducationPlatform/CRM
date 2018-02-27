package com.ewp.crm.models;

import com.ewp.crm.utils.patterns.ValidationPattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
public class Client implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "client_id")
	private Long id;

	@Column(name = "first_name", nullable = false)
	private String name;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "phone_number", unique = true)
	private String phoneNumber;

	@Size(max = 50)
	@Email(regexp = ValidationPattern.EMAIL_PATTERN)
	@Column(name = "email", length = 50, nullable = false, unique = true)
	private String email;

	@Column(name = "age")
	private byte age;

	@Column(name = "sex")
	@Enumerated(EnumType.STRING)
	private Sex sex;

	@JsonManagedReference
	@ManyToOne
	@JoinColumn(name = "status_id")
	@JoinTable(name = "status_client",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_STATUS"))})
	private Status status;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "client_comment",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "comment_id", foreignKey = @ForeignKey(name = "FK_COMMENT"))})
	private List<Comment> comments;

	public List<ClientHistory> getHistory() {
		return history;
	}

	public void setHistory(List<ClientHistory> history) {
		this.history = history;
	}

	public void addHistory(ClientHistory history) {
		this.history.add(history);
	}

	@JsonManagedReference
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
	@JoinTable(name = "history_client",
			joinColumns = {@JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "FK_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "history_id", foreignKey = @ForeignKey(name = "FK_HISTORY"))})
	@OrderBy("id DESC")
	private List<ClientHistory> history = new ArrayList<>();

	public Client() {
	}

	public Client(String name, String lastName, String phoneNumber, String email, byte age, Sex sex, Status status) {
		this.name = name;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.age = age;
		this.sex = sex;
		this.status = status;

	}

	public Client(String name, String lastName, String phoneNumber, String email, byte age, Sex sex) {
		this.name = name;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.age = age;
		this.sex = sex;
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

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte getAge() {
		return age;
	}

	public void setAge(byte age) {
		this.age = age;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Client client = (Client) o;

		if (age != client.age) return false;
		if (id != null ? !id.equals(client.id) : client.id != null) return false;
		if (name != null ? !name.equals(client.name) : client.name != null) return false;
		if (lastName != null ? !lastName.equals(client.lastName) : client.lastName != null) return false;
		if (phoneNumber != null ? !phoneNumber.equals(client.phoneNumber) : client.phoneNumber != null) return false;
		if (email != null ? !email.equals(client.email) : client.email != null) return false;
		if (sex != client.sex) return false;
		if (status != null ? !status.equals(client.status) : client.status != null) return false;
		return comments != null ? comments.equals(client.comments) : client.comments == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (int) age;
		result = 31 * result + (sex != null ? sex.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (comments != null ? comments.hashCode() : 0);
		return result;
	}

	public enum Sex {
		MALE, FEMALE;
	}

}
