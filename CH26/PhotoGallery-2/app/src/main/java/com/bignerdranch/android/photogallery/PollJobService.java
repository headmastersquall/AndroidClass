package com.bignerdranch.android.photogallery;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;

public class PollJobService extends JobService {

	public static final int JOB_ID = 0;

	private PollTask currentTask;
	private Context context;

	public void toggleJobService(final Context context) {
		this.context = context;
		final JobScheduler jobScheduler =
			(JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

		boolean isRunning = false;
		for (JobInfo job : jobScheduler.getAllPendingJobs()) {
			if (job.getId() == PollJobService.JOB_ID) {
				isRunning = true;
				break;
			}
		}

		if (isRunning) {
			jobScheduler.cancel(PollJobService.JOB_ID);
		} else {
			final JobInfo job = new JobInfo.Builder(
				PollJobService.JOB_ID, new ComponentName(context, PollJobService.class))
				.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
				.setPeriodic(1000 * 60 * 15)
				.setPersisted(true)
				.build();
			jobScheduler.schedule(job);
		}
	}


	@Override
	public boolean onStartJob(final JobParameters params) {
		currentTask = new PollTask();
		currentTask.execute(params);
		return false;
	}

	@Override
	public boolean onStopJob(final JobParameters params) {
		if (currentTask != null) {
			currentTask.cancel(true);
		}
		return true;
	}

	private class PollTask extends AsyncTask<JobParameters, Void, Void> {
		@Override
		protected Void doInBackground(final JobParameters... params) {
			final JobParameters jobParams = params[0];
			PollService.notifyIfNewImagesAreAvailable(context);
			jobFinished(jobParams, false);
			return null;
		}
	}
}
