package com.bignerdranch.android.photogallery;

public class GalleryItem {
	private String caption;
	private String id;
	private String url_s;

	@Override
	public String toString() {
		return caption;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(final String caption) {
		this.caption = caption;
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
