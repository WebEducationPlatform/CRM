package com.ewp.crm.security.service;


import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

	private final UserService userService;

	@Autowired
	public AuthenticationService(UserService userService) {
		this.userService = userService;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userService.getByEmailOrPhone(username, username);
		if (!user.isPresent()) {
			throw new UsernameNotFoundException("Username " + username + " not found");
		}
		return user.get();
	}
}
