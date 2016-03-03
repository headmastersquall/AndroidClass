package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

	private Fragment fragment;

	public static Intent newIntent(final Context context, final Uri photoPageUri) {
		final Intent i = new Intent(context, PhotoPageActivity.class);
		i.setData(photoPageUri);
		return i;
	}

	@Override
	protected Fragment createFragment() {
		fragment = PhotoPageFragment.newInstance(getIntent().getData());
		return fragment;
	}

	@Override
	public void onBackPressed() {
		if (fragment instanceof onBackPressedCallback &&
			((onBackPressedCallback) fragment).handleBackPressed()) {
			return;
		}
		super.onBackPressed();
	}

	public interface onBackPressedCallback {
		/**
		 * Returns true if the target fragment handled the back event.
		 * @return
		 */
		boolean handleBackPressed();
	}
}
