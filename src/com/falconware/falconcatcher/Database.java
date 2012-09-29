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
	public class TableSettings {
		private TableSettings() {}
		public static final String TABLE_NAME="settings";
		public static final String KEY="key";
		public static final String VALUE="value";
		public static final String APPLICATION_DIRECTORY="applicationDirectory";
	}
	
	public class TableFeed {
		private TableFeed() {}
		public static final String TABLE_NAME="feed";
		public static final String ID="_id";
		public static final String URL="url";
		public static final String TITLE="title";
		public static final String IMAGE="image";
	}
	
	public class TableEpisode {
		private TableEpisode() {}
		public static final String TABLE_NAME="episode";
		public static final String ID="_id";
		public static final String FEED_ID="feedId";
		public static final String URL="url";
		public static final String TITLE="title";
		public static final String DESCRIPTION="description";
		public static final String AUTHOR="author";
		public static final String PUBLISHED_DATE="publishedDate";
		public static final String LOCAL_FILE="localFile";
	}
	
	public class TableFile {
		private TableFile() {}
		public static final String TABLE_NAME="file";
		public static final String ID="_id";
		public static final String EPISODE_ID="episodeId";
		public static final String PATH="path";
		public static final String PLAY_INDEX="playIndex";
	}
	
	
//	public enum TableEpisode {
//		TABLE_NAME("episode"), ID("_id"), FEED_ID("feedId"), URL("url"), 
//		TITLE("title"), DESCRIPTION("description"), AUTHOR("author"), 
//		PUBLISHED_DATE("publishedDate"), LOCAL_FILE("localFile");
//		
//		private String mText;
//		
//		private TableEpisode(String text) {
//			mText = text;
//		}
//		
//		@Override 
//		public String toString() {
//			return mText;
//		}
//	}
//	public enum TableQueue {
//		ID, EPISODE_ID, PLAY_INDEX
//	}
	
	//private SQLiteDatabase mDb;
	private DictionaryOpenHelper mHelper;
	
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
		
		private static final String EPISODE_CREATE =
				"CREATE TABLE episode (" +
						"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"feedId INTEGER, " +
						"url TEXT, " +
						"title TEXT, " +
						"description TEXT, " +
						"author TEXT, " +
						"publishedDate TEXT);";
		
		private static final String FILE_CREATE = 
				"CREATE TABLE file (" +
						"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"episodeId INTEGER, " +
						"path STRING, " +
						"playIndex TEXT);";
		
		//Queue: localPath, playIndex, order? 

		DictionaryOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SETTINGS_CREATE);
			db.execSQL(FEED_CREATE);
			db.execSQL(EPISODE_CREATE);
			db.execSQL(FILE_CREATE);
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
		mHelper = new DictionaryOpenHelper(context);
		//mDb = helper.getWritableDatabase();
	}
	
	//public void close() {
	//	mDb.close();
	//}
	
	public long addFeed(String url, String title, String imageUrl) {
		ContentValues insertValues = new ContentValues();
		insertValues.put(TableFeed.URL, url);
		insertValues.put(TableFeed.TITLE, title);
		insertValues.put(TableFeed.IMAGE, imageToBuffer(imageUrl));
		return mHelper.getWritableDatabase().insert(TableFeed.TABLE_NAME, null, insertValues);
	}
	
	public long addEpisode(long feedId, String episodeUrl, String title, String description,
			String author, String publishedDate) {
		ContentValues insertValues = new ContentValues();
		insertValues.put(TableEpisode.FEED_ID, feedId);
		insertValues.put(TableEpisode.URL, episodeUrl);
		insertValues.put(TableEpisode.TITLE, title);
		insertValues.put(TableEpisode.DESCRIPTION, description);
		insertValues.put(TableEpisode.AUTHOR, author);
		insertValues.put(TableEpisode.PUBLISHED_DATE, publishedDate);
		return mHelper.getWritableDatabase().insert(TableEpisode.TABLE_NAME, null, insertValues);
	}
	
	public Cursor getFeeds() {
		String[] columns = new String[] {TableFeed.ID, TableFeed.URL, TableFeed.TITLE, TableFeed.IMAGE};
		Cursor cursor = mHelper.getReadableDatabase().query(TableFeed.TABLE_NAME, columns, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public Cursor getFeed(String feedId) {
		String[] columns = new String[] {TableFeed.ID, TableFeed.URL, TableFeed.TITLE, TableFeed.IMAGE};
		String where = TableFeed.ID + "=?";
		String[] whereArgs = new String[] {feedId};
		Cursor cursor = mHelper.getReadableDatabase().query(TableFeed.TABLE_NAME, columns, where, whereArgs, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public Cursor getEpisodes(String feedId) {
		String[] columns = new String[] {TableEpisode.ID, TableEpisode.FEED_ID, TableEpisode.URL, TableEpisode.TITLE, 
				TableEpisode.DESCRIPTION, TableEpisode.AUTHOR, TableEpisode.PUBLISHED_DATE};
		String where = TableEpisode.FEED_ID + "=?";
		String[] whereArgs = new String[] {feedId};
		Cursor cursor = mHelper.getReadableDatabase().query(TableEpisode.TABLE_NAME, columns, where, whereArgs, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public Cursor getEpisode(String episodeId) {
		String[] columns = new String[] {TableEpisode.ID, TableEpisode.FEED_ID, TableEpisode.URL, TableEpisode.TITLE, 
				TableEpisode.DESCRIPTION, TableEpisode.AUTHOR, TableEpisode.PUBLISHED_DATE};
		String where = TableEpisode.ID + "=?";
		String[] whereArgs = new String[] {episodeId};
		Cursor cursor = mHelper.getReadableDatabase().query(TableEpisode.TABLE_NAME, columns, where, whereArgs, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public void removeFeed(String feedId) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		SQLiteStatement statement = db.compileStatement("DELETE FROM " + TableEpisode.TABLE_NAME + " WHERE " +
				TableEpisode.FEED_ID + "=?");
		statement.bindString(1, feedId);
		statement.executeUpdateDelete();
		statement = db.compileStatement("DELETE FROM " + TableFeed.TABLE_NAME + " WHERE " + TableFeed.ID + "=?");
		statement.bindString(1, feedId);
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
		SQLiteDatabase db = mHelper.getWritableDatabase();
		SQLiteStatement statement = db.compileStatement("SELECT " + TableSettings.VALUE + 
				" FROM " + TableSettings.TABLE_NAME + " WHERE " + TableSettings.KEY + "='" +
				TableSettings.APPLICATION_DIRECTORY + "'");
		String dir;
		try {
			dir = statement.simpleQueryForString();
		} catch (SQLiteDoneException e) {
			dir = decipherApplicationDirectory();
			ContentValues insertValues = new ContentValues();
			insertValues.put(TableSettings.KEY, TableSettings.APPLICATION_DIRECTORY);
			insertValues.put(TableSettings.VALUE, dir);
			db.insert(TableSettings.TABLE_NAME, null, insertValues);
		}
		return dir;
	}
	
	private String decipherApplicationDirectory() {
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
