package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDAO extends JpaRepository<Role, Long> {

	Role getByRoleName(String roleName);

}
