package com.ewp.crm.exceptions.user;

public class UserPhotoException extends RuntimeException {
	public UserPhotoException() {
			super("Произошла ошибка сохранения фотографии");
		}
}
