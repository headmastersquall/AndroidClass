package com.bignerdranch.android.geoquiz;

final class Question {

	private final int textResourceId;
	private final boolean answer;

	public Question(final int textResourceId, final boolean answer) {
		this.textResourceId = textResourceId;
		this.answer = answer;
	}

	public boolean isAnswerTrue() {
		return answer;
	}

	public int getTextResourceId() {
		return textResourceId;
	}
}
