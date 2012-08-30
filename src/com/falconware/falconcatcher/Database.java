package com.falconware.falconcatcher;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;


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
						"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"url TEXT, " +
						"title TEXT, " +
						"image TEXT);";
		//TODO
		private static final String EPISODE_CREATE =
				"CREATE TABLE episode (" +
						"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"feedTitle TEXT, " +
						"url TEXT, " +
						"title TEXT, " +
						"description TEXT, " +
						"author TEXT, " +
						"publishedDate TEXT, " +
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
	
	public void close() {
		mDb.close();
	}
	
	public long addFeed(String url, String title, String imageUrl) {
		ContentValues insertValues = new ContentValues();
		insertValues.put("url", url);
		insertValues.put("title", title);
		insertValues.put("image", imageToBuffer(imageUrl));
		return mDb.insert("feed", null, insertValues);
	}
	
	public long addEpisode(String feedTitle, String episodeUrl, String title, String description,
			String author, String publishedDate) {
		ContentValues insertValues = new ContentValues();
		insertValues.put("feedTitle", feedTitle);
		insertValues.put("url", episodeUrl);
		insertValues.put("title", title);
		insertValues.put("description", description);
		insertValues.put("author", author);
		insertValues.put("publishedDate", publishedDate);
		return mDb.insert("episode", null, insertValues);
	}
	
	public Cursor getSubscriptions() {
		String table = "feed";
		String[] columns = new String[] {"_id", "url", "title", "image"};
		String where = null; //return everything
		String[] whereArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		return mDb.query(table, columns, where, whereArgs, groupBy, having, orderBy);
	}
	
	public Cursor getEpisodes(String feedTitle) {
		String table = "episode";
		String[] columns = new String[] {"_id", "feedTitle", "url", "title", "description", "author", "publishedDate"};
		String where = "feedTitle=?";
		String[] whereArgs = new String[] {feedTitle};
		String groupBy = null;
		String having = null;
		String orderBy = null;
		return mDb.query(table, columns, where, whereArgs, groupBy, having, orderBy);
	}
	
	public void removeFeed(String feedTitle) {
		//SQLiteStatement statement = mDb.compileStatement("SELECT url FROM feed WHERE title=?");
		//statement.bindString(1, feedName);
		//String result = statement.simpleQueryForString();
		SQLiteStatement statement = mDb.compileStatement("DELETE FROM episode WHERE feedTitle=?");
		statement.bindString(1, feedTitle);
		statement.executeUpdateDelete();
		statement = mDb.compileStatement("DELETE FROM feed WHERE title=?");
		statement.bindString(1, feedTitle);
		statement.executeUpdateDelete();
	}
	
	private byte[] imageToBuffer(String imageUrl) {
		try {
			URL url = new URL(imageUrl);  //http://example.com/image.jpg
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is,128);
			ByteArrayBuffer baf = new ByteArrayBuffer(128);

			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			return baf.toByteArray();
		} catch (Exception e) {
			//TODO: Do better exception handling
			e.printStackTrace();
			return null;
		}
	}

	
	public String getApplicationDirectory() {
		SQLiteStatement statement = mDb.compileStatement("SELECT value FROM settings WHERE key='applicationDirectory'");
		String result;
		try {
			result = statement.simpleQueryForString();
		} catch (SQLiteDoneException e) {
			result = setApplicationDirectory();
		}
		return result;
	}
	
	private String setApplicationDirectory() {
		boolean externalAvailable = false;
		boolean externalWritable = false;
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    externalAvailable = externalWritable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    externalAvailable = true;
		    externalWritable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    externalAvailable = externalWritable = false;
		}
		
		if (externalAvailable && externalWritable) {
			return Environment.getExternalStorageDirectory().toString() + "/FalconCatcher/";
		}
		return Environment.getDataDirectory().toString();
	}
}
