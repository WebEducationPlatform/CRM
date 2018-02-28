package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Role;

import java.util.List;

public interface RoleService {

	List<Role> getAll();

	Role get(Long id);

	Role getByRoleName(String roleName);

	void add(Role role);

	void update(Role role);

	void delete(Long id);

	void delete(Role role);
}
