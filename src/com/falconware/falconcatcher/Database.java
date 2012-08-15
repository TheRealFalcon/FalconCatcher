package com.falconware.falconcatcher;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database {
	private SQLiteDatabase mDb;
	
	private class DictionaryOpenHelper extends SQLiteOpenHelper {
		private static final int DATABASE_VERSION = 1;
		private static final String DATABASE_NAME = "FalconCatcher";
		private static final String SETTINGS_CREATE =
				"CREATE TABLE settings (" +
						"key TEXT, " +
						"value TEXT);";
		private static final String FEED_CREATE =
				"CREATE TABLE feed (" +
						"id INTEGER PRIMARY KEY, " +
						"url TEXT, " +
						"title TEXT, " +
						"imagePath TEXT);";
		//TODO
		private static final String EPISODE_CREATE =
				"CREATE TABLE episode (" +
						"feedId INTEGER, " +
						"url TEXT, " +
						"title TEXT, " +
						"description TEXT, " +
						"author TEXT, " +
						"published TEXT, " +
						"localFile TEXT);";

		DictionaryOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SETTINGS_CREATE);
			db.execSQL(FEED_CREATE);
			db.execSQL(EPISODE_CREATE);
			//ContentValues insertValues = new ContentValues();
			//insertValues.put("key", "applicationDirectory");
			//db.insert("settings", "value", insertValues);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}  
	}
	
	public Database(Context context) {
		DictionaryOpenHelper helper = new DictionaryOpenHelper(context);
		mDb = helper.getWritableDatabase();
	}
	
	public long addFeed(String url, String title, String imagePath) {
		ContentValues insertValues = new ContentValues();
		insertValues.put("url", url);
		insertValues.put("title", title);
		insertValues.put("imagePath", imagePath);
		return mDb.insert("feed", null, insertValues);
	}
	
	//public void addEpisode(String feedUrl)
}
