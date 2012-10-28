package com.falconware.falconcatcher;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;


public class ReaderParser extends AsyncTask<Void, Void, Boolean>
{
	//private Context mContext;
	private OnTaskCompleted mListener;
	private ArrayList<Map<String,String> > mEntryList;
	private ProgressDialog mDialog;
	private String mToken;
	
	
	public ReaderParser(AddFeedActivity caller, ArrayList<Map<String,String> > entryList, String token) {
		//mContext = caller;
		mListener = caller;
		mEntryList = entryList;
		mToken = token;
	}
	
	@Override
	protected void onPreExecute() {
		
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		return parseReader();
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		
		if (mListener != null) {
			mListener.onTaskCompleted(result);
		}
	}
	
	private boolean parseReader() {
		String readerJson = "";
    	try {
    		URL url = new URL("https://www.google.com/reader/api/0/subscription/list?output=json");
    		//URL url = new URL("https://www.google.com/reader/api/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			//conn.addRequestProperty("client_id", "Looks like I don't even need this...");
			conn.setRequestProperty("Authorization", "OAuth " + mToken);
			
			conn.connect();
			
			BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
    		Scanner scanner = new Scanner(in);
			
			//Scanner scanner = new Scanner(new FileInputStream("/mnt/sdcard/download/testFeeds/readerList.json"));
			//Scanner can only grab so much data at once, so immediately scanning to the end of the file
			//won't work.  Here's a dumb workaround.
			
    		StringBuilder builder = new StringBuilder();
    		while (scanner.hasNext()) {
    			builder.append(scanner.next());
    		}
    		readerJson = builder.toString();
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	
    	try {
    		JSONObject object = (JSONObject)new JSONTokener(readerJson).nextValue();
    		JSONArray subscriptions = object.getJSONArray("subscriptions");
    		//ArrayList<Map<String,String> > entryList = new ArrayList<Map<String, String> >();
    		for (int index=0; index<subscriptions.length(); index++) {
    			Map<String,String> entry = new HashMap<String,String>();
    			JSONObject jsonEntry = subscriptions.getJSONObject(index);
    			entry.put("id", jsonEntry.getString("id").substring(5));  //clear the starting "feed/"
    			String entryTitle = jsonEntry.getString("title");
    			entry.put("title", entryTitle);
    			JSONObject categoryArray = jsonEntry.getJSONArray("categories").optJSONObject(0);
    			String category = "";
    			if (categoryArray != null) {
    				category = categoryArray.getString("label");
    			}
    			entry.put("category", category);
    			mEntryList.add(entry);    			
    		}
    		return true;
    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    		return false;
    	} 
	}
}
