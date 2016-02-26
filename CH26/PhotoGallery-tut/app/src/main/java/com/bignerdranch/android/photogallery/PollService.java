package com.bignerdranch.android.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

public class PollService extends IntentService {
	private static final String TAG = "PollService";
	private static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

	public static Intent newIntent(final Context context) {
		return new Intent(context, PollService.class);
	}

	public PollService() {
		super(TAG);
	}

	public static void setServiceAlarm(final Context context, final boolean isOn) {
		final Intent i = PollService.newIntent(context);
		final PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

		final AlarmManager alarmManager =
			(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		if (isOn) {
			alarmManager.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime(),
				POLL_INTERVAL,
				pi);
		} else {
			alarmManager.cancel(pi);
			pi.cancel();
		}
	}

	public static boolean isServiceAlarmOn(final Context context) {
		final Intent i = PollService.newIntent(context);
		final PendingIntent pi =
			PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		if (!isNetworkAvailableAndConnected()) {
			return;
		}
		final String query = QueryPreferences.getStoredQuery(this);
		final String lastResultId = QueryPreferences.getLastResultId(this);
		final List<GalleryItem> items =
			query.length() == 0
				? new FlickerFetchr().fetchRecentPhotos()
				: new FlickerFetchr().searchPhotos(query);

		if (items.size() == 0) {
			return;
		}

		final String resultId = items.get(0).getId();
		if (resultId.equals(lastResultId)) {
			Log.i(TAG, "Got an old result: " + resultId);
		} else {
			Log.i(TAG, "Got a new result: " + resultId);

			final Resources resources = getResources();
			final Intent i = PhotoGalleryActivity.newIntent(this);
			final PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

			final Notification notification = new NotificationCompat.Builder(this)
				.setTicker(resources.getString(R.string.new_pictures_title))
				.setSmallIcon(android.R.drawable.ic_menu_report_image)
				.setContentTitle(resources.getString(R.string.new_pictures_title))
				.setContentText(resources.getString(R.string.new_pictures_text))
				.setContentIntent(pi)
				.setAutoCancel(true)
				.build();

			final NotificationManagerCompat notificationManager =
				NotificationManagerCompat.from(this);
			notificationManager.notify(0, notification);
		}

		QueryPreferences.setPrefLastResultId(this, resultId);
	}

	private boolean isNetworkAvailableAndConnected() {
		final ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		final boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
		return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
	}
}
