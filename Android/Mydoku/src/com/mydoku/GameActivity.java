package com.mydoku;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The class GameActivity controls the gameplay. Uses the class PuzzleView to
 * draw the sudoku grid and uses the class MydokuModel to create and manage a
 * sudoku board. Implements SensorEventListener and listens for changes in the
 * phones movement, used to change the selected cell in the sudoku board
 * depending on the phones angle.
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
@SuppressLint("SimpleDateFormat")
public class GameActivity extends Activity implements SensorEventListener {

	private static final String DIFFICULTY = "difficulty";
	private static final String GAME = "game";
	private static final String CHECK = "check";
	private static final String ORIGINALGAME = "original";
	private static final String GAME_RES = "game_res";
	private static final long HALF_HOUR = 1800;
	private static final String TIME_HINTS_USED_TOTAL = "timesHintUsed";


	private int timesHintsUsedTotal;
	private boolean isSensorOn;
	private float[] gravity;
	private int difficulty;
	private long time;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private BoardView boardView;
	private boolean doubleBackToExitPressedOnce = false;
	private Button mClear;
	private Button[] mNumberButtons;
	private MydokuModel model;
	private Timer timeTimer;
	private Timer gyroTimer;
	private Menu myMenu;
	private TextView mTimeView;
	private float currAccel;
	private float accelLast;
	private float mAccel;
	private int timesHintsUsed;
	private long startTimeHint = 0;
	private TextView mHintView;
	private ShowHintRunnable shr;
	private long lastUpdate;


	/**
	 * Getter for gravity Synchronized cause its used by different threads.
	 * 
	 * @return
	 */
	public synchronized float[] getGravity() {
		return gravity;
	}

	/**
	 * Setter for gravity Syncronized cause its used by different threads.
	 * 
	 * @param g
	 */
	public synchronized void setmGravity(float[] g) {
		gravity = g;
	}

	/**
	 * Build the interface for the activity.
	 */
	@SuppressLint("InflateParams")
	private void buildInterface() {
		LayoutInflater inf = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout buttonLayout = (LinearLayout) inf.inflate(R.layout.board_view, null,
				false);

		FrameLayout view = new FrameLayout(this);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		view.setLayoutParams(lp);
		boardView = new BoardView(this, model);
		buttonLayout.setWeightSum(0.32f);
		view.addView(boardView);
		view.addView(buttonLayout);
		setContentView(view);
	}

	/**
	 * Checks if game is completed.
	 */
	private void checkIfFinished() {
		if (IsGameCompleted()) {
			stopSensor();
			gameIsFinished();
		}
	}

	/**
	 * Creates all the buttons for the view.
	 */
	private void createButtons() {
		mNumberButtons = new Button[9];

		mNumberButtons[0] = (Button) findViewById(R.id.button1);
		mNumberButtons[1] = (Button) findViewById(R.id.button2);
		mNumberButtons[2] = (Button) findViewById(R.id.button3);
		mNumberButtons[3] = (Button) findViewById(R.id.button4);
		mNumberButtons[4] = (Button) findViewById(R.id.button5);
		mNumberButtons[5] = (Button) findViewById(R.id.button6);
		mNumberButtons[6] = (Button) findViewById(R.id.button7);
		mNumberButtons[7] = (Button) findViewById(R.id.button8);
		mNumberButtons[8] = (Button) findViewById(R.id.button9);

		for (int i = 0; i < mNumberButtons.length; i++) {
			mNumberButtons[i].setOnClickListener(ButtonListener);
		}

		mClear = (Button) findViewById(R.id.clearCell);
		mClear.setOnClickListener(ButtonListener);

	}

