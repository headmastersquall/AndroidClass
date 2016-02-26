package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {
	private static final String PREF_SEARCH_QUERY = "searchQuery";
	private static final String PREF_LAST_RESULT_ID = "lastResultId";

	public static String getStoredQuery(final Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getString(PREF_SEARCH_QUERY, "");
	}

	public static void setStoredQuery(final Context context, final String query) {
		setValue(context, PREF_SEARCH_QUERY, query);
	}

	public static String getLastResultId(final Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
			.getString(PREF_LAST_RESULT_ID, "");
	}

	public static void setPrefLastResultId(final Context context, final String resultId) {
		setValue(context, PREF_LAST_RESULT_ID, resultId);
	}

	private static void setValue(final Context context, final String key, final String value) {
		PreferenceManager.getDefaultSharedPreferences(context)
			.edit()
			.putString(key, value)
			.apply();
	}
}
