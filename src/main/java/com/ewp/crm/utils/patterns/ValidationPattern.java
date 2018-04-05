package com.ewp.crm.utils.patterns;

public interface ValidationPattern {
	String EMAIL_PATTERN =
			"^$|^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
}
