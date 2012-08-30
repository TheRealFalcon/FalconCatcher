package com.falconware.falconcatcher;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

public class PlayerService extends Service {
	public static final String ACTION_PLAY = "com.falconware.action.PLAY";
	public static final String ACTION_PAUSE = "com.falconware.action.PAUSE";
	private static final int NOTIFICATION_ID = 1;
	private final IBinder mBinder = new LocalBinder();
	
	private MediaPlayer mPlayer;
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	new AsyncTask<Intent, Void, Void>() {

			@Override
			protected Void doInBackground(Intent... intents) {
				Intent intent = intents[0];
				handleIntent(intent);
				return null;
			}
		}.execute(intent);
    	return START_STICKY;
    }

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	protected void handleIntent(Intent intent) {
		String intentAction = intent.getAction();
		if (intentAction.equals(ACTION_PLAY)) {
			Notification notification;
			if (Build.VERSION.SDK_INT < 16) {
				notification = new Notification.Builder(getApplicationContext())
				.setContentTitle("Notification title")
				.setContentText("Notification text")
				.setSmallIcon(android.R.drawable.ic_media_play)
				.getNotification();
			}
			else {
				notification = new Notification.Builder(getApplicationContext())
				.setContentTitle("Notification title")
				.setContentText("Notification text")
				.setSmallIcon(android.R.drawable.ic_media_play)
				.build();
			}
			startForeground(NOTIFICATION_ID, notification);
			
//			notification.tickerText = "tickerText";
//			notification.icon = android.R.drawable.ic_media_play;
//			notification.flags |= Notification.FLAG_ONGOING_EVENT;
//			notification.setLatestEventInfo(getApplicationContext(), "FalconCatcher", "Playing stuff", PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
//			
			String filename = intent.getStringExtra("filename");
			if (filename == null) {
				if (!mPlayer.isPlaying()) {
					mPlayer.start();
				}
			}
			else {
				try {
					mPlayer.reset();
					mPlayer.setDataSource(filename);
					//TODO: prepareAsync for web streams
					mPlayer.prepare();
					mPlayer.start();
				} catch (IOException e) {
					e.printStackTrace();			
				}	
			}			
		}
		else if (intentAction.equals(ACTION_PAUSE)) {
			mPlayer.pause();
		}
		
	}
	
	@Override
	public void onDestroy() {
		//do something
	}
	
	public class LocalBinder extends Binder {
		PlayerService getService() {
			return PlayerService.this;
		}
	}
}
