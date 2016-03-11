package com.bignerdranch.android.locatr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

public class LocatrFragment extends Fragment {
	private static final String TAG = "LocatrFragment";

	private ImageView imageView;
	private GoogleApiClient client;

	public static LocatrFragment newInstance() {
		return new LocatrFragment();
	}

	@Override
	public void onCreate(final @Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		client = new GoogleApiClient.Builder(getActivity())
			.addApi(LocationServices.API)
			.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
				@Override
				public void onConnected(final Bundle bundle) {
					getActivity().invalidateOptionsMenu();
				}

				@Override
				public void onConnectionSuspended(final int i) {

				}
			})
			.build();
	}

	@Nullable
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_locatr, container, false);
		imageView = (ImageView) v.findViewById(R.id.image);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();

		getActivity().invalidateOptionsMenu();
		client.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		client.disconnect();
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_locatr, menu);

		final MenuItem searchItem = menu.findItem(R.id.action_locate);
		searchItem.setEnabled(client.isConnected());
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_locate:
				findImage();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void findImage() {
		final LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setNumUpdates(1);
		request.setInterval(0);

		LocationServices.FusedLocationApi
			.requestLocationUpdates(client, request, new LocationListener() {
				@Override
				public void onLocationChanged(final Location location) {
					Log.i(TAG, "Got a fix: " + location);
					new SearchTask().execute(location);
				}
			});
	}

	private class SearchTask extends AsyncTask<Location, Void, Void> {
		private GalleryItem galleryItem;
		private Bitmap bitmap;

		@Override
		protected Void doInBackground(Location... params) {
			final FlickerFetchr fetchr = new FlickerFetchr();
			List<GalleryItem> items = fetchr.searchPhotos(params[0]);

			if (items.size() == 0) {
				return null;
			}

			galleryItem = items.get(0);

			try {
				byte[] bytes = fetchr.getUrlBytes(galleryItem.getUrl());
				bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			} catch (IOException ioe) {
				Log.i(TAG, "Unable to download bitmap", ioe);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			imageView.setImageBitmap(bitmap);
		}
	}
}
