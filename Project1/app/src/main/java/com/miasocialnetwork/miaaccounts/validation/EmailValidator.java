package com.miasocialnetwork.miaaccounts.validation;

public class EmailValidator {
	private static final int MIN_LENGTH = 6;

	public Boolean isValid(final String email) {
		return email.contains("@") && email.contains(".") && email.length() > MIN_LENGTH;
	}
}