	/**
	 * Finishes the activity, sets the time it took to complete the game as
	 * result to be sent back.
	 */
	private void gameIsFinished() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GAME_RES, time);
		returnIntent.putExtra(TIME_HINTS_USED_TOTAL, timesHintsUsedTotal);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	/**
	 * Checks if the game is completed, uses methods in the model to check that
	 * all cells are filled and all are correct
	 * 
	 * @return
	 */
	private boolean IsGameCompleted() {
		if (model.isAllCellsFilled()) {
			if (model.checkGame()) {
				return true;
			} else
				return false;
		}
		return false;
	}

	/**
	 * Checks how many cells are left to be filled by the player.
	 * 
	 * @return
	 */
	private int numbersOfCellsLeft() {
		int count = 0;
		for (int i = 0; i < model.getGame().length; i++) {
			if (model.getGame()[i] == 0) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * onCreate for the activity, Starts a timer that increments the time
	 * variable to track how long time it took to play. Also starts a timer that
	 * gets the sensors data at set intervals and determines if the selected
	 * cell should be changed.
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timesHintsUsedTotal = 0;
		time = 0;
		startTimer();
		shr = new ShowHintRunnable();

		if (savedInstanceState != null) {
			model = new MydokuModel();
			model.setGame(savedInstanceState.getIntArray(GAME));
			model.setCheck(savedInstanceState.getBooleanArray(CHECK));
			model.setOriginalGame(savedInstanceState.getIntArray(ORIGINALGAME));

		} else {
			Intent mIntent = getIntent();
			difficulty = mIntent.getIntExtra(DIFFICULTY, 2);
			model = new MydokuModel();
			model.newGame(difficulty);
			model = new MydokuModel();
			model.newGame(difficulty);
		}

		buildInterface();

		createButtons();
		setTextViews();

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		startSensor();
		isSensorOn = true;
		startGeoTimer();
	}

	/**
	 * Create the action bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.game_activity_menu, menu);

		myMenu = menu;
		return true;
	}

	/**
	 * Listener for when the player presses a menu item.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (myMenu != null) {
			MenuItem g = myMenu.findItem(R.id.disable_enable_gyro);
			if (isSensorOn) {
				g.setTitle("Disable Gyro");
			} else {
				g.setTitle("Enable Gyro");
			}
		}

		switch (item.getItemId()) {
		case R.id.check_game:
			if (model.checkGame()) {
				showToast(getString(R.string.everything_is_ok_)
						+ numbersOfCellsLeft() + getString(R.string._to_go));
			} else {
				showToast(getString(R.string.checkGameMistakes));
			}
			return true;
		case R.id.disable_enable_gyro:
			if (isSensorOn) {
				stopSensor();
				isSensorOn = false;
			} else {
				isSensorOn = true;
				startSensor();
			}
			return true;

		case R.id.help:
			startHelpActivity();
			return true;
		case R.id.show_hint:
			showHint();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startHelpActivity() {
		Intent helpIntent = new Intent(this, HelpActivity.class);
		startActivity(helpIntent);
	}

	/**
	 * Method called when
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		timeTimer.cancel();
		gyroTimer.cancel();
	}

	/**
	 * Saves variables
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBooleanArray(CHECK, model.getCheck());
		outState.putIntArray(GAME, model.getGame());
		outState.putIntArray(ORIGINALGAME, model.getOriginalGame());
	}

	/**
	 * Called when the sensor values have changed.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {

		case Sensor.TYPE_ACCELEROMETER:
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			setmGravity(event.values.clone());

			long curTime = System.currentTimeMillis();
			// only allow one update every 1000ms.
			if ((curTime - lastUpdate) > 1000) {
				lastUpdate = curTime;

				accelLast = currAccel;
				currAccel = (float) Math.sqrt((x * x + y * y + z * z));
				float delta = currAccel - accelLast;
				mAccel = mAccel * 0.9f + delta; // perform low-cut filter
				if (mAccel > 5 && time > 5) {
					showHint();

				}
			}
			break;
		default:
			return;
		}
	}

	/**
	 * Class that runs a handler after a shake event have occured and the hint
	 * is shown. waits for 4 seconds then removes the hint.
	 * 
	 */
	class ShowHintRunnable {

		private boolean isRunning = false;

		public void runHandler() {
			isRunning = true;
			while (isRunning) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						model.setHint(model.getHintIndex(), 0);
						boardView.postInvalidate();
					}
				}, 4000);
				isRunning = false;
			}
		}

		public boolean isRunning() {
			return isRunning;
		}

	}

	/**
	 * Gets a random cell which isnt filled and shows the value that should be
	 * in that cell.
	 */
	private void showHint() {
		if (timesHintsUsed > 2) {
			if (getHintTime() > HALF_HOUR) {
				timesHintsUsed = 0;
			}
		} else if (timesHintsUsed < 3 && !shr.isRunning()) {
			timesHintsUsed++;
			timesHintsUsedTotal++;
			int hintIndex = model.generateHint();
			model.setHint(hintIndex, model.getSolutionCell(hintIndex));
			boardView.postInvalidate();
			shr.runHandler();
		}
	}

	/**
	 * Starts the timer that uses the sensor data to determine which direction
	 * the phone is tilted and changes the selected cell corresponding to the
	 * direction.
	 */
	private void startGeoTimer() {
		// mGravity[0] = x values
		// mGravity[1] = y values
		gyroTimer = new Timer();
		gyroTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						if (getGravity() != null && isSensorOn) {
							if (getGravity()[0] > 5) {
								boardView.moveGyro("LEFT");
							} else if (getGravity()[0] < -5) {
								boardView.moveGyro("RIGHT");
							}
							if (getGravity()[1] > 5) {
								boardView.moveGyro("DOWN");
							} else if (getGravity()[1] < 5) {
								boardView.moveGyro("UP");
							}
						}
					}
				});
			}
		}, 0, 1000);
	}

	/**
	 * Used to make it so that the user needs to press the back button twice to
	 * exit the activity. The user needs to press the back button twice in less
	 * than two seconds.
	 */
	@Override
	public void onBackPressed() {

		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();

			finish();
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Press again to quit", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	/**
	 * Sets the value on the game board on the selected cell, calls for a
	 * repaint of the view.
	 * 
	 * @param index
	 */
	private void setCellValue(int index) {
		if (model.getOriginalGame()[boardView.getSelectedCell()] == 0) {
			model.setValue(boardView.getSelectedCell(), index);
			boardView.postInvalidate();
		}
	}

	/**
	 * Finds the textview in the view and sets their text.
	 */
	private void setTextViews() {
		TextView difficultyView = (TextView) findViewById(R.id.difficultyView);
		if (difficulty == 1) {
			difficultyView.setText("Easy");
		} else if (difficulty == 2) {
			difficultyView.setText("Medium");
		} else if (difficulty == 3) {
			difficultyView.setText("Hard");
		}
		mTimeView = (TextView) findViewById(R.id.timeView);
		mTimeView.setText("00:00");

		mHintView = (TextView) findViewById(R.id.hintView);

	}

	/**
	 * Creates a toast and shows it.
	 * 
	 * @param message
	 */
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Starts a timer that increments the time variable each second to get how
	 * long time it took for the player to finish the game.
	 */
	private void startTimer() {
		timeTimer = new Timer();
		timeTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				time++;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (timesHintsUsed > 2) {
							mHintView.setText(getText(R.string.hintText)
									+ " "
									+ sdf.format(((1800 - getHintTime()) * 1000)));
						} else {
							mHintView.setText("Hints left: "
									+ (3 - timesHintsUsed));
						}

						mTimeView.setText(sdf.format(1000 * time));
					}
				});
			}
		}, 0, 1000);
	}

	/**
	 * Gets the time for the hint timer.
	 * @return
	 */
	private long getHintTime() {
		return time - startTimeHint;
	}

	/**
	 * Register listener
	 */
	private void startSensor() {
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_GAME);
	}

	/**
	 * Unregister listeners from sensor.
	 */
	private void stopSensor() {
		mSensorManager.unregisterListener(this);
	}

	/**
	 * Listener for the buttons in the game. Does appropriate actions depending
	 * on which button is clicked.
	 */
	public OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				setCellValue(1);
				checkIfFinished();
				break;
			case R.id.button2:
				setCellValue(2);
				checkIfFinished();
				break;
			case R.id.button3:
				setCellValue(3);
				checkIfFinished();
				break;
			case R.id.button4:
				setCellValue(4);
				checkIfFinished();
				break;
			case R.id.button5:
				setCellValue(5);
				checkIfFinished();
				break;
			case R.id.button6:
				setCellValue(6);
				checkIfFinished();
				break;
			case R.id.button7:
				setCellValue(7);
				checkIfFinished();
				break;
			case R.id.button8:
				setCellValue(8);
				checkIfFinished();
				break;
			case R.id.button9:
				setCellValue(9);
				checkIfFinished();
				break;
			case R.id.clearCell:
				setCellValue(0);
				break;
			default:
				break;
			}
		}
	};
}