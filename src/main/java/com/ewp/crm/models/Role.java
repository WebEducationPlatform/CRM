package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class Role implements GrantedAuthority {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true, nullable = false)
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
	public String getAuthority() {
		return roleName;
	}
}
