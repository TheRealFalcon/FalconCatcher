package com.falconware.falconcatcher;

import java.io.File;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

public class DownloadService extends IntentService {
	public DownloadService(String name) {
		super(name);
	}

	public static final String ACTION_DOWNLOAD = "com.falconware.action.DOWNLOAD";
	public static final String ACTION_SCHEDULE = "com.falconware.action.SCHEDULE";
	
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//    	final int id = startId;
//    	new AsyncTask<Intent, Void, Void>() {
//			@Override
//			protected Void doInBackground(Intent... intents) {
//				Intent intent = intents[0];
//				handleIntent(intent, id);
//				return null;
//			}
//		}.execute(intent);
//    	return START_REDELIVER_INTENT;
//    }

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if (action.equals(ACTION_DOWNLOAD)) {
			String episodeId = intent.getStringExtra("episodeId");
			downloadEpisode(episodeId);
		}
		else if (action.equals(ACTION_SCHEDULE)) {
			
		}
	}
	
	//private long downloadEpisode(String feedTitle, String episodeTitle, String url) {
	private void downloadEpisode(String episodeId) {
		Database db = new Database(this);
		Cursor episodeCursor = db.getEpisode(episodeId);
		String episodeTitle = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.TITLE));
		String url = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.URL));
		
		String feedId = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.FEED_ID));
		Cursor feedCursor = db.getFeed(feedId);
		String feedTitle = feedCursor.getString(feedCursor.getColumnIndex(Database.TableFeed.ID));
		
		String downloadDirectory = new Database(this).getApplicationDirectory() + feedTitle + "/";
		File downloadDir = new File(downloadDirectory);
		downloadDir.mkdirs();
		Uri currentUri = Uri.parse(url);
		String filename = currentUri.getLastPathSegment();
		
		final String finalDestination = downloadDir + "/" + filename;
		DownloadManager.Request request = new DownloadManager.Request(currentUri);		
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);		
		request.setDestinationUri(Uri.fromFile(new File(finalDestination)));
		request.setTitle("FalconCatcher");
		request.setDescription(feedTitle + ": " + episodeTitle);
		System.out.println("Download location: " + finalDestination);
		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);		
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println(finalDestination);
			}		
		}, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		//registerReceiver(onNotificationClick, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
		manager.enqueue(request);
	}
	
//	private static BroadcastReceiver onComplete = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//						
//		}		
//	};


}
