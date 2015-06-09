package com.mydoku;

import java.text.SimpleDateFormat;

import java.util.UUID;

import com.mydoku.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The CompletedActivity class extends an activity and gets shown when 
 * the user have finished a game. Used to show the player the time it took and 
 * which difficulty they played and gives them a choice to add their result to 
 * the database.
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21 
 */
@SuppressLint("SimpleDateFormat")
public class CompletedActivity extends Activity {

	private static final String TIME = "time";
	private static final String DIFFICULTY = "difficulty";
	private static final String TIME_HINTS_USED_TOTAL = "timesHintUsed";
	
	private long time;
	private String difficulty;
	private TextView timeText;
	private TextView diffText;
	private Button addToDbButton;
	private EditText nameText;
	private MySql db;
	private int timesHintUsed;
	private TextView hintText;

	/**
	 * onCreate for the activity, declares variables and sets listener to buttons
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_view);

		if (savedInstanceState != null) {
			timesHintUsed = savedInstanceState.getInt(TIME_HINTS_USED_TOTAL);
			time = savedInstanceState.getLong(TIME);
			difficulty = savedInstanceState.getString(DIFFICULTY);
		} else {
			Intent mIntent = getIntent();
			int diff = mIntent.getIntExtra(DIFFICULTY, 1);
			if (diff == 1) {
				difficulty = "Easy";
			} else if (diff == 2) {
				difficulty = "Medium";
			} else if (diff == 3) {
				difficulty = "Hard";
			}
			timesHintUsed = mIntent.getIntExtra(TIME_HINTS_USED_TOTAL, 0);
			time = mIntent.getLongExtra(TIME, 0);
		}

		timeText = (TextView) findViewById(R.id.timeView);
		timeText.setText("Time: "
				+ new SimpleDateFormat("mm:ss").format(1000 * time));

		diffText = (TextView) findViewById(R.id.difficultyView);
		diffText.setText("Difficulty: " + difficulty);

		hintText = (TextView) findViewById(R.id.hintTimeView);
		hintText.setText("Hints used: "+timesHintUsed);
		
		nameText = (EditText) findViewById(R.id.nameText);

		
		addToDbButton = (Button) findViewById(R.id.ok);
		addToDbButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = nameText.getText().toString();
				if (text.length() < 3) {
					Toast.makeText(getBaseContext(),
							"Must have a name, (Min 3 letters)",
							Toast.LENGTH_SHORT).show();
				} else if (text.contains(" ")) {
					Toast.makeText(getBaseContext(),
							"Name cant contain spaces", Toast.LENGTH_SHORT)
							.show();
				} else {
					addToDb();
					finish();
				}
			}
		});
	}

	/**
	 * Adds data to the database.  
	 */
	private void addToDb() {
		db = new MySql(this);
		db.createProfile(new Profile(nameText.getText().toString(), time,
				difficulty, timesHintUsed, UUID.randomUUID().toString()));
	}

	/**
	 * Restores values to variables when recreating activity.
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		time = savedInstanceState.getLong(TIME);
		difficulty = savedInstanceState.getString(DIFFICULTY);
		timesHintUsed = savedInstanceState.getInt(TIME_HINTS_USED_TOTAL);
	}

	/**
	 * Saves variables.
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putString(DIFFICULTY, difficulty);
		outState.putLong(TIME, time);
		outState.putInt(TIME_HINTS_USED_TOTAL, timesHintUsed);
	}
}
