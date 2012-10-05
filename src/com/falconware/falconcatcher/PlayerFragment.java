package com.falconware.falconcatcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.falconware.falconcatcher.PlayerService.LocalBinder;

public class PlayerFragment extends Fragment {
	private PlayerService mService;
	private Activity mActivity;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			PlayerFragment.this.setButtonState();
			//TODO: This seems smelly.  Verify this doesn't do screwy things after this view was destroyed.
			mService.getPlayer().setOnPreparedListener(new OnPreparedListener() {				
				@Override
				public void onPrepared(MediaPlayer player) {
					System.out.println("OnPrepared!");
					PlayerFragment.this.setButtonState();
				}
			});
			

		}
		
		@Override
		public void onServiceDisconnected(ComponentName className) {
			// TODO Auto-generated method stub
			
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
		Intent intent = new Intent(mActivity, PlayerService.class);
		mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

	}
	
	@Override
	public void onStop() {
		super.onStop();
		mActivity.unbindService(mConnection);
		Intent playerIntent = new Intent(mActivity, PlayerService.class);
		playerIntent.setAction(PlayerService.ACTION_DIE_IF_IDLE);
		mActivity.startService(playerIntent);		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setButtonState();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.player_fragment, container, false);
		
		//Not using xml callbacks because it looks for them in the acitivity...
		Button button = (Button)layout.findViewById(R.id.play_or_pause_button);
		button.setOnClickListener(new PlayOrPauseTrack());
		
		//setButtonState();
		Bundle bundle = getArguments();
		if (bundle != null) {
			boolean playing = getArguments().getBoolean("playing", false);
			if (playing) {
				((Button)layout.findViewById(R.id.play_or_pause_button)).setBackgroundResource(android.R.drawable.ic_media_pause);
			}
		}
		
		//Do rewind, fastforward, and next
		return layout;
	}
	
	private void setButtonState() {
		if (mService == null) {
			return;
		}
		
		if (mService.getPlayer().isPlaying()) {
			((Button)mActivity.findViewById(R.id.play_or_pause_button)).setBackgroundResource(android.R.drawable.ic_media_pause);
		}
		else {
			((Button)mActivity.findViewById(R.id.play_or_pause_button)).setBackgroundResource(android.R.drawable.ic_media_play);
		}
	}
	
	private class PlayOrPauseTrack implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			Button button = (Button)view;

			if (((String)button.getTag()).equals(getString(R.string.button_play))) {
				playerIntent.setAction(PlayerService.ACTION_PLAY);
				button.setBackgroundResource(android.R.drawable.ic_media_pause);
				button.setTag(getString(R.string.button_pause));    		
			}
			else {
				playerIntent.setAction(PlayerService.ACTION_PAUSE);
				button.setBackgroundResource(android.R.drawable.ic_media_play);
				button.setTag(getString(R.string.button_play));
			}
			mActivity.startService(playerIntent);
		}
	}
    
    public void nextTrack(View view) {
    	System.out.println("Next track");
    }
    
    public void fastForwardTrack(View view) {
    	System.out.println("Fast forward track");
    }
}
