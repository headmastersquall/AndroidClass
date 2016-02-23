package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
	private static final String PREF_SEARCH_QUERY = "searchQuery";

	public static String getStoredQuery(final Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getString(PREF_SEARCH_QUERY, "");
	}

	public static void setStoredQuery(final Context context, final String query) {
		PreferenceManager.getDefaultSharedPreferences(context)
			.edit()
			.putString(PREF_SEARCH_QUERY, query)
			.apply();
	}
}
