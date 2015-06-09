package com.mydoku;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that handles the database for the game.
 * 
 * @author Christer
 * @version 1.0
 * @since 2014-08-21
 */
public class MySql extends SQLiteOpenHelper {

	// Table
	public static final String TABLE_HIGHSCORE = "highscore";

	// COLUMNS
	public static final String NAME = "Name";
	public static final String TIME = "Time";
	public static final String DIFFICULTY = "Difficulty";
	public static final String ID = "id";
	public static final String HINT = "Hint";

	// Database name
	private static final String DATABASE_NAME = "highscore.db";

	private static final int DATABASE_VERSION = 1;

	// Create TABLE
	private static final String CREATE_TABLE = "CREATE TABLE "
			+ TABLE_HIGHSCORE + "(" + TIME + " INTEGER," + NAME + " TEXT,"
			+ DIFFICULTY + " TEXT," + HINT + " INTEGER," + ID + " TEXT PRIMARY KEY)";

	// Keys for columns
	private static final String KEY_NAME = "Name";
	private static final String KEY_TIME = "Time";
	private static final String KEY_DIFF = "Difficulty";
	private static final String KEY_ID = "id";
	private static final String KEY_HINT = "Hint";

	/**
	 * Creates table
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	/**
	 * Constructor for the class.
	 * 
	 * @param context
	 */
	public MySql(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * if database will upgrade to newer version. the table gets removed and
	 * recreated.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIGHSCORE);

		// create new tables
		onCreate(db);
	}

	/*
	 * Creating a profile
	 */
	public long createProfile(Profile profile) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, profile.getName());
		values.put(KEY_TIME, profile.getTime());
		values.put(KEY_DIFF, profile.getDiff());
		values.put(KEY_ID, profile.getId());
		values.put(KEY_HINT, profile.getTimesHintUsed());

		long profile_id = 0;

		profile_id = db.insert(TABLE_HIGHSCORE, null, values);
		db.close();
		return profile_id;
	}

	/*
	 * get single profile
	 */
	public Profile getProfile(long profile_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_HIGHSCORE + " WHERE "
				+ KEY_NAME + " = " + profile_id;

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Profile p = new Profile();
		p.setName(c.getString(c.getColumnIndex(KEY_NAME)));
		p.setDiff((c.getString(c.getColumnIndex(KEY_DIFF))));
		p.setTime(c.getInt(c.getColumnIndex(KEY_TIME)));
		p.setTimesHintUsed(c.getColumnIndex(KEY_HINT));
		
		db.close();
		return p;
	}

	/*
	 * Getting all profiles
	 */
	public List<Profile> getAllProfiles() {
		List<Profile> profiles = new ArrayList<Profile>();
		String selectQuery = "SELECT  * FROM " + TABLE_HIGHSCORE;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Profile p = new Profile();
				p.setName(c.getString(c.getColumnIndex(KEY_NAME)));
				p.setDiff((c.getString(c.getColumnIndex(KEY_DIFF))));
				p.setTime(c.getInt(c.getColumnIndex(KEY_TIME)));
				p.setTimesHintUsed(c.getInt(c.getColumnIndex(KEY_HINT)));
				
				// adding to Profile list
				profiles.add(p);
			} while (c.moveToNext());
		}
		db.close();
		return profiles;
	}

	/**
	 * Get profiles with a specific value in Difficulty column.
	 * 
	 * @param string
	 * @return
	 */
	public List<Profile> getProfilesWithDiff(String string) {
		List<Profile> profiles = new ArrayList<Profile>();
		String selectQuery = "SELECT  *  FROM " + TABLE_HIGHSCORE;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				if (c.getString(c.getColumnIndex(KEY_DIFF)).equals(string)) {
					Profile p = new Profile();
					p.setName(c.getString(c.getColumnIndex(KEY_NAME)));
					p.setDiff((c.getString(c.getColumnIndex(KEY_DIFF))));
					p.setTime(c.getInt(c.getColumnIndex(KEY_TIME)));
					p.setTimesHintUsed(c.getInt(c.getColumnIndex(KEY_HINT)));
					
					profiles.add(p);
				} 
			} while (c.moveToNext());
		}
		db.close();
		return profiles;
	}

	/**
	 * Deletes the content in the database.
	 * @return 
	 */
	public boolean deleteDb() {
		SQLiteDatabase database = this.getReadableDatabase();
 		return SQLiteDatabase.deleteDatabase(new File(database.getPath()));
	}

}
