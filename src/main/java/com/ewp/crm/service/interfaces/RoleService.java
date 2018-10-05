package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Role;

public interface RoleService extends CommonService<Role> {

	Role getRoleByName(String roleName);
}
