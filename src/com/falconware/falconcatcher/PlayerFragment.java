package com.falconware.falconcatcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.falconware.falconcatcher.PlayerService.LocalBinder;

public class PlayerFragment extends Fragment {
	private PlayerService mService;
	private Activity mActivity;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			System.out.println("service connected");
			//PlayerFragment.this.setButtonState();
			binder.setOnStateChangeListener(stateChangeListener);	
			
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			playerIntent.setAction(PlayerService.ACTION_CONNECT_UI);
			mActivity.startService(playerIntent);
//			binder.setOnTrackChangeListener(new PlayerService.OnTrackChangeListener() {				
//				@Override
//				public void updateUi(final int endPos) {
//					// TODO Auto-generated method stub
//					PlayerFragment.this.getActivity().runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							((SeekBar)mActivity.findViewById(R.id.seekBar)).setMax(endPos);
//						}
//					});
//				}
//			});
		}
		
		@Override
		public void onServiceDisconnected(ComponentName className) {
			System.out.println("In onServiceDisconnected");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		System.out.println("in onStart");
		//setButtonState();
		
		Intent intent = new Intent(mActivity, PlayerService.class);
		mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		
		
		

	}
	
	@Override
	public void onStop() {
		System.out.println("in onStop");		
		Intent playerIntent = new Intent(mActivity, PlayerService.class);
		playerIntent.setAction(PlayerService.ACTION_LEAVE_UI);
		mActivity.startService(playerIntent);
		mActivity.unbindService(mConnection);
		super.onStop();
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		System.out.println("in onResume");
//		
//		Intent intent = new Intent(mActivity, PlayerService.class);
//		mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//		
//		Intent playerIntent = new Intent(mActivity, PlayerService.class);
//		playerIntent.setAction(PlayerService.ACTION_CONNECT_UI);
//		mActivity.startService(playerIntent);			
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.player_fragment, container, false);
		
		//Not using xml callbacks because it looks for them in the activity...
		Button playPauseButton = (Button)layout.findViewById(R.id.play_or_pause_button);
		playPauseButton.setOnClickListener(playOrPauseTrack);
		
		Button fastForwardButton = (Button)layout.findViewById(R.id.fast_forward_button);
		fastForwardButton.setOnClickListener(fastForwardTrack);
		
		Button rewindButton = (Button)layout.findViewById(R.id.rewind_button);
		rewindButton.setOnClickListener(rewindTrack);
		
		Button nextButton = (Button)layout.findViewById(R.id.next_button);
		nextButton.setOnClickListener(nextTrack);
		
		SeekBar bar = (SeekBar)layout.findViewById(R.id.seekBar);
		bar.setOnSeekBarChangeListener(changeSeek);
		

		return layout;
	}
	
