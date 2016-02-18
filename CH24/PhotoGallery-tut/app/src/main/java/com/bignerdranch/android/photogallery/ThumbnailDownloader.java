package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {
	private static final String TAG = "ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD = 0;

	private Handler requestHandler;
	private ConcurrentMap<T, String> requestMap = new ConcurrentHashMap<>();
	private Handler responseHandler;
	private ThumbnailDownloadListener<T> thumbnailDownloadListener;

	public interface ThumbnailDownloadListener<T> {
		void onThumbnailDownloaded(final T target, final Bitmap thumbnail);
	}

	public void setThumbnailDownloadListener(final ThumbnailDownloadListener<T> listener) {
		this.thumbnailDownloadListener = listener;
	}

	public ThumbnailDownloader(final Handler responseHandler) {
		super(TAG);
		this.responseHandler = responseHandler;
	}

	@Override
	protected void onLooperPrepared() {
		requestHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				if (msg.what == MESSAGE_DOWNLOAD) {
					final T target = (T) msg.obj;
					Log.i(TAG, "Got a request for URL: " + requestMap.get(target));
					handleRequest(target);
				}
			}
		};
	}

	public void queueThumbnail(final T target, final String url) {
		Log.i(TAG, "Got a URL: " + url);

		if (url == null) {
			requestMap.remove(target);
		} else {
			requestMap.put(target, url);
			requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
		}
	}

	public void clearQueue() {
		requestHandler.removeMessages(MESSAGE_DOWNLOAD);
	}

	private void handleRequest(final T target) {
		try {
			final String url = requestMap.get(target);

			if (url == null) {
				return;
			}

			final byte[] bitmapBytes = new FlickerFetchr().getUrlBytes(url);
			final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			Log.i(TAG, "Bitmap created");

			responseHandler.post(new Runnable() {
				@Override
				public void run() {
					if (requestMap.get(target).equals(url)) {
						requestMap.remove(target);
						thumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
					}
				}
			});
		} catch (IOException ioe) {
			Log.e(TAG, "Error downloading image", ioe);
		}
	}
}
