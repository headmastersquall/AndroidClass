package com.bignerdranch.android.geoquiz;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button trueButton;
    private Button falseButton;
	private Button previousButton;
    private Button nextButton;
    private TextView questionTextView;
	private int currentIndex = 0;

    private Question[] questions = new Question[] {
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };

    private void updateQuestion() {
		int question = questions[currentIndex].getTextResourceId();
		questionTextView.setText(question);
    }

	private void checkAnswer(boolean usersAnswer) {
		boolean actualAnswer = questions[currentIndex].isAnswer();
		int messageId =
			usersAnswer == actualAnswer
			? R.string.correct_toast
			: R.string.incorrect_toast;
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}

    private void advanceToPreviousQuestion() {
        currentIndex = currentIndex == 0 ? questions.length - 1 : currentIndex - 1;
        updateQuestion();
    }

	private void advanceToNextQuestion() {
		currentIndex = (currentIndex + 1) % questions.length;
		updateQuestion();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = (TextView) findViewById(R.id.question_text_view);
        questionTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				advanceToNextQuestion();
			}
		});

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

		previousButton = (Button) findViewById(R.id.previous_button);
		previousButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				advanceToPreviousQuestion();
			}
		});
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                advanceToNextQuestion();
			}
		});

		updateQuestion();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
