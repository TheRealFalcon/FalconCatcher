package com.falconware.falconcatcher;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class DownloadReceiver extends BroadcastReceiver {// = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("In onReceive");
		DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
		Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(id));
		cursor.moveToFirst();
		System.out.println("Column title: " + cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)));
		System.out.println("R.string.app_name: " + context.getString(R.string.app_name));
		if (!cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)).equals(context.getString(R.string.app_name))) {
			//Not our app!
			System.out.println("Not our app!");
			return;
		}
		if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
			String description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
			String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
			//String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
			System.out.println("Download complete!");
			System.out.println("Description: " + description);
			//System.out.println("Filename: " + filename);
			//System.out.println("URI: " + uri);
			//TODO: Is there a better way to do this?  This seems like it'd be really fragile...
			String episodeName = description.substring(description.indexOf(':') + 2);
			Database db = new Database(context);
			System.out.println("Adding file");
			db.addFile(episodeName, path);
			System.out.println("File (hopefully) added!");
		}
	}	
};