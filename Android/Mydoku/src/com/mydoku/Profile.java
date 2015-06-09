package com.mydoku;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;

/**
 * Class represents the data for each time a player has completed a game, the 
 * class stores data that gets put in the database.
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class Profile {

	private static final long HOUR_IN_SECONDS = 3600;
	private String name = "";
	private long time = 0;
	private String diff = "";
	private String id = "";
	private int timesHintUsed = 0;
	

	/**
	 * Constructor for class.
	 * initiates all values the profile represents.
	 * 
	 * @param name
	 * @param timeResult
	 * @param diff
	 * @param uuid
	 */
	public Profile(String name, long timeResult, String diff, int timesHintUsed, String uuid) {
		this.name = name;
		this.timesHintUsed = timesHintUsed;
		this.time = timeResult;
		this.diff = diff;
		id = uuid;
	}

	/**
	 * Getter for timesHintUsed
	 * 
	 * @return
	 */
	public int getTimesHintUsed() {
		return timesHintUsed;
	}
	
	/**
	 * Setter for timesHintUsed
	 * 
	 * @param timesHintUsed
	 */
	public void setTimesHintUsed(int timesHintUsed) {
		this.timesHintUsed = timesHintUsed;
	}
	
	/**
	 * Constructor
	 */
	public Profile() {
	}

	/**
	 * Getter for id
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter for id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Getter for name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for time.
	 * @return
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Setter for time
	 * @param time
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * Getter for difficulty
	 * @return
	 */
	public String getDiff() {
		return diff;
	}

	/**
	 * Setter for difficulty
	 * @param diff
	 */
	public void setDiff(String diff) {
		this.diff = diff;
	}

	/**
	 * Specifies how a profile toString should look like.
	 * Used in HighscoreActivity to create highscore list
	 */
	@SuppressLint("SimpleDateFormat")
	@Override
	public String toString() {
		SimpleDateFormat sdf;
		if (time >= HOUR_IN_SECONDS) {
			sdf = new SimpleDateFormat("hh:mm:ss");
		} else {
			sdf = new SimpleDateFormat("mm:ss");
		}
		return name + " " + sdf.format(1000 * time) + " "+timesHintUsed + " " + diff;
	}
}
