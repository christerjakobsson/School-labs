package com.greed;

/* GreedController
 *
 * Purpose: This class controls all the actions for the game and listens to 
 * button clicks and image clicks.  Sets the TextViews text appropriately.
 *
 * v1.0
 *
 * Copyright (c). 2014-01-28 Christer Jakobsson.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * This class handles all the events that can occur when playing the game Greed.
 * creates a menu where the user can click to quit or get help about the rules.
 * 
 * @author Christer
 * 
 */
public class GreedController extends Activity {

	private static final String SCORE = "score";
	private static final String TURNS = "turns";
	private static final String ALL_DICE_USED_SCORE = "allDiceUsedScore";
	private static final String NR_OF_THROW = "nrOfThrow";
	private static final String DICES_USED = "dicesUsed";
	private static final String SCORE_DICE = "scoreDice";
	private static final String VAL = "val";
	private static final String LOCKED = "locked";
	private static final String MARKED = "marked";
	private static final String NR_OF_DICES = "nrOfDices";
	private static final int DICES = 6;
	private static final int QUIT = 1;
	private static final int CLOSE_ALL = 0;
	private static final String SCORE_BUTTON = "scoreButton";
	private static final String SAVE_BUTTON = "scoreButton";
	private int[] diceResources;
	private int[] lockedDiceResources;

	private TextView scoreView;
	private TextView turnScoreView;
	private TextView turnsView;
	private Button saveButton;
	private Button throwButton;
	private Button scoreButton;
	private ImageView[] diceImageView;
	private GreedModel g;
	private Vibrator vib;

