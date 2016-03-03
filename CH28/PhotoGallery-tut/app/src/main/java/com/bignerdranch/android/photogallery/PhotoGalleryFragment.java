package com.bignerdranch.android.photogallery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends VisibleFragment {

	private static final String TAG = "PhotoGalleryFragment";

	private RecyclerView photoRecyclerView;
	private List<GalleryItem> items = new ArrayList<>();
	private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
	private ProgressDialog progressDialog;

	public static PhotoGalleryFragment newInstance() {
		return new PhotoGalleryFragment();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		updateItems();

		progressDialog = new ProgressDialog(getContext());
		final Handler responseHandler = new Handler();
		thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
		thumbnailDownloader.setThumbnailDownloadListener(
			new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
				@Override
				public void onThumbnailDownloaded(final PhotoHolder photoHolder, final Bitmap thumbnail) {
					final Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
					photoHolder.bindDrawable(drawable);
				}
			}
		);
		thumbnailDownloader.start();
		thumbnailDownloader.getLooper();
		Log.i(TAG, "Background thread started");
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
		photoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
		photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

		setupAdapter();
		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		thumbnailDownloader.clearQueue();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		thumbnailDownloader.quit();
		Log.i(TAG, "Background thread destroyed");
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_photo_gallery, menu);

		final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
		final SearchView searchView = (SearchView) searchItem.getActionView();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(final String query) {
				Log.d(TAG, "QueryTextSubmit: " + query);
				QueryPreferences.setStoredQuery(getActivity(), query);
				searchView.setVisibility(View.INVISIBLE);
				updateItems();
				progressDialog.setMessage("Loading...");
				progressDialog.setCancelable(false);
				progressDialog.show();
				return true;
			}

			@Override
			public boolean onQueryTextChange(final String newText) {
				Log.d(TAG, "QueryTextChange: " + newText);
				return false;
			}
		});

		searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final String query = QueryPreferences.getStoredQuery(getActivity());
				searchView.setQuery(query, false);
			}
		});

		final MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
		if (PollService.isServiceAlarmOn(getActivity())) {
			toggleItem.setTitle(R.string.stop_polling);
		} else {
			toggleItem.setTitle(R.string.start_polling);
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_clear:
				QueryPreferences.setStoredQuery(getActivity(), "");
				updateItems();
				return true;
			case R.id.menu_item_toggle_polling:
				boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
				PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
				getActivity().invalidateOptionsMenu();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void updateItems() {
		final String query = QueryPreferences.getStoredQuery(getActivity());
		new FetchItemsTask(query).execute();
	}

	private void setupAdapter() {
		if (isAdded()) {
			photoRecyclerView.setAdapter(new PhotoAdapter(items));
		}
	}

	private class PhotoHolder extends RecyclerView.ViewHolder
		implements View.OnClickListener
	{
		private ImageView imageView;
		private GalleryItem galleryItem;

		public PhotoHolder(final View itemView) {
			super(itemView);
			imageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
			itemView.setOnClickListener(this);
		}

		public void bindDrawable(final Drawable drawable) {
			imageView.setImageDrawable(drawable);
		}

		public void bindGalleryItem(final GalleryItem galleryItem) {
			this.galleryItem = galleryItem;
		}

		@Override
		public void onClick(final View v) {
			final Intent i = PhotoPageActivity.newIntent(
				getActivity(),
				galleryItem.getPhotoPageUri());
			startActivity(i);
		}
	}

	private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
		private List<GalleryItem> galleryItems;

		public PhotoAdapter(final List<GalleryItem> galleryItems) {
			this.galleryItems = galleryItems;
		}

		@Override
		public PhotoHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			final LayoutInflater inflater = LayoutInflater.from(getActivity());
			final View view = inflater.inflate(R.layout.gallery_item, parent, false);
			return new PhotoHolder(view);
		}

		@Override
		public void onBindViewHolder(final PhotoHolder holder, final int position) {
			final GalleryItem galleryItem = galleryItems.get(position);
			final Drawable placeHolder = getResources().getDrawable(R.drawable.bill_up_close);
			holder.bindGalleryItem(galleryItem);
			holder.bindDrawable(placeHolder);
			thumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
		}

		@Override
		public int getItemCount() {
			return galleryItems.size();
		}
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

		private final String query;

		public FetchItemsTask(final String query) {
			this.query = query;
		}

		@Override
		protected List<GalleryItem> doInBackground(final Void... params) {
			return query.length() == 0
				? new FlickerFetchr().fetchRecentPhotos()
				: new FlickerFetchr().searchPhotos(query);
		}

		@Override
		protected void onPostExecute(final List<GalleryItem> galleryItems) {
			PhotoGalleryFragment.this.items = galleryItems;
			setupAdapter();
			progressDialog.hide();
		}
	}
}
