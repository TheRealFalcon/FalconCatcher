package com.falconware.falconcatcher;

import java.io.File;

import android.os.Environment;

public class Storage {
	private static final File APPLICATION_DIRECTORY = Environment.getExternalStorageDirectory();
	
	public static void downloadEpisode(String feedTitle, String episodeTitle, String url) {
		System.out.println(APPLICATION_DIRECTORY);
	}
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