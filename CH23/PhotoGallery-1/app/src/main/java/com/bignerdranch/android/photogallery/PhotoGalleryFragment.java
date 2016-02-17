package com.bignerdranch.android.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

	private static final String TAG = "PhotoGalleryFragment";

	private RecyclerView photoRecyclerView;
	private List<GalleryItem> items = new ArrayList<>();

	public static PhotoGalleryFragment newInstance() {
		return new PhotoGalleryFragment();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		new FetchItemsTask().execute();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
		photoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
		photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
		setupAdapter();
		return v;
	}

	private void setupAdapter() {
		if (isAdded()) {
			photoRecyclerView.setAdapter(new PhotoAdapter(items));
		}
	}

	private class PhotoHolder extends RecyclerView.ViewHolder {
		private TextView titleTextView;

		public PhotoHolder(final View itemView) {
			super(itemView);
			titleTextView = (TextView)itemView;
		}

		public void bindGalleryItem(final GalleryItem item) {
			titleTextView.setText(item.toString());
		}
	}

	private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
		private List<GalleryItem> galleryItems;

		public PhotoAdapter(final List<GalleryItem> galleryItems) {
			this.galleryItems = galleryItems;
		}

		@Override
		public PhotoHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			return new PhotoHolder(new TextView(getActivity()));
		}

		@Override
		public void onBindViewHolder(final PhotoHolder holder, final int position) {
			holder.bindGalleryItem(galleryItems.get(position));
		}

		@Override
		public int getItemCount() {
			return galleryItems.size();
		}
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
		@Override
		protected List<GalleryItem> doInBackground(final Void... params) {
			return new FlickerFetchr().fetchItems();
		}

		@Override
		protected void onPostExecute(final List<GalleryItem> galleryItems) {
			PhotoGalleryFragment.this.items = galleryItems;
			setupAdapter();
		}
	}
}
