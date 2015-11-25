package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
	private static final String KEY_INDEX = "index";
	private static final String CHEAT_INDEX = "cheat";
	private static final int REQUEST_CODE_CHEAT = 0;

    private Button trueButton;
    private Button falseButton;
    private Button nextButton;
	private Button cheatButton;
    private TextView questionTextView;
	private int currentIndex = 0;

    private final Question[] questions = new Question[] {
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };

	private boolean[] cheatedQuestions = new boolean[questions.length];

    private void updateQuestion() {
		int question = questions[currentIndex].getTextResourceId();
		questionTextView.setText(question);
    }

	private void checkAnswer(boolean usersAnswer) {
		boolean actualAnswer = questions[currentIndex].isAnswerTrue();

		int messageId;

		if (hasCheated()) {
			messageId = R.string.judgment_toast;
		}
		else {
			messageId =
				usersAnswer == actualAnswer
					? R.string.correct_toast
					: R.string.incorrect_toast;
		}
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}

	private boolean hasCheated() {
		return cheatedQuestions[currentIndex];
	}

	private void setHasCheated(boolean cheated) {
		cheatedQuestions[currentIndex] = cheated;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        questionTextView = (TextView) findViewById(R.id.question_text_view);

        trueButton = (Button) findViewById(R.id.true_button);
        trueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAnswer(true);
			}
		});

        falseButton = (Button) findViewById(R.id.false_button);
        falseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAnswer(false);
			}
		});

        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentIndex = (currentIndex + 1) % questions.length;
				updateQuestion();
			}
		});

		cheatButton = (Button) findViewById(R.id.cheat_button);
		cheatButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean answer = questions[currentIndex].isAnswerTrue();
				Intent i = CheatActivity.newIntent(QuizActivity.this, answer);
				startActivityForResult(i, REQUEST_CODE_CHEAT);
			}
		});
		if (savedInstanceState != null) {
			currentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
			cheatedQuestions = savedInstanceState.getBooleanArray(CHEAT_INDEX);
		}
		updateQuestion();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_CODE_CHEAT &&
			data != null) {
			if (!hasCheated()) {
				setHasCheated(CheatActivity.wasAnswerShown(data));
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Log.i(TAG, "onSaveInstanceState");
		savedInstanceState.putInt(KEY_INDEX, currentIndex);
		savedInstanceState.putBooleanArray(CHEAT_INDEX, cheatedQuestions);
	}

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onPause() {
        super.onPause();
		Log.d(TAG, "onPause() called");
    }

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume() called");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop() called");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() called");
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

