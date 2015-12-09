package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {

	private CrimeAdapter adapter;
	private RecyclerView recyclerView;
	private Crime clickedCrime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
		View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
		recyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		updateUI();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateUI();
	}

	private void updateUI() {
		CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
		List<Crime> crimes = crimeLab.getCrimes();

		if (adapter == null) {
			adapter = new CrimeAdapter(crimes);
			recyclerView.setAdapter(adapter);
		} else {
			adapter.notifyItemChanged(crimes.indexOf(clickedCrime));
		}
	}

	private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private Crime crime;
		private TextView title;
		private TextView date;
		private CheckBox solved;

		public CrimeHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			title = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
			date = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
			solved = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
		}

		public void bindCrime(Crime crime) {
			this.crime = crime;
			title.setText(crime.getTitle());
			date.setText(crime.getDate().toString());
			solved.setChecked(crime.isSolved());
		}

		@Override
		public void onClick(View v) {
			clickedCrime = crime;
			Intent intent = CrimeActivity.newIntent(getActivity(), crime.getId());
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
			LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
			return new CrimeHolder(view);
		}

		@Override
		public void onBindViewHolder(CrimeHolder holder, int position) {
			holder.bindCrime(crimes.get(position));
		}

		@Override
		public int getItemCount() {
			return crimes.size();
		}
	}
}
