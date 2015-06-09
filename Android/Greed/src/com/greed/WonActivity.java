package com.greed;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/* WonActivity
 *
 * Purpose: Used to show the user that they have won and ask them if they 
 * want to play again.
 *
 * v1.0
 *
 * Copyright (c). 2014-01-28 Christer Jakobsson.
 */

/**
 * Class that extends Activity and tells the user that they've won. Shows the
 * score and how many turns it took, and has a button if the player wants to
 * play again
 * 
 * @author Christer
 * 
 */
public class WonActivity extends Activity {

	private static final String TURNS = "turns";
	private static final String SCORE = "score";
	private static final String SCORE_TEXT = "scoreText";
	private static final int CLOSE_ALL = 0;
	protected static final int CONTINUE = 1;

	private TextView youGotView;
	private Button tryAgainButton;
	private String scoreText = "";
	private int score;
	private int turns;

	/**
	 * onCreate that initializes all the view objects and gets the data from
	 * GreedController and puts them in the view objects.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_won);

		Bundle extras = getIntent().getExtras();

		if (savedInstanceState == null && extras == null) {
			score = 0;
			turns = 0;
		} else if (extras != null) {
			score = extras.getInt(SCORE, 100);
			turns = extras.getInt(TURNS, 100);
		} else {
			score = savedInstanceState.getInt(SCORE, 200);
			turns = savedInstanceState.getInt(TURNS, 200);
			scoreText = savedInstanceState.getString(SCORE_TEXT);

		}
		youGotView = (TextView) findViewById(R.id.youGotTextView);
		createScoreText();
		youGotView.setText(scoreText);

		tryAgainButton = (Button) findViewById(R.id.tryButton);
		tryAgainButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setResult(CONTINUE);
				finish();
			}
		});
	}
	
	/**
	 * Returns CLOSE_ALL to the GreedController when the user pressed the back
	 * button and closes the application.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(CLOSE_ALL);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Saves data that needs to be saved.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SCORE, score);
		outState.putInt(TURNS, turns);
		outState.putString(SCORE_TEXT, scoreText);
	}

	/**
	 * Sets the scoreText to tell the player what score they got and how many
	 * turns they had to play to win.
	 */
	private void createScoreText() {
		scoreText = getResources().getString(R.string.you_got) + " " + score
				+ " " + getResources().getString(R.string.after) + " " + turns
				+ " " + getResources().getString(R.string.rounds);
	}

	/**
	 * Restores data.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		score = savedInstanceState.getInt(SCORE);
		turns = savedInstanceState.getInt(TURNS);
		scoreText = savedInstanceState.getString(SCORE_TEXT);
	}
}
