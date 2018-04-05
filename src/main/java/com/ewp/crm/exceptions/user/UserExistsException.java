package com.ewp.crm.exceptions.user;

public class UserExistsException extends RuntimeException {
	public UserExistsException() {
		super("Пользователь c таким e-mail уже существует");
	}
}

