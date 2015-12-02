package com.bignerdranch.android.criminalintent;

import java.util.UUID;

public class Crime {
	private final UUID id;
	private String title;

	public Crime() {
		id = UUID.randomUUID();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UUID getId() {
		return id;
	}
}
