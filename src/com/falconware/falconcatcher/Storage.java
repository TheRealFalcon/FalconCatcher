package com.falconware.falconcatcher;

import java.io.File;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.Toast;

public class Storage {
	//private static final String APPLICATION_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/FalconCatcher/";
	

	
	private static BroadcastReceiver onNotificationClick = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "onNotificationClick onReceive", Toast.LENGTH_LONG).show();			
		}
		
	};
	

}

//boolean mExternalStorageAvailable = false;
//boolean mExternalStorageWriteable = false;
//String state = Environment.getExternalStorageState();
//
//if (Environment.MEDIA_MOUNTED.equals(state)) {
//    // We can read and write the media
//    mExternalStorageAvailable = mExternalStorageWriteable = true;
//} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//    // We can only read the media
//    mExternalStorageAvailable = true;
//    mExternalStorageWriteable = false;
//} else {
//    // Something else is wrong. It may be one of many other states, but all we need
//    //  to know is we can neither read nor write
//    mExternalStorageAvailable = mExternalStorageWriteable = false;
//}