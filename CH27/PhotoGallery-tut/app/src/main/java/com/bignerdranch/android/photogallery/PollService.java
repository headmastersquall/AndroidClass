package com.bignerdranch.android.photogallery;

import android.app.Activity;
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
//	private static final long POLL_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
	private static final long POLL_INTERVAL = 1000 * 60;

	public static final String ACTION_SHOW_NOTIFICATION =
		"com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
	public static final String PERM_PRIVATE =
		"com.bignerdranch.android.photogallery.PRIVATE";
	public static final String REQUEST_CODE = "REQUEST_CODE";
	public static final String NOTIFICATION = "NOTIFICATION";

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

		QueryPreferences.setAlarmOn(context, isOn);
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

			showBackgroundNotification(0, notification);
		}

		QueryPreferences.setPrefLastResultId(this, resultId);
	}

	private void showBackgroundNotification(final int requestCode, final Notification notification) {
		final Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
		i.putExtra(REQUEST_CODE, requestCode);
		i.putExtra(NOTIFICATION, notification);
		sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
	}

	private boolean isNetworkAvailableAndConnected() {
		final ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		final boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
		return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
	}
}
