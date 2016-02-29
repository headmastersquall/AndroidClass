package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

public class VisibleFragment extends Fragment {
	private static final String TAG = "VisibleFragment";

	@Override
	public void onStart() {
		super.onStart();
		final IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
		getActivity().registerReceiver(onShowNotification, filter, PollService.PERM_PRIVATE, null);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(onShowNotification);
	}

	private BroadcastReceiver onShowNotification = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			// If we receive this, we're visible, so cancel the notification
			Log.i(TAG, "canceling notification");
			setResultCode(Activity.RESULT_CANCELED);
		}
	};
}
