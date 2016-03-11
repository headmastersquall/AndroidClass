package com.bignerdranch.android.locatr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class LocatrFragment extends SupportMapFragment {
	private static final String TAG = "LocatrFragment";

	private GoogleApiClient client;
	private GoogleMap map;
	private Bitmap mapImage;
	private GalleryItem mapItem;
	private Location currentLocation;

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

		getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(final GoogleMap googleMap) {
				map = googleMap;
				updateUI();
			}
		});
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

	private void updateUI() {
		if (map == null || mapImage == null) {
			return;
		}

		final LatLng itemPoint = new LatLng(mapItem.getLatitude(), mapItem.getLongitude());
		final LatLng myPoint = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

		final BitmapDescriptor itemBitmap = BitmapDescriptorFactory.fromBitmap(mapImage);
		final MarkerOptions itemMarker = new MarkerOptions()
			.position(itemPoint)
			.icon(itemBitmap);
		final MarkerOptions myMarker = new MarkerOptions()
			.position(myPoint);

		map.clear();
		map.addMarker(itemMarker);
		map.addMarker(myMarker);

		final LatLngBounds bounds = LatLngBounds.builder()
			.include(itemPoint)
			.include(myPoint)
			.build();

		final int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
		final CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
		map.animateCamera(update);
	}

	private class SearchTask extends AsyncTask<Location, Void, Void> {
		private GalleryItem galleryItem;
		private Bitmap bitmap;
		private Location location;

		@Override
		protected Void doInBackground(Location... params) {
			location = params[0];
			final FlickerFetchr fetchr = new FlickerFetchr();
			List<GalleryItem> items = fetchr.searchPhotos(location);

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
			mapImage = bitmap;
			mapItem = galleryItem;
			currentLocation = location;
			updateUI();
		}
	}
}
