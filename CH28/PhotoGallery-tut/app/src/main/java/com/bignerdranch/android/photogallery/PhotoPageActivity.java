package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

	public static Intent newIntent(final Context context, final Uri photoPageUri) {
		final Intent i = new Intent(context, PhotoPageActivity.class);
		i.setData(photoPageUri);
		return i;
	}

	@Override
	protected Fragment createFragment() {
		return PhotoPageFragment.newInstance(getIntent().getData());
	}
}
