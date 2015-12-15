package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

	public static final String EXTRA_DATE =
		"com.bignerdranch.android.criminalintent.date";

	private static final String ARG_DATE = "date";

	private DatePicker datePicker;

	public static DatePickerFragment newInstance(final Date date) {
		final Bundle args = new Bundle();
		args.putSerializable(ARG_DATE, date);
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime((Date) getArguments().getSerializable(ARG_DATE));
		initializeDatePicker(view, calendar);

		final Button ok = (Button) view.findViewById(R.id.date_picker_button_ok);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Date date = getDateFromDatePicker(calendar);
				sendResult(Activity.RESULT_OK, date);
				dismiss();
			}
		});
		return view;
	}

	private void initializeDatePicker(final View v, final Calendar calendar) {
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);

		datePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
		datePicker.init(year, month, day, null);
	}

	private Date getDateFromDatePicker(Calendar calendar) {
		final int year = datePicker.getYear();
		final int month = datePicker.getMonth();
		final int day = datePicker.getDayOfMonth();
		final Date date = new GregorianCalendar(
			year, month, day,
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)).getTime();
		return date;
	}

	private void sendResult(int resultCode, Date date) {
		if (getTargetFragment() == null) {
			return;
		}

		final Intent intent = new Intent();
		intent.putExtra(EXTRA_DATE, date);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
	}
}
