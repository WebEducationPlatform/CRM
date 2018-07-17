package com.ewp.crm.service.impl;

import com.ewp.crm.models.Role;
import com.ewp.crm.repository.interfaces.RoleDAO;
import com.ewp.crm.service.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends CommonServiceImpl<Role> implements RoleService {
	private final RoleDAO roleDAO;

	@Autowired
	public RoleServiceImpl(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}

	@Override
	public Role getByRoleName(String roleName) {
		return roleDAO.getByRoleName(roleName);
	}
}
