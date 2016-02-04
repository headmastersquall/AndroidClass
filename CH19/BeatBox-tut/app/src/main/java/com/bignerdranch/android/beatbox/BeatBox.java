package com.bignerdranch.android.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
	private static final String TAG = "BeatBox";
	private static final String SOUNDS_FOLDER = "sample_sounds";
	private static final int MAX_SOUNDS = 5;

	private AssetManager assets;
	private List<Sound> sounds = new ArrayList<>();
	private SoundPool soundPool;

	public BeatBox(final Context context) {
		this.assets = context.getAssets();
		// This old constructor is deprecated, but we need it for compatibility
		soundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
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
		for (final String fileName : soundNames) {
			tryToAddSoundToList(fileName);
		}
	}

	private void tryToAddSoundToList(final String fileName) {
		try {
			final String assetPath = SOUNDS_FOLDER + "/" + fileName;
			final Sound sound = new Sound(assetPath);
			load(sound);
			sounds.add(sound);
		} catch(IOException ioe) {
			Log.e(TAG, "Could not load sound " + fileName, ioe);
		}
	}

	private void load(final Sound sound) throws IOException {
		final AssetFileDescriptor afd = assets.openFd(sound.getAssetPath());
		final int soundId = soundPool.load(afd, 1);
		sound.setSoundId(soundId);
	}

	public void play(final Sound sound) {
		if (sound.getSoundId() != null) {
			soundPool.play(sound.getSoundId(), 1.0f, 1.0f ,1, 0, 1.0f);
		}
	}

	public void release() {
		soundPool.release();
	}

	public List<Sound> getSounds() {
		return sounds;
	}
}
