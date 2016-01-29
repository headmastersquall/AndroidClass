package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
	implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

	@Override
	protected Fragment createFragment() {
		return new CrimeListFragment();
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_masterdetail;
	}

	@Override
	public void onCrimeSelected(final Crime crime) {
		if (findViewById(R.id.detail_fragment_container) == null) {
			final Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
			startActivity(intent);
		} else {
			final Fragment newDetail = CrimeFragment.newInstance(crime.getId());
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.detail_fragment_container, newDetail)
				.commit();
		}
	}

	@Override
	public void onCrimeUpdated(final Crime crime) {
		final CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
			.findFragmentById(R.id.fragment_container);
		listFragment.updateUI();
	}
}
