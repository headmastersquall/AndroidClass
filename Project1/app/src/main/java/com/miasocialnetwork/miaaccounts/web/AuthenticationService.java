package com.miasocialnetwork.miaaccounts.web;

import android.accounts.AuthenticatorException;

public class AuthenticationService {
	public Boolean isLoginValid(final String email, final String password) {
		return email.equals("foo@bar.com") && password.equals("abc123");
	}

	public Boolean isTokenValid(final String token) {
		return token.equals("mytoken");
	}

	public AuthenticationService register(final String email, final String token) {
		if (isTokenValid(token)) {
			return this;
		}
		//TODO: Add register call
		throw new IllegalArgumentException("Invalid token");
	}

	public AuthenticationService login(final String email, final String password) throws AuthenticatorException {
		if (isLoginValid(email, password)) {
			return this;
		}
		//TODO: Add login call
		throw new AuthenticatorException("Invalid login credentials");
	}
}
