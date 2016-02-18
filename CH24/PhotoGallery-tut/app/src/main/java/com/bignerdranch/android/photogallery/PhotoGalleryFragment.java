package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

	private static final String TAG = "PhotoGalleryFragment";

	private FlickerFetchr flickerFetchr = new FlickerFetchr();
	private RecyclerView photoRecyclerView;
	private List<GalleryItem> items = new ArrayList<>();
	private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
	private Boolean isLoading = false;
	private int currentPage = 1;

	public static PhotoGalleryFragment newInstance() {
		return new PhotoGalleryFragment();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		loadItems();

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

	private void loadItems() {
		new FetchItemsTask().execute();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
		photoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
		photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
		photoRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
				super.onScrolled(recyclerView, dx, dy);

				final GridLayoutManager layoutManager = (GridLayoutManager)recyclerView.getLayoutManager();
				int visibleItemCount =  layoutManager.getChildCount();
				int totalItemCount = layoutManager.getItemCount();
				int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

				if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
					&& firstVisibleItemPosition >= 0
					&& currentPage < flickerFetchr.getPages()) {
					Log.i(TAG, "Loading next page");
					currentPage += 1;
					loadItems();
				}

			}
		});
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

	private void setupAdapter() {
		if (isAdded()) {
			photoRecyclerView.setAdapter(new PhotoAdapter(items));
		}
	}

	private class PhotoHolder extends RecyclerView.ViewHolder {
		private ImageView imageView;

		public PhotoHolder(final View itemView) {
			super(itemView);
			imageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
		}

		public void bindDrawable(final Drawable drawable) {
			imageView.setImageDrawable(drawable);
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
			holder.bindDrawable(placeHolder);
			thumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
		}

		@Override
		public int getItemCount() {
			return galleryItems.size();
		}
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
		@Override
		protected List<GalleryItem> doInBackground(final Void... params) {
			isLoading = true;
			return flickerFetchr.fetchItems(currentPage);
		}

		@Override
		protected void onPostExecute(final List<GalleryItem> galleryItems) {
			PhotoGalleryFragment.this.items = galleryItems;
			setupAdapter();
			isLoading = false;
		}
	}
}
