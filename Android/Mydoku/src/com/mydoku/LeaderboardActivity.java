package com.mydoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Class used to create activity where the user can check the leaderboard.
 * 
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class LeaderboardActivity extends Activity implements OnItemSelectedListener {

	private ListView listView;
	private MySql db;
	private ArrayList<String> leaderboard;
	private Button easyButton;
	private Button mediumButton;
	private Button hardButton;
	private Button deleteDb;
	private ListViewAdapter adapter;
	private Comparator<Profile> sort;

	/**
	 * Instantiates variables, gets listview from view and sets a listview
	 * adapter to it.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard_view);
		sort = new TimeComparator();
		db = new MySql(this);
		leaderboard = new ArrayList<>();

		createButtons();

		listView = (ListView) findViewById(R.id.leaderboard_list);
		adapter = new ListViewAdapter(this, leaderboard);

		listView.setAdapter(adapter);
	}

	/**
	 * Creates the buttons from the view and sets listeners for them.
	 */
	private void createButtons() {
		easyButton = (Button) findViewById(R.id.easyButton);
		easyButton.setOnClickListener(ButtonListener);

		mediumButton = (Button) findViewById(R.id.mediumButton);
		mediumButton.setOnClickListener(ButtonListener);

		hardButton = (Button) findViewById(R.id.hardButton);
		hardButton.setOnClickListener(ButtonListener);

		deleteDb = (Button) findViewById(R.id.deleteDb);
		deleteDb.setOnClickListener(ButtonListener);
		
		Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
		String[] items = new String[]{"Sort By Time", "Sort By Hints",};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		dropdown.setAdapter(adapter);
	}

	/**
	 * Listener for the buttons.
	 */
	public OnClickListener ButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.easyButton:
				leaderboard = getLeaderBoard("Easy");
				refreshAdapter();
				break;
			case R.id.mediumButton:
				leaderboard = getLeaderBoard("Medium");
				refreshAdapter();
				break;
			case R.id.hardButton:
				leaderboard = getLeaderBoard("Hard");
				refreshAdapter();
				break;
			case R.id.deleteDb:
				deleteDb();
				break;
			default:
				break;
			}
		}
	};	
	
	/**
	 * Refreshes the adapter with the new changes and calls the listview to
	 * repaint itself.
	 */
	private void refreshAdapter() {
		adapter = new ListViewAdapter(this, leaderboard);
		listView.setAdapter(adapter);
		listView.invalidate();
	}

	/**
	 * Gets a leaderboard depending on which difficulty the user wants to see.
	 * 
	 * @param string
	 * @return
	 */
	private ArrayList<String> getLeaderBoard(String string) {
		ArrayList<String> high = new ArrayList<>();
		List<Profile> profiles = db.getProfilesWithDiff(string);

		Collections.sort(profiles, sort);

		for (int i = 0; i < profiles.size(); i++) {
			high.add((i + 1) + " " + profiles.get(i).toString());
		}

		return high;
	}

	/**
	 * Removes all the values from the database.
	 */
	public void deleteDb() {
		db.close();

		if (!db.deleteDb()) {
			Toast.makeText(this, "Error deleting database", Toast.LENGTH_SHORT)
					.show();
		} else {
			leaderboard.clear();
			refreshAdapter();
		}
	}

	/**
	 * Comparator to sort the list with profiles by time.
	 * 
	 */
	class TimeComparator implements Comparator<Profile> {

		@Override
		public int compare(Profile x, Profile y) {
			if (x.getTime() < y.getTime()) {
				return -1;
			} else if (x.getTime() == y.getTime()) {
				return 0;
			} else
				return 1;
		}
	}
	
	/**
	 * Comparator to sort the list with profiles by hints.
	 * 
	 */
	class HintComparator implements Comparator<Profile> {

		@Override
		public int compare(Profile x, Profile y) {
			if (x.getTimesHintUsed() < y.getTimesHintUsed()) {
				return -1;
			} else if (x.getTimesHintUsed() == y.getTimesHintUsed()) {
				return 0;
			} else
				return 1;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch(position) {
		case 0:
			sort = new TimeComparator();
			refreshAdapter();
			break;
		case 1:
			sort = new HintComparator();
			refreshAdapter();
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
}
