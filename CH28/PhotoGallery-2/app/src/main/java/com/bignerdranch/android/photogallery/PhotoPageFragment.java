package com.bignerdranch.android.photogallery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PhotoPageFragment extends VisibleFragment
	implements PhotoPageActivity.onBackPressedCallback {

	private static final String TAG = "PhotoPageFragment";
	private static final String ARG_URI = "photo_page_url";

	private Uri uri;
	private WebView webView;
	private ProgressBar progressBar;

	public static PhotoPageFragment newInstance(final Uri uri) {
		final Bundle args = new Bundle();
		args.putParcelable(ARG_URI, uri);

		final PhotoPageFragment fragment = new PhotoPageFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uri = getArguments().getParcelable(ARG_URI);
	}

	@Nullable
	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_photo_page, container, false);
		progressBar = (ProgressBar) view.findViewById(R.id.fragment_photo_page_progress_bar);
		progressBar.setMax(100); // WebChromeClient reports in range 0-100

		webView = (WebView) view.findViewById(R.id.fragment_photo_page_web_view);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(final WebView view, final int newProgress) {
				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				} else {
					progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(newProgress);
				}
			}

			@Override
			public void onReceivedTitle(final WebView view, final String title) {
				final AppCompatActivity activity = (AppCompatActivity) getActivity();
				activity.getSupportActionBar().setSubtitle(title);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(final WebView wview, final String url) {
				final Uri uri = Uri.parse(url);
				if (uri.getScheme().equals("http") || uri.getScheme().equals("https")) {
					wview.loadUrl(url);
					return false;
				} else {
					Log.i(TAG, "Launching external uri: " + uri.toString());
					final Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(uri);
					startActivity(intent);
					return true;
				}
			}
		});
		webView.loadUrl(uri.toString());
		return view;
	}

	@Override
	public boolean handleBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return false;
	}
}
