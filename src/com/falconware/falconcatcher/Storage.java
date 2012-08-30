package com.falconware.falconcatcher;

import java.io.File;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class Storage {
	//private static final String APPLICATION_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/FalconCatcher/";
	
	public static long downloadEpisode(Context context, String feedTitle, String episodeTitle, String url) {
		String downloadDirectory = new Database(context).getApplicationDirectory() + feedTitle + "/";
		File downloadDir = new File(downloadDirectory);
		downloadDir.mkdirs();
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		//TODO: Won't always be mp3 extension
		System.out.println("Downloaded file: " + downloadDir + "/" + episodeTitle + ".mp3");
		request.setDestinationUri(Uri.fromFile(new File(downloadDir + "/" + episodeTitle + ".mp3")));
		request.setTitle("FalconCatcher");
		request.setDescription(feedTitle + ": " + episodeTitle);
		DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		return manager.enqueue(request);
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