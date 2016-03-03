package com.bignerdranch.android.photogallery;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class PhotoPageFragment extends VisibleFragment {
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
			public boolean shouldOverrideUrlLoading(final WebView wview, final String url) {
				return false;
			}
		});
		webView.loadUrl(uri.toString());
		return view;
	}
}
