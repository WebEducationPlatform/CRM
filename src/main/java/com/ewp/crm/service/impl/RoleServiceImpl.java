package com.ewp.crm.service.impl;

import com.ewp.crm.models.Role;
import com.ewp.crm.repository.interfaces.RoleDAO;
import com.ewp.crm.service.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleDAO roleDAO;

	@Autowired
	public RoleServiceImpl(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}


	@Override
	public List<Role> getAll() {
		return roleDAO.findAll();
	}

	@Override
	public Role get(Long id) {
		return roleDAO.findOne(id);
	}

	@Override
	public Role getByRoleName(String roleName) {
		return roleDAO.getByRoleName(roleName);
	}

	@Override
	public void add(Role role) {
		roleDAO.saveAndFlush(role);
	}

	@Override
	public void update(Role role) {
		roleDAO.saveAndFlush(role);
	}

	@Override
	public void delete(Long id) {
		roleDAO.delete(id);
	}

	@Override
	public void delete(Role role) {
		roleDAO.delete(role);
	}
}
