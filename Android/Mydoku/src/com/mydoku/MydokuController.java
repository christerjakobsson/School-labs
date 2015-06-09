package com.mydoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Class that acts as controller for the application, attaches a listener to 
 * all the buttons. Starts other activities for when the player is playing the 
 * game and when the player have finished the game and adds score to the database. 	
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class MydokuController extends Activity {

	private static final String DIFFICULTY = "difficulty";
	private static final String GAME_RES = "game_res";
	private static final int GAME_RESULT = 1;
	private static final String TIME = "time";
	private static final String TIME_HINTS_USED_TOTAL = "timesHintUsed";

	private int difficulty;
	private Button highScore;
	private Button newGame;
	private long timeResult;
	private int hintTimes;

	/**
	 * Listener for the buttons in the game. Does appropriate actions depending
	 * on which button is clicked.
	 */
	public OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.newGameButton:
				if (difficulty >= 1 && difficulty <= 3) {
					startGameActivity(difficulty);
				} else {
					showToast(getString(R.string.must_choose_difficulty_to_play_game));
				}
				break;
			case R.id.leaderBoardButton:
				startHighScoreActivity();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Creates an intent and starts activity which shows the highscore lists.
	 */
	private void startHighScoreActivity() {
		Intent gameIntent = new Intent(this, LeaderboardActivity.class);
		startActivity(gameIntent);		
	}

	/**
	 * Getter for difficulty
	 * 
	 * @return difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

	/**
	 * Getter for timeResult.
	 * 
	 * @return Long: timeResult
	 */
	public long getTimeResult() {
		return timeResult;
	}

	/**
	 * Gets result from started activities.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				timeResult = data.getLongExtra(GAME_RES, 0);
				hintTimes = data.getIntExtra(TIME_HINTS_USED_TOTAL, 0);
				startCompletedActivity(timeResult, difficulty,  hintTimes);
			}
		}
	}
	
	/**
	 * Starts an activity after the game is completed.
	 * 
	 * @param time
	 * @param diff
	 */
	
	private void startCompletedActivity(long time, int diff, int hint) {
		Intent gameIntent = new Intent(this, CompletedActivity.class);
		
		gameIntent.putExtra(DIFFICULTY, diff);
		gameIntent.putExtra(TIME_HINTS_USED_TOTAL, hintTimes);
		gameIntent.putExtra(TIME, time);
		startActivity(gameIntent);
	}

	/**
	 *
	 * sets the content view and creates buttons and adds listener to them.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydokucontroller_main);

		if (savedInstanceState != null) {
			timeResult = savedInstanceState.getLong(TIME);
			difficulty = savedInstanceState.getInt(DIFFICULTY);
		} else {
			timeResult = 0; 
			difficulty = 0;
		}

		newGame = (Button) findViewById(R.id.newGameButton);
		newGame.setOnClickListener(ButtonListener);

		highScore = (Button) findViewById(R.id.leaderBoardButton);
		highScore.setOnClickListener(ButtonListener);
	}

	/**
	 * Listener for  the radioButtons
	 * 
	 * @param view
	 */
	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.easy:
			if (checked)
				difficulty = 1;
			break;
		case R.id.medium:
			if (checked)
				difficulty = 2;
			break;
		case R.id.hard:
			if (checked)
				difficulty = 3;
			break;
		default:
		}
	}

	/**
	 * Restores data to all needed variables.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(savedInstanceState != null) {
		timeResult = savedInstanceState.getLong(TIME);
		difficulty = savedInstanceState.getInt(DIFFICULTY);
		}
	}

	/**
	 * Saves data from all needed variables.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(TIME, timeResult);
		outState.putInt(DIFFICULTY, difficulty);
	}
	
	/**
	 * Shows a toast with the given message.
	 * 
	 * @param message
	 */
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Create the action bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	private void startHelpActivity() {
		Intent helpIntent = new Intent(this, HelpActivity.class);
		startActivity(helpIntent);
	}
	
	/**
	 * Listener for when the player presses a menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			startHelpActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	/**
	 * Starts a activity where the game will be played.
	 * 
	 * @param difficulty
	 */
	private void startGameActivity(int difficulty) {
		Intent gameIntent = new Intent(this, GameActivity.class);
		gameIntent.putExtra(DIFFICULTY, difficulty);
		startActivityForResult(gameIntent, GAME_RESULT);
	}
}
