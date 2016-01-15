package com.miasocialnetwork.miaaccounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.miasocialnetwork.miaaccounts.validation.EmailValidator;
import com.miasocialnetwork.miaaccounts.web.AuthenticationService;

public class SignUpFragment extends Fragment {

	private EditText emailEditText;
	private EditText tokenEditText;

	public static Intent newInstance(final Context packageContext) {
		final Intent intent = new Intent(packageContext, SignUpActivity.class);
		return intent;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_signup, container, false);

		final Button signUpButton = (Button)view.findViewById(R.id.sign_up_button);
		signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				signUp();
			}
		});

		emailEditText = (EditText)view.findViewById(R.id.sign_up_email_edit_text);
		tokenEditText = (EditText)view.findViewById(R.id.sign_up_token_edit_text);
		return view;
	}

	private void signUp() {
		final String email = emailEditText.getText().toString();
		final String token = tokenEditText.getText().toString();

		final AuthenticationService authenticationService = new AuthenticationService();
		final EmailValidator emailValidator = new EmailValidator();

		if (!emailValidator.isValid(email)) {
			Toast.makeText(getActivity(), R.string.sign_up_invalid_email, Toast.LENGTH_SHORT).show();
			return;
		}

		if (!authenticationService.isTokenValid(token)) {
			Toast.makeText(getActivity(), R.string.sign_up_invalid_token, Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(getActivity(), R.string.sign_up_successful, Toast.LENGTH_SHORT).show();
	}
}
