package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class CrimeFragment extends Fragment {
	private Crime crime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		crime = new Crime();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v =  inflater.inflate(R.layout.fragment_crime, container, false);
		EditText titleField = (EditText) v.findViewById(R.id.crime_title);
		titleField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				crime.setTitle(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		Button dateButton = (Button) v.findViewById(R.id.crime_date);
		String date = DateFormat.getMediumDateFormat(getContext()).format(crime.getDate());
		dateButton.setText(date);
		dateButton.setEnabled(false);

		final CheckBox solved = (CheckBox) v.findViewById(R.id.crime_solved);
		solved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				crime.setSolved(isChecked);
			}
		});
		return v;
	}
}
