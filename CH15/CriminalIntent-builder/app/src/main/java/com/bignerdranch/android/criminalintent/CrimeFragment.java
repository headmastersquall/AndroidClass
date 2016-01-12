package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
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

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

	private static final String ARG_CRIME_ID = "crime_id";
	private static final String DIALOG_DATE = "DialogDate";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_CONTACT = 1;

	private Button dateButton;
	private Button suspectButton;
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
	public void onPause() {
		super.onPause();
		CrimeLab.getInstance(getActivity()).updateCrime(crime);
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

		final Button reportButton = (Button)v.findViewById(R.id.crime_report);
		reportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent i = ShareCompat.IntentBuilder
					.from(getActivity())
					.setType("text/plain")
					.setText(getCrimeReport())
					.setSubject(getString(R.string.crime_report_subject))
					.setChooserTitle(getString(R.string.send_report))
					.createChooserIntent();
				startActivity(i);
			}
		});

		final Intent pickContact = new Intent(
			Intent.ACTION_PICK,
			ContactsContract.Contacts.CONTENT_URI);
		suspectButton = (Button)v.findViewById(R.id.crime_suspect);
		suspectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(pickContact, REQUEST_CONTACT);
			}
		});

		if (crime.getSuspect() != null) {
			suspectButton.setText(crime.getSuspect());
		}

		final PackageManager packageManager = getActivity().getPackageManager();
		if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
			suspectButton.setEnabled(false);
		}
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
		} else if (requestCode == REQUEST_CONTACT && data != null) {
			final Uri contactUri = data.getData();
			final String[] queryFields = new String[] {
				ContactsContract.Contacts.DISPLAY_NAME
			};
			final Cursor c = getActivity().getContentResolver()
				.query(contactUri, queryFields, null, null, null);

			try {
				if (c.getCount() == 0) {
					return;
				}

				c.moveToFirst();
				final String suspect = c.getString(0);
				crime.setSuspect(suspect);
				suspectButton.setText(suspect);
			} finally {
				c.close();
			}
		}
	}

	private void updateDate() {
		dateButton.setText(crime.getDate().toString());
	}

	private String getCrimeReport() {
		final String dateFormat = "EEE, MMM dd";
		final String dateString = DateFormat.format(dateFormat, crime.getDate()).toString();
		final String solvedString = crime.isSolved()
			? getString(R.string.crime_report_solved)
			: getString(R.string.crime_report_unsolved);
		final String suspect = crime.getSuspect() == null
			? getString(R.string.crime_report_no_suspect)
			: getString(R.string.crime_report_suspect, crime.getSuspect());
		return getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);
	}
}
