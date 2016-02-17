package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;

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

	public List<GalleryItem> fetchItems() {
		final List<GalleryItem> items = new ArrayList<>();

		try {
			final String url = Uri.parse(FLICR_REST_API_URL)
				.buildUpon()
				.appendQueryParameter("method", "flickr.photos.getRecent")
				.appendQueryParameter("api_key", API_KEY)
				.appendQueryParameter("format", "json")
				.appendQueryParameter("nojsoncallback", "1")
				.appendQueryParameter("extras", "url_s")
				.build()
				.toString();
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

	private void parseItems(final List<GalleryItem> items, JSONObject jsonBody)
		throws IOException, JSONException {
		final JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
		final JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");

		for (int i = 0; i< photosJsonArray.length(); i++) {
			final JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);

			final GalleryItem item = new GalleryItem();
			item.setId(photoJsonObject.getString("id"));
			item.setCaption(photoJsonObject.getString("title"));

			if (!photoJsonObject.has("url_s")) {
				continue;
			}

			item.setUrl(photoJsonObject.getString("url_s"));
			items.add(item);
		}
	}
}
