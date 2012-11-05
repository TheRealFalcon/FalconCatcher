package com.falconware.falconcatcher;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.falconware.falconcatcher.Database.TableFile;

public class PlayerService extends Service {
	public static final String ACTION_PLAY_LOCAL = "com.falconware.action.PLAY_LOCAL";
	public static final String ACTION_PAUSE = "com.falconware.action.PAUSE";
	public static final String ACTION_RESUME = "com.falconware.action.RESUME";
	public static final String ACTION_FAST_FORWARD = "com.falconware.action.FAST_FORWARD";
	public static final String ACTION_REWIND = "com.falconware.action.REWIND";
	public static final String ACTION_NEXT_TRACK = "com.falconware.action.NEXT_TRACK";
	public static final String ACTION_LEAVE_UI = "com.falconware.action.LEAVE_UI";
	public static final String ACTION_CONNECT_UI = "com.falconware.action.RESUME_UI";
	private static final int NOTIFICATION_ID = 1;
	private static final int BAD_ID = -1;
	
	//Currently (maybe permenantly?) just hardcoding these
	private static final int FAST_FORWARD_AMOUNT = 30000;  //Move ahead 30 seconds when fast forwarding
	private static final int REWIND_AMOUNT = 10000;  //Move back 10 seconds when rewinding
	private static final int FAST_FOWARD_TOLERANCE = 5000; //Don't do the operation if it'll put you within 5 seconds of the end of the file.
	
	
	private final IBinder mBinder = new LocalBinder();	
	private MediaPlayer mPlayer;
	
