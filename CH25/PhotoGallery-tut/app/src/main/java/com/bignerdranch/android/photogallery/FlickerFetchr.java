package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickerFetchr {

	private static final String TAG = "FlickrFetchr";
	private static final String API_KEY = "5e8435506073f8458ff95af77944379d";
	private static final String FLICR_REST_API_URL = "https://api.flickr.com/services/rest/";
	private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
	private static final String SEARCH_METHOD = "flickr.photos.search";
	private static final Uri ENDPOINT = Uri
		.parse(FLICR_REST_API_URL)
		.buildUpon()
		.appendQueryParameter("api_key", API_KEY)
		.appendQueryParameter("format", "json")
		.appendQueryParameter("nojsoncallback", "1")
		.appendQueryParameter("extras", "url_s")
		.build();

	private int currentPage = 0;
	private int pages = 0;
	private int perPage = 0;
	private int totalItems = 0;

	public byte[] getUrlBytes(final String urlSpec) throws IOException {
		final URL url = new URL(urlSpec);
		final HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		try {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final InputStream in = connection.getInputStream();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
			}

			int bytesRead = 0;
			final byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	public String getUrlString(final String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}

	public List<GalleryItem> fetchRecentPhotos() {
		final String url = buildUrl(FETCH_RECENTS_METHOD, "");
		return downloadGalleryItems(url);
	}

	public List<GalleryItem> searchPhotos(final String query) {
		final String url = buildUrl(SEARCH_METHOD, query);
		return downloadGalleryItems(url);
	}

	private List<GalleryItem> downloadGalleryItems(final String url) {
		final List<GalleryItem> items = new ArrayList<>();

		try {
			final String jsonString = getUrlString(url);
			Log.i(TAG, "Received JSOn: " + jsonString);
			final JSONObject jsonBody = new JSONObject(jsonString);
			parseItems(items, jsonBody);
		} catch (JSONException je) {
			Log.e(TAG, "Failed to parse JSO", je);
		} catch (IOException ioe) {
			Log.e(TAG, "Failed to fetch items", ioe);
		}

		Log.i(TAG, items.size() + " GalleryItems parsed.");
		return items;
	}

	private String buildUrl(final String method, final String query) {
		Uri.Builder uriBuilder = ENDPOINT.buildUpon()
			.appendQueryParameter("method", method);

		if (method.equals(SEARCH_METHOD)) {
			uriBuilder.appendQueryParameter("text", query);
		}
		return uriBuilder.toString();
	}

	private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {

		final Gson gson = new Gson();
		final JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
		final JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

		currentPage = photosJsonObject.getInt("page");
		pages = photosJsonObject.getInt("pages");
		perPage = photosJsonObject.getInt("perpage");
		totalItems = photosJsonObject.getInt("total");

		for (int i = 0; i< photosJsonArray.length(); i++) {
			final GalleryItem item = gson.fromJson(photosJsonArray.getString(i), GalleryItem.class);
			if (item.getUrl() != null) {
				items.add(item);
			}
		}
	}

	/**
	 * Gets the number of pages available.
	 * @return
	 */
	public int getPages() {
		return pages;
	}

	/**
	 * Gets the page number of the most recent page that was loaded.
	 * @return
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Get the amount of items that appear per page.
	 * @return
	 */
	public int getPerPage() {
		return perPage;
	}

	/**
	 * Gets the total number of items that can be retrieved.
	 * @return
	 */
	public int getTotalItems() {
		return totalItems;
	}
}
