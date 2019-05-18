package com.ewp.crm.exceptions.client;

public class ClientExistsException extends RuntimeException {
	public ClientExistsException(String message) {
		super(message);
	}
}
