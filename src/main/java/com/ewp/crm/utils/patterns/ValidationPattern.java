package com.ewp.crm.utils.patterns;

public interface ValidationPattern {
	String EMAIL_PATTERN =
			"^$|^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	String PHONE_NUMBER_PATTERN = "^$|^((7)([0-9]{10}))$";

	String VK_LINK_PATTERN = "^(https?://)?m?.?vk.com/id\\d+$";
}
