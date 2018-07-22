package com.ewp.crm.models;

import com.ewp.crm.utils.patterns.ValidationPattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table
public class User implements UserDetails {

	@Id
	@GeneratedValue
	@Column (name = "user_id")
	private Long id;

	@Pattern(regexp = ValidationPattern.USER_FIRSTNAME_LASTNAME_PATTERN)
	@Column(nullable = false)
	private String firstName;

	@Pattern(regexp = ValidationPattern.USER_FIRSTNAME_LASTNAME_PATTERN)
	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	private String vk;

	@Column(nullable = false)
	private String sex;

	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private String country;

	@Column(name = "photo")
	private String photo;

	@Column(name = "photoType")
	private String photoType;

	@Column
	private boolean ipTelephony;

	@Column
	private boolean isEnabled;

	@Column(name = "vkToken")
	private String vkToken;

	@JsonIgnore
	@OneToMany
	@JoinTable(name = "assign_user_skype_call",
			joinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL_USER"))},
			inverseJoinColumns = {@JoinColumn(name = "assign_skype_call_id", foreignKey = @ForeignKey(name = "FK_ASSIGN_SKYPE_CALL"))})
	private List<AssignSkypeCall> userAssignSkypeCall;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_notification",
			joinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION_USER"))},
			inverseJoinColumns = {@JoinColumn(name = "notification_id", foreignKey = @ForeignKey(name = "FK_NOTIFICATION"))})
	private List<Notification> notifications;

	@JsonIgnore
	private boolean enableMailNotifications = true;

	@JsonIgnore
	@OneToMany(mappedBy = "ownerUser")
	private List<Client> ownedClients;

	@NotNull
	@ManyToMany(fetch = FetchType.EAGER, targetEntity = Role.class)
	@JoinTable(name = "permissions",
			joinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))},
			inverseJoinColumns = {@JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_ROLE"))})
	private List<Role> role = new ArrayList<>();

	public User() {
		this.isEnabled = true;
	}

	public User(String firstName, String lastName, String phoneNumber, String email, String password, String vk, String sex,  String city, String country,  List<Role> role, boolean ipTelephony) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
		this.vk = vk;
		this.sex = sex;
		this.city = city;
		this.country = country;
		this.role = role;
		this.ipTelephony = ipTelephony;
		this.isEnabled = true;
	}

	public String getVkToken() {
		return vkToken;
	}

	public void setVkToken(String vkToken) {
		this.vkToken = vkToken;
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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public String getFullCombinedName() {return this.firstName + this.lastName;}

	public String getPhoto() {
		return this.photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhotoType() {
		return photoType;
	}

	public void setPhotoType(String photoType) {
		this.photoType = photoType;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.synchronizedList(role);
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
		return isEnabled;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	public List<Client> getOwnedClients() {
		return ownedClients;
	}

	public void setOwnedClients(List<Client> ownedClients) {
		this.ownedClients = ownedClients;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;

		User user = (User) o;

		if (!id.equals(user.id)) return false;
		if (!phoneNumber.equals(user.phoneNumber)) return false;
		if (!email.equals(user.email)) return false;
		return vk != null ? vk.equals(user.vk) : user.vk == null;
	}

//	@Override
//	public int hashCode() {
//		int result = id.hashCode();
//		result = 31 * result + phoneNumber.hashCode();
//		result = 31 * result + email.hashCode();
//		return result;
//	}


	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getPhoneNumber() != null ? getPhoneNumber().hashCode() : 0);
		result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
		return result;
	}

	public boolean isEnableMailNotifications() {
		return enableMailNotifications;
	}

	public void setEnableMailNotifications(boolean enableMailNotifications) {
		this.enableMailNotifications = enableMailNotifications;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public boolean isIpTelephony() {
		return ipTelephony;
	}

	public void setIpTelephony(boolean ipTelephony) {
		this.ipTelephony = ipTelephony;
	}

	public void  setEnabled(boolean availability){
		this.isEnabled = availability;
	}

}
