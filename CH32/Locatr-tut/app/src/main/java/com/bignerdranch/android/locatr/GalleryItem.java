package com.bignerdranch.android.locatr;

import android.net.Uri;

public class GalleryItem {
	private String title;
	private String id;
	private String url_s;
	private String owner;
	private double latitude;
	private double longitude;

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

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(final double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
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
