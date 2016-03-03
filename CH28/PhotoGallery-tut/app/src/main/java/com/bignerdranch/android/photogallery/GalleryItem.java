package com.bignerdranch.android.photogallery;

import android.net.Uri;

public class GalleryItem {
	private String title;
	private String id;
	private String url_s;
	private String owner;

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getUrl() {
		return url_s;
	}

	public void setUrl(final String url) {
		this.url_s = url;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(final String owner) {
		this.owner = owner;
	}

	public Uri getPhotoPageUri() {
		return Uri.parse("http://www.flickr.com/photos/")
			.buildUpon()
			.appendPath(owner)
			.appendPath(id)
			.build();
	}

	@Override
	public String toString() {
		return title;
	}
}
