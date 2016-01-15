package com.miasocialnetwork.miaaccounts;

import android.accounts.AuthenticatorException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miasocialnetwork.miaaccounts.validation.EmailValidator;
import com.miasocialnetwork.miaaccounts.validation.PasswordValidator;
import com.miasocialnetwork.miaaccounts.web.AuthenticationService;

public class LoginFragment extends Fragment {

	private EditText emailEditText;
	private EditText passwordEditText;

	@Override
	public View onCreateView(
		final LayoutInflater inflater,
		final ViewGroup container,
		final Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.fragment_login, container, false);
		final Button loginButton = (Button)view.findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				login();
			}
		});

		final Button signUpButton = (Button)view.findViewById(R.id.login_sign_up_button);
		signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Intent intent = SignUpFragment.newInstance(getActivity());
				startActivity(intent);
			}
		});
		emailEditText = (EditText)view.findViewById(R.id.login_email_edit_text);
		passwordEditText = (EditText)view.findViewById(R.id.login_password_edit_text);
		return view;
	}

	private void login() {
		final String email = emailEditText.getText().toString();
		final String password = passwordEditText.getText().toString();

		final EmailValidator emailValidator = new EmailValidator();
		final PasswordValidator passwordValidator = new PasswordValidator();
		final AuthenticationService authService = new AuthenticationService();
		if (!emailValidator.isValid(email) || !passwordValidator.isValid(password)) {
			Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			authService.login(email, password);
			Toast.makeText(getActivity(), R.string.login_successful, Toast.LENGTH_SHORT).show();
		} catch (AuthenticatorException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
}
