package com.falconware.falconcatcher;

import java.io.File;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class DownloadService extends IntentService {
	public static final String ACTION_DOWNLOAD = "com.falconware.action.DOWNLOAD";
	public static final String ACTION_SCHEDULE = "com.falconware.action.SCHEDULE";
	
	private DownloadManager manager;
	
	public DownloadService() {
		super("DownloadService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		
		//registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if (action.equals(ACTION_DOWNLOAD)) {
			String episodeTitle = intent.getStringExtra("episodeTitle");
			downloadEpisode(episodeTitle);
		}
		else if (action.equals(ACTION_SCHEDULE)) {
			
		}
	}
	
	//private long downloadEpisode(String feedTitle, String episodeTitle, String url) {
	private void downloadEpisode(String episodeTitle) {
		Database db = new Database(this);
		Cursor episodeCursor = db.getEpisode(episodeTitle);
		String url = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.URL));		
		String feedTitle = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.FEED_TITLE));
		String feedDir = sanitizePath(feedTitle);
		episodeCursor.close();
		
		String downloadDirectory = new Database(this).getApplicationDirectory() + feedDir + "/";
		File downloadDir = new File(downloadDirectory);
		if (!downloadDir.isDirectory() && downloadDir.mkdirs() == false) {
			System.err.println("ERROR!  Can't create feed directory.");
			System.err.println("Tried to create: " + downloadDir);
		}
		Uri currentUri = Uri.parse(url);
		String filename = sanitizePath(currentUri.getLastPathSegment());
		
		//TODO: Check that URI is valid.  DownloadManager is too stupid to know that a URI is invalid
		final String finalDestination = downloadDir + "/" + filename;
		DownloadManager.Request request = new DownloadManager.Request(currentUri);		
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		request.setDestinationUri(Uri.fromFile(new File(finalDestination)));
		request.setTitle(getString(R.string.app_name));
		request.setDescription(feedDir + ": " + episodeTitle);
		System.out.println("Download location: " + finalDestination);
		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);	
		

		
		
		//registerReceiver(onNotificationClick, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
		//AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		//Intent intent = new Intent(this, CompleteAlarm.class);
		//alarm.set(AlarmManager.RTC, triggerAtMillis, operation)
		
		manager.enqueue(request);
	}
	
	private String sanitizePath(String title) {
		return title.replaceAll("[\"|\\\\?*<\":>+\\[\\]/']", "");
	}
	




}
