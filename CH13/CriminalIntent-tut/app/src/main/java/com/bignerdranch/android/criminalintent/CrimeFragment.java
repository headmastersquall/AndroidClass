package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

	private static final String ARG_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "DialogDate";
	private static final int REQUEST_DATE = 0;

	private Button dateButton;
	private Crime crime;

	public static CrimeFragment newInstance(UUID crimeId) {
		final Bundle args = new Bundle();
		final CrimeFragment fragment = new CrimeFragment();
		args.putSerializable(ARG_CRIME_ID, crimeId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
		crime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v =  inflater.inflate(R.layout.fragment_crime, container, false);
		final EditText titleField = (EditText) v.findViewById(R.id.crime_title);
		titleField.setText(crime.getTitle());
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
		dateButton = (Button) v.findViewById(R.id.crime_date);
		updateDate();
		dateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final FragmentManager manager = getFragmentManager();
				final DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(manager, DIALOG_DATE);
			}
		});

		final CheckBox solved = (CheckBox) v.findViewById(R.id.crime_solved);
		solved.setChecked(crime.isSolved());
		solved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				crime.setSolved(isChecked);
			}
		});
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_DATE) {
			final Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			crime.setDate(date);
			updateDate();
		}
	}

	private void updateDate() {
		dateButton.setText(crime.getDate().toString());
	}
}
