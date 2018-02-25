package com.ewp.crm.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table
public class User implements UserDetails {

	@Id
	@GeneratedValue
	private Long id;

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String password;
	private String vk;
	private byte[] photo;
	private String sex;
	private byte age;
	private String city;
	private String country;
	private String vacancy;
	private double salary;
	private Role role;

	public User() {
	}

	public User(String firstName, String lastName, String phoneNumber, String email, String password, double salary, Role role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
		this.salary = salary;
		this.role = role;
	}

	public User(String firstName, String lastName, String phoneNumber, String email, String password, String vk, byte[] photo, String sex, byte age, String city, String country, String vacancy, double salary, Role role) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
		this.vk = vk;
		this.photo = photo;
		this.sex = sex;
		this.age = age;
		this.city = city;
		this.country = country;
		this.vacancy = vacancy;
		this.salary = salary;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVk() {
		return vk;
	}

	public void setVk(String vk) {
		this.vk = vk;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public byte getAge() {
		return age;
	}

	public void setAge(byte age) {
		this.age = age;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getVacancy() {
		return vacancy;
	}

	public void setVacancy(String vacancy) {
		this.vacancy = vacancy;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(role);
	}

	@Override
	public String getUsername() {
		return firstName + " " + lastName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		if (age != user.age) return false;
		if (Double.compare(user.salary, salary) != 0) return false;
		if (id != null ? !id.equals(user.id) : user.id != null) return false;
		if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
		if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
		if (phoneNumber != null ? !phoneNumber.equals(user.phoneNumber) : user.phoneNumber != null) return false;
		if (email != null ? !email.equals(user.email) : user.email != null) return false;
		if (password != null ? !password.equals(user.password) : user.password != null) return false;
		if (vk != null ? !vk.equals(user.vk) : user.vk != null) return false;
		if (!Arrays.equals(photo, user.photo)) return false;
		if (sex != null ? !sex.equals(user.sex) : user.sex != null) return false;
		if (city != null ? !city.equals(user.city) : user.city != null) return false;
		if (country != null ? !country.equals(user.country) : user.country != null) return false;
		if (vacancy != null ? !vacancy.equals(user.vacancy) : user.vacancy != null) return false;
		return role != null ? role.equals(user.role) : user.role == null;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = id != null ? id.hashCode() : 0;
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (vk != null ? vk.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(photo);
		result = 31 * result + (sex != null ? sex.hashCode() : 0);
		result = 31 * result + (int) age;
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		result = 31 * result + (vacancy != null ? vacancy.hashCode() : 0);
		temp = Double.doubleToLongBits(salary);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (role != null ? role.hashCode() : 0);
		return result;
	}
}
