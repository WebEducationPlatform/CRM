package com.ewp.crm.models;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "potential_client")
public class PotentialClient {

	@Id
	@GeneratedValue
	@Column(name = "potential_client_id")
	private Long id;

	//unreliable name to address to client
	@Column(name = "first_name")
	private String name;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "upload_date")
	private LocalDateTime uploadDate;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinTable(name = "potential_client_social_profile",
			joinColumns = {@JoinColumn(name = "potential_client_id", foreignKey = @ForeignKey(name = "FK_POTENTIAL_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "social_network_id", foreignKey = @ForeignKey(name = "FK_SOCIAL_NETWORK"))})
	private List<SocialProfile> socialProfiles = new ArrayList<>();

	public PotentialClient() {
		this.uploadDate = LocalDateTime.now();
	}

	public PotentialClient(String name) {
		this();
		this.name = name;
	}

	public PotentialClient(String name, String lastName) {
		this();
		this.name = name;
		this.lastName = lastName;
	}

	public PotentialClient(List<SocialProfile> socialProfiles) {
		this();
		this.socialProfiles = socialProfiles;
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

	public LocalDateTime getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDateTime uploadDate) {
		this.uploadDate = uploadDate;
	}

	public List<SocialProfile> getSocialProfiles() {
		return socialProfiles;
	}

	public void setSocialProfiles(List<SocialProfile> socialProfiles) {
		this.socialProfiles = socialProfiles;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PotentialClient)) return false;

		PotentialClient that = (PotentialClient) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
		if (uploadDate != null ? !uploadDate.equals(that.uploadDate) : that.uploadDate != null) return false;
		return socialProfiles != null ? socialProfiles.equals(that.socialProfiles) : that.socialProfiles == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (uploadDate != null ? uploadDate.hashCode() : 0);
		result = 31 * result + (socialProfiles != null ? socialProfiles.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "PotentialClient{" +
				"id=" + id +
				", name='" + name + '\'' +
				", lastName='" + lastName + '\'' +
				", uploadDate=" + uploadDate +
				", socialProfiles=" + socialProfiles +
				'}';
	}
}