//	private void setButtonState() {
//		if (mService == null || mActivity.findViewById(R.id.play_or_pause_button) == null) {
//			return;
//		}
//		
//		boolean isPlaying = mService.getPlayer().isPlaying();
//		((ToggleButton)mActivity.findViewById(R.id.play_or_pause_button)).setChecked(isPlaying);
//	}
	
	private OnClickListener playOrPauseTrack = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			ToggleButton button = (ToggleButton)view;
			if (mService.getPlayer().isPlaying()) {
				button.setChecked(false);
				playerIntent.setAction(PlayerService.ACTION_PAUSE);
			}
			else {
				button.setChecked(true);
				playerIntent.setAction(PlayerService.ACTION_RESUME);				
			}
			mActivity.startService(playerIntent);
		}
	};
	
	private OnClickListener fastForwardTrack = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			playerIntent.setAction(PlayerService.ACTION_FAST_FORWARD);
			mActivity.startService(playerIntent);
		}
	};
	
	private OnClickListener rewindTrack = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			playerIntent.setAction(PlayerService.ACTION_REWIND);
			mActivity.startService(playerIntent);
		}
	};
	
	private OnClickListener nextTrack = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			playerIntent.setAction(PlayerService.ACTION_NEXT_TRACK);
			mActivity.startService(playerIntent);
		}
	};
	
	private OnSeekBarChangeListener changeSeek = new OnSeekBarChangeListener() {
		private boolean wasPlaying;
		private MediaPlayer player;
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (wasPlaying) {
				//player.start();
				Intent playerIntent = new Intent(mActivity, PlayerService.class);
				playerIntent.setAction(PlayerService.ACTION_RESUME);
				mActivity.startService(playerIntent);
			}
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			player = mService.getPlayer();
			wasPlaying = player.isPlaying();
			if (wasPlaying) {
				Intent playerIntent = new Intent(mActivity, PlayerService.class);
				playerIntent.setAction(PlayerService.ACTION_PAUSE);
				mActivity.startService(playerIntent);
				//player.pause();
			}			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				player.seekTo(progress);
				((TextView)mActivity.findViewById(R.id.timeElapsed)).setText(msToTime(progress));
			}
		}
	};
    
    public void nextTrack(View view) {
    	System.out.println("Next track");
    }
    
    public void fastForwardTrack(View view) {
    	System.out.println("Fast forward track");
    }
    
    private String msToTime(int ms) {
    	int timeInSeconds = ms / 1000;
    	int minutes = timeInSeconds / 60;
    	int seconds = timeInSeconds - (minutes * 60);
    	return "" + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
    
    private PlayerService.OnStateChangeListener stateChangeListener = new PlayerService.OnStateChangeListener() {	
		private Thread mUpdateThread = new Thread();
		private boolean mConnected;
		private final Handler mHandler = new Handler();
		//private final MediaPlayer player = mService.getPlayer();
		
		
		@Override
		public void setPlaying(final boolean playing, final int endPos) {		
//			int endInSeconds = endPos / 1000;  //ms to s
//			final int endMinutes = endInSeconds / 60;
//			final int endSeconds = endInSeconds - (endMinutes * 60);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					ToggleButton button = (ToggleButton)mActivity.findViewById(R.id.play_or_pause_button);					
					button.setChecked(playing);
					if (endPos > -1) {
						SeekBar bar = (SeekBar)mActivity.findViewById(R.id.seekBar);	
						bar.setMax(endPos);
						
						TextView end = (TextView)mActivity.findViewById(R.id.timeLeft);
						end.setText(msToTime(endPos));
					}
					if (playing) {
						//if (!mUpdateThread.isAlive()) {
						
						startUpdating();
							
						//}
					}
				}
			});	
		}
		
		@Override
		public void unbind() {
			//updateThread.interrupt();
			//mConnected = false;
			System.out.println("Calling unbind");
			mUpdateThread.interrupt();
		}
		
//		@Override
//		public void bind(int endPos) {
//			mConnected = true;
//			SeekBar bar = (SeekBar)mActivity.findViewById(R.id.seekBar);
//			if (bar.getMax() != endPos) {
//				System.out.println("Bar max: " + bar.getMax());
//				System.out.println("endPos: " + endPos);
//				bar.setMax(endPos);	
//			}
//			//setSeekPosition(currentPos, endPos);
//			//startUpdating();
//						
//		}
		
		@Override
		public void setSeekPosition(final int currentPos) { //, int endPos) {
//			int elapsedInSeconds = currentPos / 1000;  //ms to s
//			final int elapsedMinutes = elapsedInSeconds / 60;
//			final int elapsedSeconds = elapsedInSeconds - (elapsedMinutes * 60);
			mHandler.post(new Runnable() {
				@Override
				public void run() {					
					TextView elapsed = (TextView)mActivity.findViewById(R.id.timeElapsed);
					SeekBar bar = (SeekBar)mActivity.findViewById(R.id.seekBar);
					
					if (elapsed !=null && bar != null) {
						//TextView end = (TextView)mActivity.findViewById(R.id.timeLeft);
						elapsed.setText(msToTime(currentPos));
						//left.setText(bar.getMax())					
						
						bar.setProgress(currentPos);
					}
				}
			});
		}
		
		private void startUpdating() {			
			mUpdateThread = new Thread(new Runnable() {
				public void run() {		
					final MediaPlayer player = mService.getPlayer();
					if (player == null) {
						return;
					}
					//final SeekBar bar = (SeekBar)mActivity.findViewById(R.id.seekBar);
					//bar.setMax(player.getDuration());
					while (true) {
						if (mService.isReady() && mService.isPlaying()) {
							setSeekPosition(player.getCurrentPosition());
//							mHandler.post(new Runnable() {
//								public void run() {
//									//System.out.println("Once per second");								
//									bar.setProgress(player.getCurrentPosition());
//									System.out.println("bar max: " + bar.getMax());
//								}
//							});
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							return;
						}

					}
				}
			});
			mUpdateThread.setDaemon(true);
			mUpdateThread.start();
		}
	};
}
