package com.bignerdranch.android.beatbox;

public class Sound {
	private final String assetPath;
	private String name;
	private Integer soundId;

	public Sound(final String assetPath) {
		this.assetPath = assetPath;
		final String[] components = assetPath.split("/");
		final String fileName = components[components.length - 1];
		name = fileName.replace(".wav", "");
	}

	public String getAssetPath() {
		return assetPath;
	}

	public String getName() {
		return name;
	}

	public Integer getSoundId() {
		return soundId;
	}

	public void setSoundId(final Integer id) {
		this.soundId = id;
	}
}
