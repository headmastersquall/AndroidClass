package com.bignerdranch.android.geoquiz;

public class Question {

	private int textResourceId;
	private boolean answer;

	public Question(int textResourceId, boolean answer) {
		this.textResourceId = textResourceId;
		this.answer = answer;
	}

	public boolean isAnswer() {
		return answer;
	}

	public int getTextResourceId() {
		return textResourceId;
	}
}
