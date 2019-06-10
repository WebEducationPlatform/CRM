package com.ewp.crm.models;

import com.ewp.crm.util.patterns.ValidationPattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Пользователь CRM, менеджер, ментор и тд
 */
@Entity
@Table(name = "user")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column (name = "user_id")
	private Long id;

	@Pattern(regexp = ValidationPattern.USER_FIRSTNAME_LASTNAME_PATTERN)
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Pattern(regexp = ValidationPattern.USER_FIRSTNAME_LASTNAME_PATTERN)
	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "vk")
	private String vk;

	@Column(name = "sex", nullable = false) // gender (пол или мужской/женский род) правильнее. sex - это ибатсо
	private String sex;

	@Column(name = "city", nullable = false)
	private String city;

	@Column(name = "country", nullable = false)
	private String country;

	/**
	 * Ссылка на фото
	 */
	@Column(name = "photo")
	private String photo;

	/**
	 * Тип фотографии???
	 */
	@Column(name = "photoType")
	private String photoType;

	/**
	 * Доступна ли пользователю ip-телефония
	 */
	@Column(name = "ip_telephony")
	private boolean ipTelephony; // некорректное названия поля типа boolean и вообще

	/**
	 * ????????
	 */
	@Column(name = "is_enabled") // включен, разрешен??? user-info.js, всегда false
	private boolean isEnabled;

	@Column(name = "new_client_notify_is_enabled")
	private boolean newClientNotifyIsEnabled = true;

	/**
	 * ????????
	 */
	@Column(name = "is_verified") // проверен на что??? user-info.js, всегда false
	private boolean isVerified;

	/**
	 * ????????
	 */
	@Column(name = "autoAnswer") // автоответ. в какой ситуации? РОМАН ГАПОНОВ
	private String autoAnswer;

	/**
	 * Настройки авторизации пользователя в vk
	 */
	@Column(name = "vkToken")
	private String vkToken;

	/**
	 * Настройки авторизации пользователя в google
	 */
	@Column(name = "googleToken")
	private String googleToken;

	/**
	 * Присылать ли уведомления на электронную почту
	 */
	@Column(name = "enable_mail_notifications")
	private boolean enableMailNotifications;

	/**
	 * Присылать ли уведомления в sms
	 */
	@Column(name = "enable_sms_notifications")
	private boolean enableSmsNotifications;

	/**
	 * Права (роль)
	 * FetchType.EAGER for initialize all fields.
	 * We use FetchMode.SUBSELECT for loading all elements of all collections.
	 */
	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinTable(name = "permissions",
			joinColumns = {@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))},
			inverseJoinColumns = {@JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "FK_ROLE"))})
	private List<Role> role = new ArrayList<>();

	/**
	 * Индивидуальная настройка интрефейса
	 */
	@Column(name = "color_background")
	private String colorBackground;

	/**
	 * Настройки фильтров на странице Все студенты
	 */
	@Column(name = "student_page_filters")
	private String studentPageFilters;

	public User() {
		this.isEnabled = false;
		this.isVerified = false;
	}

	public User(String firstName, String lastName, LocalDate birthDate, String phoneNumber, String email, String password, String vk, String sex,  String city, String country,  List<Role> role, boolean ipTelephony, boolean isVerified) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
		this.vk = vk;
		this.sex = sex;
		this.city = city;
		this.country = country;
		this.role = role;
		this.ipTelephony = ipTelephony;
		this.isVerified = isVerified;
		this.isEnabled = isVerified;
	}

	public void setVerified(boolean verified) {
		isVerified = verified;
	}

	public String getVkToken() {
		return vkToken;
	}

	public void setVkToken(String vkToken) {
		this.vkToken = vkToken;
	}

	public String getGoogleToken() {
		return googleToken;
	}

	public void setGoogleToken(String googleToken) {
		this.googleToken = googleToken;
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

	public String getAutoAnswer() {
		return autoAnswer;
	}

	public void setAutoAnswer(String autoAnswer) {
		this.autoAnswer = autoAnswer;
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

	public String getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(String colorBackground) {
		this.colorBackground = colorBackground;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.synchronizedList(role);
	}

	@Override
	public String getUsername() {
		return email;
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

	public boolean isNewClientNotifyIsEnabled() {
		return newClientNotifyIsEnabled;
	}

	public void setNewClientNotifyIsEnabled(boolean newClientNotifyIsEnabled) {
		this.newClientNotifyIsEnabled = newClientNotifyIsEnabled;
	}

	public String getStudentPageFilters() {
		return studentPageFilters;
	}

	public void setStudentPageFilters(String studentPageFilters) {
		this.studentPageFilters = studentPageFilters;
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

	public boolean isIpTelephony() {
		return ipTelephony;
	}

	public void setIpTelephony(boolean ipTelephony) {
		this.ipTelephony = ipTelephony;
	}

	public void  setEnabled(boolean availability){
		this.isEnabled = availability;
	}

	public void setIsEnabled(boolean availability) {
		this.isEnabled = availability;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setIsVerified(boolean verified) {
		isVerified = verified;
	}

	public boolean isEnableSmsNotifications() {
		return enableSmsNotifications;
	}

	public void setEnableSmsNotifications(boolean enableSmsNotifications) {
		this.enableSmsNotifications = enableSmsNotifications;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
}
