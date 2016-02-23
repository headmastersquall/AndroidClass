package com.bignerdranch.android.beatbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
	private static final String TAG = "BeatBox";
	private static final String SOUNDS_FOLDER = "sample_sounds";

	private AssetManager assets;
	private List<Sound> sounds = new ArrayList<>();

	public BeatBox(final Context context) {
		this.assets = context.getAssets();
		loadSounds();
	}

	private void loadSounds() {
		try {
			final String[] soundNames = assets.list(SOUNDS_FOLDER);
			addSoundsToSoundList(soundNames);
			Log.i(TAG, "Found " + soundNames.length + " sounds");
		} catch (IOException ioe) {
			Log.e(TAG, "Could not load assets", ioe);
		}
	}

	private void addSoundsToSoundList(final String[] soundNames) {
		for(final String fileName : soundNames) {
			final String assetPath = SOUNDS_FOLDER + "/" + fileName;
			final Sound sound = new Sound(assetPath);
			sounds.add(sound);
		}
	}

	public List<Sound> getSounds() {
		return sounds;
	}
}
