package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {

	private CrimeAdapter adapter;
	private RecyclerView recyclerView;
	private View view;
	private boolean isSubtitleVisible;
	private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
		this.view = view;
		recyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		if (savedInstanceState != null) {
			isSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
		}
		updateUI();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVED_SUBTITLE_VISIBLE, isSubtitleVisible);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);

		final MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
		final int textId = isSubtitleVisible ? R.string.hide_subtitle : R.string.show_subtitle;
		subtitleItem.setTitle(textId);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_crime:
				createNewCrime();
				return true;
			case R.id.menu_item_show_subtitle:
				isSubtitleVisible = !isSubtitleVisible;
				getActivity().invalidateOptionsMenu();
				updateSubtitle();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void createNewCrime() {
		final Crime crime = new Crime();
		CrimeLab.getInstance(getActivity()).addCrime(crime);
		final Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
		startActivity(intent);
	}

	private void updateSubtitle() {
		final CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
		final int crimeCount = crimeLab.getCrimes().size();
		final String subtitle = getResources()
			.getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
		final AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.getSupportActionBar().setSubtitle(subtitle);
	}

	private void showHideNoCrimeText(final CrimeLab crimeLab) {
		final TextView noCrimesText = (TextView) view.findViewById(R.id.no_crimes_yet_textview);
		boolean visible = crimeLab.getCrimes().size() == 0;
		noCrimesText.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}

	private void updateUI() {
		final CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
		final List<Crime> crimes = crimeLab.getCrimes();

		if (adapter == null) {
			adapter = new CrimeAdapter(crimes);
			recyclerView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}

		updateSubtitle();
		showHideNoCrimeText(crimeLab);
	}

	private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private Crime crime;
		private TextView title;
		private TextView date;
		private CheckBox solved;

		public CrimeHolder(final View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			title = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
			date = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
			solved = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
		}

		public void bindCrime(final Crime crime) {
			this.crime = crime;
			title.setText(crime.getTitle());
			date.setText(crime.getDate().toString());
			solved.setChecked(crime.isSolved());
		}

		@Override
		public void onClick(final View v) {
			final Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
			startActivity(intent);
		}
	}

	public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
		private List<Crime> crimes;

		public CrimeAdapter(final List<Crime> crimes) {
			this.crimes = crimes;
		}

		@Override
		public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			final View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
			return new CrimeHolder(view);
		}

		@Override
		public void onBindViewHolder(final CrimeHolder holder, final int position) {
			holder.bindCrime(crimes.get(position));
		}

		@Override
		public int getItemCount() {
			return crimes.size();
		}
	}
}
