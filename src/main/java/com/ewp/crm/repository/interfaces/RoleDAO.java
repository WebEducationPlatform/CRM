package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;

public interface RoleDAO extends CommonGenericRepository<Role> {
	Role getByRoleName(String roleName);
}
