package com.bignerdranch.android.photogallery;

public class GalleryItem {
	private String title;
	private String id;
	private String url_s;

	@Override
	public String toString() {
		return title;
	}

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
}