	private Database mDb;
	private long mCurrentFileId;
	private OnStateChangeListener mStateListener;
	private boolean mReady;
	//private OnTrackChangeListener mTrackListener;
	
    
    @Override
    public void onCreate() {
    	super.onCreate();
    	System.out.println("In PlayerService onCreate()");
    	mReady = false;
    	mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mDb = new Database(this);
		mCurrentFileId = BAD_ID;
		mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
					System.err.println("MediaPlayer server died!");
				}
				else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
					System.err.println("Media Error unknown!");
				}
				else if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
					System.err.println("MediaPlayer error not valid for progressive playback!");
				}
				System.err.println("Now what do you do?");
				return true;
			}
		});
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	//final int id = startId;
//    	new AsyncTask<Intent, Void, Void>() {
//
//			@Override
//			protected Void doInBackground(Intent... intents) {
//				Intent intent = intents[0];
//				handleIntent(intent, id);
//				return null;
//			}
//		}.execute(intent);
    	handleIntent(intent, startId);
		if (!mPlayer.isPlaying()) {
			stopSelf(startId);
		}
    	return START_STICKY;
    }

	
	protected void handleIntent(Intent intent, int id) {
		if (intent == null) {
			return;
		}
		String intentAction = intent.getAction();
		if (intentAction.equals(ACTION_PLAY_LOCAL)) {
			long fileId = intent.getLongExtra("id", BAD_ID);			
			if (fileId == BAD_ID) {
				System.err.println("Didn't get id of file to play...die");
				return;
			}
			mCurrentFileId = fileId;
			if (fileId == mDb.getCurrentFile()) {
				if (mPlayer.isPlaying()) {
					//They just selected an already playing track
					return;
				}
				prepareFile(mDb.getFilePosition(fileId));
			}			
			else {
				mDb.setCurrentFile(fileId);			
				prepareFile();
			}
			playFile();
		}
		else if (intentAction.equals(ACTION_RESUME)) {
			//Need to reinitialize media player
			if (mCurrentFileId == BAD_ID) {
				mCurrentFileId = mDb.getCurrentFile();
				prepareFile(getCurrentPosition());
			}
			playFile();
		}
		else if (intentAction.equals(ACTION_PAUSE)) {
			mPlayer.pause();
			mStateListener.setPlaying(false, -1);
			//saveCurrentPosition();
		}
		else if (intentAction.equals(ACTION_FAST_FORWARD) || intentAction.equals(ACTION_REWIND)) {
			int currentPosition = 0;
			int endPosition = 0;
			if (mCurrentFileId == BAD_ID) {
				mCurrentFileId = mDb.getCurrentFile();
				if (mCurrentFileId == BAD_ID) {
					return;
				}
				currentPosition = getCurrentPosition();
				prepareFile(currentPosition);
				endPosition = mPlayer.getDuration();
			}
			else {
				currentPosition = mPlayer.getCurrentPosition();
				endPosition = mPlayer.getDuration();
			}
			if (endPosition == 0) {
				System.err.println("Attempted to seek but track length is 0.  This shouldn't be happening");
				return;
			}
			
			if (intentAction.equals(ACTION_FAST_FORWARD)) {
				if (currentPosition + FAST_FORWARD_AMOUNT + FAST_FOWARD_TOLERANCE > endPosition) {
					return;
				}
				int newPosition = currentPosition + FAST_FORWARD_AMOUNT;
				mPlayer.seekTo(newPosition);
				mStateListener.setSeekPosition(newPosition);
			}
			else if (intentAction.equals(ACTION_REWIND)) {
				int newPosition = 0;
				if (currentPosition - REWIND_AMOUNT > 0) {
					newPosition = currentPosition - REWIND_AMOUNT;
				}
				mPlayer.seekTo(newPosition);
				mStateListener.setSeekPosition(newPosition);			
			}
		}
		else if (intentAction.equals(ACTION_NEXT_TRACK)) {
//			if (mCurrentFileId == BAD_ID) {
//				mCurrentFileId = mDb.getCurrentFile();
//				if (mCurrentFileId == BAD_ID) {
//					return;
//				}				
//			}
			
		}
		else if (intentAction.equals(ACTION_LEAVE_UI)) {
			mStateListener.unbind();
			if (mPlayer.isPlaying()) {
				//mStateListener.unbind();
			}
			else {
				saveCurrentPosition();
				//mPlayer.release();
				//mPlayer = null;
				//stopSelf(id);
			}
		}
		else if (intentAction.equals(ACTION_CONNECT_UI)) {
			int currentPosition = 0;
			if (mCurrentFileId == BAD_ID) {
				mCurrentFileId = mDb.getCurrentFile();
				if (mCurrentFileId == BAD_ID) {
					return;
				}
				currentPosition = getCurrentPosition();
				prepareFile(currentPosition);
			}
			else {
				currentPosition = getCurrentPosition();
			}
			//mStateListener.bind(mPlayer.getDuration());
//			while (mStateListener == null) {
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			if (mStateListener != null) {
				mStateListener.setPlaying(mPlayer.isPlaying(), mPlayer.getDuration());
				mStateListener.setSeekPosition(currentPosition);			
			}
		}
	}
	
	private void prepareFile() {
		prepareFile(0);
	}
	
	private void prepareFile(int currentPos) {
		mReady = false;
		Cursor fileCursor = mDb.getFile(mCurrentFileId);
		String episodeName = fileCursor.getString(fileCursor.getColumnIndex(TableFile.EPISODE_TITLE));
		setNotification(episodeName);		
		String filename = fileCursor.getString(fileCursor.getColumnIndex(TableFile.PATH));
		fileCursor.close();
		System.out.println("Preaparing: " + filename);
		
		if (filename != null) {
			try {
				mPlayer.reset();
				mPlayer.setDataSource(filename);
				//TODO: prepareAsync for web streams
				mPlayer.prepare();

			} catch (IOException e) {
				//TODO: Need better exception handling here
				e.printStackTrace();			
			}
		}
		else {
			System.err.println("Can't play file because filename is null!");
			return;
		}
		mPlayer.seekTo(currentPos);
		mReady = true;
	}
	
	private void playFile() {
		mPlayer.start();
		if (mStateListener != null) {
			mStateListener.setPlaying(true, mPlayer.getDuration());	
		}
		//mTrackListener.updateUi(mPlayer.getDuration());
	}
	
	private void saveCurrentPosition() {
		if (mCurrentFileId == BAD_ID) {
			System.err.println("mCurrentFileId == BAD_ID, cannot save track position");
			return;
		}
		int pos = mPlayer.getCurrentPosition();
		mDb.setFilePosition(mCurrentFileId, pos);
	}
	
	private int getCurrentPosition() {
		if (mCurrentFileId == BAD_ID) {
			System.err.println("mCurrentFileId == BAD_ID, cannot get track position");
			return BAD_ID;
		}
		return mDb.getFilePosition(mCurrentFileId);
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void setNotification(String episodeName) {
		Notification notification;
		if (Build.VERSION.SDK_INT < 16) {
			notification = new Notification.Builder(getApplicationContext())
			.setContentTitle(getString(R.string.app_name))
			.setContentText(episodeName)
			.setSmallIcon(android.R.drawable.ic_media_play)
			.getNotification();
		}
		else {
			notification = new Notification.Builder(getApplicationContext())
			.setContentTitle(getString(R.string.app_name))
			.setContentText(episodeName)
			.setSmallIcon(android.R.drawable.ic_media_play)
			.build();
		}
		startForeground(NOTIFICATION_ID, notification);
	}
	
	public MediaPlayer getPlayer() {
		return mPlayer;
	}
	
	public boolean isReady() {
		return mReady;
	}
	
	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}
	
	@Override
	public void onDestroy() {
		mReady = false;
		if (mPlayer != null) {
			mPlayer.release();
		}
	}
	
	public class LocalBinder extends Binder {
		PlayerService getService() {
			return PlayerService.this;
		}
		
		void setOnStateChangeListener(OnStateChangeListener listener) {
			PlayerService.this.mStateListener = listener;
		}
		
//		void setOnTrackChangeListener(OnTrackChangeListener listener) {
//			PlayerService.this.mTrackListener = listener;
//		}
	}
	
	public interface OnStateChangeListener {
		public void setPlaying(boolean playing, int endPos);
		public void unbind();
		//public void bind(int endPos);
		public void setSeekPosition(int currentPos);
	}
	
//	public interface OnTrackChangeListener {
//		//TODO: Update to accomodate rest screen changes needed
//		public void updateUi(int endPos);
//	}
}
