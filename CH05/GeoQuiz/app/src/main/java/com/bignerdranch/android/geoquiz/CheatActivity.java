package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

	private static final String CHEAT_INDEX = "cheat";
	private static final String EXTRA_ANSWER = "com.bignerdranch.android.geoquiz.answer";
	private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";

	private Boolean answerShown = false;

	public static Intent newIntent(Context packageContext, boolean answer) {
		Intent i = new Intent(packageContext, CheatActivity.class);
		i.putExtra(EXTRA_ANSWER, answer);
		return i;
	}

	public static boolean wasAnswerShown(Intent result) {
		return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			answerShown = savedInstanceState.getBoolean(CHEAT_INDEX);
			setAnswerShownResult();
		}

		setContentView(R.layout.activity_cheat);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final TextView answerTextView = (TextView) findViewById(R.id.answerTextView);

		final Button showAnswerButton = (Button) findViewById(R.id.showAnswerButton);
		showAnswerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int answerId = getAnswerFromIntent()
					? R.string.true_button
					: R.string.false_button;
				answerTextView.setText(answerId);
				answerShown = true;
				setAnswerShownResult();
			}
		});
	}

	private boolean getAnswerFromIntent() {
		return getIntent().getBooleanExtra(EXTRA_ANSWER, false);
	}

	private void setAnswerShownResult() {
		Intent data = new Intent();
		data.putExtra(EXTRA_ANSWER_SHOWN, answerShown);
		setResult(RESULT_OK, data);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putBoolean(CHEAT_INDEX, answerShown);
	}
}
