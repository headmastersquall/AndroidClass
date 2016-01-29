package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class DetailImageDialogFragment extends DialogFragment {

	public static final String ARG_IMAGE = "image";

	public static DetailImageDialogFragment createInstance(final String imageName) {
		final Bundle args = new Bundle();
		args.putString(ARG_IMAGE, imageName);
		final DetailImageDialogFragment fragment = new DetailImageDialogFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final String imageName = getArguments().getString(ARG_IMAGE);
		final View v = LayoutInflater.from(getActivity()).inflate(
			R.layout.detail_image_dialog, null);
		final ImageView imageView = (ImageView)v.findViewById(R.id.detail_image_view);
		imageView.setImageBitmap(PictureUtils.getScaledBitmap(imageName, getActivity()));
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle("Image details")
			.setPositiveButton(android.R.string.ok,  new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
				}
			}).create();
	}
}
