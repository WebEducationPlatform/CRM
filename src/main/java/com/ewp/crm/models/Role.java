package com.ewp.crm.models;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Роль (user, admin, mentor и тд)
 */
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "role_name", unique = true, nullable = false)
	private String roleName;

	public Role() {
	}

	public Role(String roleName) {
		this.roleName = roleName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String getAuthority() {
		return roleName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Role role = (Role) o;

		return roleName != null ? roleName.equals(role.roleName) : role.roleName == null;
	}

	@Override
	public int hashCode() {
		return roleName != null ? roleName.hashCode() : 0;
	}

	@Override
	public String toString() {
		return roleName + " (" + id + ")";
	}
}
