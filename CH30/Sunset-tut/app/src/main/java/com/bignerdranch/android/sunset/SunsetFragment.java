package com.bignerdranch.android.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class SunsetFragment extends Fragment {

	private View sceneView;
	private View sunView;
	private View skyView;

	private int blueSkyColor;
	private int sunsetSkyColor;
	private int nightSkyColor;

	public static Fragment newInstance() {
		return new SunsetFragment();
	}

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_sunset, container, false);
		sceneView = view;
		sunView = view.findViewById(R.id.sun);
		skyView = view.findViewById(R.id.sky);

		final Resources resources = getResources();
		blueSkyColor = resources.getColor(R.color.blue_sky);
		sunsetSkyColor = resources.getColor(R.color.sunset_sky);
		nightSkyColor = resources.getColor(R.color.night_sky);

		sceneView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				startAnimation();
			}
		});
		return view;
	}

	private void startAnimation() {
		final float sunYStart = sunView.getTop();
		final float sunYEnd = skyView.getHeight();

		final ObjectAnimator heightAnimator = ObjectAnimator
			.ofFloat(sunView, "y", sunYStart, sunYEnd)
			.setDuration(3000);
		heightAnimator.setInterpolator(new AccelerateInterpolator());

		final ObjectAnimator sunsetSkyAnimator = ObjectAnimator
			.ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
			.setDuration(3000);
		sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

		final ObjectAnimator nightSkyAnimator = ObjectAnimator
			.ofInt(skyView, "backgroundColor", sunsetSkyColor, nightSkyColor)
			.setDuration(1500);
		nightSkyAnimator.setEvaluator(new ArgbEvaluator());

		final AnimatorSet animatorSet = new AnimatorSet();
		animatorSet
			.play(heightAnimator)
			.with(sunsetSkyAnimator)
			.before(nightSkyAnimator);
		animatorSet.start();
	}
}