	/**
	 * Initializes all view objects and sets all needed values if there is saved
	 * data. adds Listeners to the buttons and the imageviews
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			g = new GreedModel(DICES);
		} else {
			g = new GreedModel(DICES);
			g.setNrOfDices(savedInstanceState.getInt(NR_OF_DICES));
			g.setAllDiceUsedScore(savedInstanceState
					.getInt(ALL_DICE_USED_SCORE));
			g.setNrOfThrow(savedInstanceState.getInt(NR_OF_THROW));
			g.setDicesUsed(savedInstanceState.getInt(DICES_USED));
			g.setScoreDice(savedInstanceState.getIntArray(SCORE_DICE));
			g.setScore(savedInstanceState.getInt(SCORE));
			g.setTurns(savedInstanceState.getInt(TURNS));
			getDiceArrays(savedInstanceState);
		}

		scoreView = (TextView) findViewById(R.id.scoreTextView);
		turnScoreView = (TextView) findViewById(R.id.turnScoreTextView);
		turnsView = (TextView) findViewById(R.id.turnTextView);

		diceImageView = new ImageView[DICES];
		diceImageView[0] = (ImageView) findViewById(R.id.diceImageView_1);
		diceImageView[0].setOnClickListener(DiceViewListener);
		diceImageView[1] = (ImageView) findViewById(R.id.diceImageView_2);
		diceImageView[1].setOnClickListener(DiceViewListener);
		diceImageView[2] = (ImageView) findViewById(R.id.diceImageView_3);
		diceImageView[2].setOnClickListener(DiceViewListener);
		diceImageView[3] = (ImageView) findViewById(R.id.diceImageView_4);
		diceImageView[3].setOnClickListener(DiceViewListener);
		diceImageView[4] = (ImageView) findViewById(R.id.diceImageView_5);
		diceImageView[4].setOnClickListener(DiceViewListener);
		diceImageView[5] = (ImageView) findViewById(R.id.diceImageView_6);
		diceImageView[5].setOnClickListener(DiceViewListener);

		diceResources = new int[] { R.drawable.white1, R.drawable.white2,
				R.drawable.white3, R.drawable.white4, R.drawable.white5,
				R.drawable.white6 };

		lockedDiceResources = new int[] { R.drawable.white1_locked,
				R.drawable.white2_locked, R.drawable.white3_locked,
				R.drawable.white4_locked, R.drawable.white5_locked,
				R.drawable.white6_locked };
		
		scoreButton = (Button) findViewById(R.id.scoreButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		throwButton = (Button) findViewById(R.id.throwButton);

		scoreButton.setEnabled(false);
		saveButton.setEnabled(false);

		scoreButton.setOnClickListener(ButtonListener);
		throwButton.setOnClickListener(ButtonListener);
		saveButton.setOnClickListener(ButtonListener);
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	/**
	 * Listens for when the player clicks on a dice image and of the player
	 * should be able to mark it for not throwing it, it will be marked and the
	 * picture will change.
	 */
	public OnClickListener DiceViewListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (scoreButton.isEnabled()) {
				Dice[] dice = g.getDice();
				switch (v.getId()) {
				case R.id.diceImageView_1:
					if (!dice[0].isLocked()) {
						dice[0].markUnmark();
						repaint();
					}
					break;
				case R.id.diceImageView_2:
					if (!dice[1].isLocked()) {
						dice[1].markUnmark();
						repaint();
					}
					break;
				case R.id.diceImageView_3:
					if (!dice[2].isLocked()) {
						dice[2].markUnmark();
						repaint();
					}
					break;
				case R.id.diceImageView_4:
					if (!dice[3].isLocked()) {
						dice[3].markUnmark();
						repaint();
					}
					break;
				case R.id.diceImageView_5:
					if (!dice[4].isLocked()) {
						dice[4].markUnmark();
						repaint();
					}
					break;
				case R.id.diceImageView_6:
					if (!dice[5].isLocked()) {
						dice[5].markUnmark();
						repaint();
					}
					break;
				}
			}
		}
	};

	/**
	 * Listener for the buttons in the game. Does appropriate actions depending
	 * on which button is clicked. Checks if there should be a new turn or if
	 * the player has won.
	 */
	public OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean newTurn = false;

			switch (v.getId()) {
			case R.id.saveButton:
				if (g.isSavedOk()) {
					newTurn = saveScore();
				} else
					showToast("Marked dices dont give any points");

				break;
			case R.id.scoreButton:
				turnScoreView.setText("Turn Score: " + g.getTurnScore());
				break;
			case R.id.throwButton:
				if (g.isThrowScoreOk() || g.getNrOfThrow() == 0) {
					if (g.isAllDicesUsed() && !(g.getNrOfThrow() == 0)) {
						g.allDicesUsedThrowAgain();
					}
					g.throwDice();
					if (!g.isScoreOk()) {
						showToast(getString(R.string.not_enough_score));

						scoreButton.setEnabled(false);
						saveButton.setEnabled(false);
						newTurn = true;
					} else {
						saveButton.setEnabled(true);
						scoreButton.setEnabled(true);
					}
					repaint();
					g.lockScoreDices();

				} else {
					showToast(getString(R.string.not_enough_score));
				}
				break;
			}

			if (newTurn) {
				g.endTurn();
				turnScoreView.setText(R.string.turn_score_text);
				turnsView.setText("Turns: " + g.getTurns());
			}
			if (g.hasWon()) {
				youWon();
			}
		}
	};

	/**
	 * Used in onRestoreInstanceState. saves all the values for each dice.
	 * 
	 * @param savedInstanceState
	 */
	private void getDiceArrays(Bundle savedInstanceState) {
		boolean[] locked = savedInstanceState.getBooleanArray(LOCKED);
		boolean[] marked = savedInstanceState.getBooleanArray(MARKED);
		int[] val = savedInstanceState.getIntArray(VAL);

		for (int i = 0; i < g.getDice().length; i++) {
			g.getDice()[i].setLocked(locked[i]);
			g.getDice()[i].setMarked(marked[i]);
			g.getDice()[i].setVal(val[i]);
		}
	}

	/**
	 * Create the menu that shows when the player presses the menu button.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	/**
	 * Listener for when the player presses a menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help_action:
			runHelpActivity();
			return true;
		case R.id.quit_action:
			onStop();
			finish();
			return true;
		case R.id.restart_action:
			resetGame();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Restores data to all needed variables.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		g.setNrOfDices(savedInstanceState.getInt(NR_OF_DICES));
		g.setAllDiceUsedScore(savedInstanceState.getInt(ALL_DICE_USED_SCORE));
		g.setNrOfThrow(savedInstanceState.getInt(NR_OF_THROW));
		g.setDicesUsed(savedInstanceState.getInt(DICES_USED));
		g.setScoreDice(savedInstanceState.getIntArray(SCORE_DICE));
		g.setScore(savedInstanceState.getInt(SCORE));
		g.setTurns(savedInstanceState.getInt(TURNS));
		scoreButton.setEnabled(savedInstanceState.getBoolean(SCORE_BUTTON));
		saveButton.setEnabled(savedInstanceState.getBoolean(SAVE_BUTTON));
		getDiceArrays(savedInstanceState);
		repaint();
	}

	/**
	 * Saves data from all needed variables.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(NR_OF_DICES, g.getNrOfDices());
		outState.putInt(ALL_DICE_USED_SCORE, g.getAllDiceUsedScore());
		outState.putInt(NR_OF_THROW, g.getNrOfThrow());
		outState.putInt(DICES_USED, g.getDicesUsed());
		outState.putIntArray(SCORE_DICE, g.getScoreDice());
		outState.putInt(SCORE, g.getScore());
		outState.putInt(TURNS, g.getTurns());
		outState.putBoolean(SCORE_BUTTON, scoreButton.isEnabled());
		outState.putBoolean(SAVE_BUTTON, saveButton.isEnabled());
		saveDiceArrays(outState);
	}

	/**
	 * Changes the image of a dice if the user clicked on it and it is not
	 * marked nor locked, and updates the scoreTextView and turnTextView.
	 */
	private void repaint() {
		Dice[] dice = g.getDice();
		int index;

		for (int i = 0; i < dice.length; i++) {
			index = dice[i].getVal() - 1;
			if (!dice[i].isMarked()) {
				diceImageView[i].setImageResource(diceResources[index]);
			} else
				diceImageView[i].setImageResource(lockedDiceResources[index]);
		}
		
		scoreView.setText("Score: " + g.getScore());
		turnsView.setText("Turns: " + g.getTurns());
	}

	/**
	 * Resets the whole game to its starting state.
	 */
	private void resetGame() {
		g.reset();
		turnScoreView.setText(R.string.turn_score_text);
		turnsView.setText(R.string.turn_text);
		scoreView.setText(R.string.score_text);
		scoreButton.setEnabled(false);
		saveButton.setEnabled(false);
	}

	/**
	 * Start the activity that shows the help view.
	 */
	private void runHelpActivity() {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}

	/**
	 * Used by onSaveInstanceState Saves all the dices variables values.
	 * 
	 * @param outState
	 */
	private void saveDiceArrays(Bundle outState) {
		Dice[] dice = g.getDice();
		boolean[] marked = new boolean[dice.length];
		boolean[] locked = new boolean[dice.length];
		int[] val = new int[dice.length];
		
		for (int i = 0; i < dice.length; i++) {
			marked[i] = dice[i].isMarked();
			locked[i] = dice[i].isLocked();
			val[i] = dice[i].getVal();
		}

		outState.putIntArray(VAL, val);
		outState.putBooleanArray(LOCKED, locked);
		outState.putBooleanArray(MARKED, marked);
	}

	/**
	 * Saves the score and and change state of TextViews. returns a boolean that
	 * tells the game that there should be a new turn.
	 * 
	 * @return
	 */
	private boolean saveScore() {
		showToast("Saved " + g.saveScore());
		scoreView.setText("Score: " + g.getScore());
		turnsView.setText("Turns: " + g.getTurns());
		turnScoreView.setText(R.string.turn_score_text);
		scoreButton.setEnabled(false);
		saveButton.setEnabled(false);
		throwButton.setEnabled(true);
		return true;
	}

	/**
	 * Creates a Toast with the param message and shows it.
	 * 
	 * @param string
	 */
	private void showToast(String message) {
		Toast.makeText(GreedController.this, message, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * If the score is over 10 000 the player won and the activity to show this
	 * gets created here, it gets the score and the turn results so it can print
	 * them for the player.
	 */
	private void youWon() {
		Intent intent = new Intent(this, WonActivity.class);
		intent.putExtra(SCORE, g.getScore());
		intent.putExtra(TURNS, g.getTurns());
		resetGame();
		vib.vibrate(300);
		startActivityForResult(intent, QUIT);
	}

	/**
	 * Closes the application if the user pressed the back button in the Won
	 * activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == QUIT && resultCode == CLOSE_ALL) {
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
