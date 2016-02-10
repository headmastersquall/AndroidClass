package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

	private static final String TAG = "NerdLauncherFragment";

	private RecyclerView recyclerView;

	public static NerdLauncherFragment newInstance() {
		return new NerdLauncherFragment();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
		recyclerView = (RecyclerView)v.findViewById(R.id.fragment_nerd_launcher_recycler_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		setupAdapter();
		return v;
	}

	private void setupAdapter() {
		final Intent startupIntent = new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final PackageManager pm = getActivity().getPackageManager();
		final List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
		Collections.sort(activities, new Comparator<ResolveInfo>() {
			@Override
			public int compare(final ResolveInfo lhs, final ResolveInfo rhs) {
				final PackageManager pm = getActivity().getPackageManager();
				return String.CASE_INSENSITIVE_ORDER.compare(
					lhs.loadLabel(pm).toString(),
					rhs.loadLabel(pm).toString());
			}
		});
		Log.i(TAG, "Found " + activities.size() + " activities.");
		recyclerView.setAdapter(new ActivityAdapter(activities));
	}

	private class ActivityHolder extends RecyclerView.ViewHolder
		implements View.OnClickListener {

		private ResolveInfo resolveInfo;
		private TextView nameTextView;

		public ActivityHolder(final View itemView) {
			super(itemView);
			nameTextView = (TextView) itemView;
			nameTextView.setOnClickListener(this);
		}

		public void bindActivity(final ResolveInfo resolveInfo) {
			this.resolveInfo = resolveInfo;
			final PackageManager pm = getActivity().getPackageManager();
			final String appName = this.resolveInfo.loadLabel(pm).toString();
			nameTextView.setText(appName);
		}

		@Override
		public void onClick(final View v) {
			final ActivityInfo activityInfo = resolveInfo.activityInfo;
			final Intent i = new Intent(Intent.ACTION_MAIN)
				.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
	}

	private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
		private final List<ResolveInfo> activities;

		public ActivityAdapter(final List<ResolveInfo> activities) {
			this.activities = activities;
		}

		@Override
		public ActivityHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
			final View view = layoutInflater.inflate(
				android.R.layout.simple_list_item_1, parent, false);
			return new ActivityHolder(view);
		}

		@Override
		public void onBindViewHolder(final ActivityHolder holder, final int position) {
			final ResolveInfo resolveInfo = activities.get(position);
			holder.bindActivity(resolveInfo);
		}

		@Override
		public int getItemCount() {
			return activities.size();
		}
	}
}
