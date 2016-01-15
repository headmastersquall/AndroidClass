package com.miasocialnetwork.miaaccounts.validation;

public class PasswordValidator {
	private static final int MIN_LENGTH = 5;

	public Boolean isValid(final String password) {
		return password.length() >= MIN_LENGTH;
	}
}
