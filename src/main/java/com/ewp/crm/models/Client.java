package com.ewp.crm.models;

import com.ewp.crm.utils.patterns.ValidationPattern;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class Client implements Serializable {
	@Id
	@GeneratedValue
	@Column(name = "user_id")
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



	@NotNull
	@ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.MERGE,CascadeType.DETACH,CascadeType.DETACH,CascadeType.PERSIST})
	@JoinColumn(name = "status_id")
	private Status status;

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

	private enum Sex {
		MALE, FEMALE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Client)) return false;

		Client client = (Client) o;

		if (age != client.age) return false;
		if (!name.equals(client.name)) return false;
		if (lastName != null ? !lastName.equals(client.lastName) : client.lastName != null) return false;
		if (phoneNumber != null ? !phoneNumber.equals(client.phoneNumber) : client.phoneNumber != null) return false;
		if (!email.equals(client.email)) return false;
		if (sex != client.sex) return false;
		return status.equals(client.status);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
		result = 31 * result + email.hashCode();
		result = 31 * result + (int) age;
		result = 31 * result + (sex != null ? sex.hashCode() : 0);
		result = 31 * result + status.hashCode();
		return result;
	}

}
