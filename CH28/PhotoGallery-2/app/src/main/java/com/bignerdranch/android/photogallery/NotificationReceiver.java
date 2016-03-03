package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
	private static final String TAG = "NotificationReceiver";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		Log.i(TAG, "received result: " + getResultCode());
		if (getResultCode() != Activity.RESULT_OK) {
			// A foreground activity cancelled the broadcast
			return;
		}

		final int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
		final Notification notification = (Notification)intent
			.getParcelableExtra(PollService.NOTIFICATION);

		final NotificationManagerCompat notificationManager =
			NotificationManagerCompat.from(context);
		notificationManager.notify(requestCode, notification);
	}
}
