package com.falconware.falconcatcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class ReaderParser
{
	public ArrayList<Map<String,String> > parseReader(){
		String readerJson = "";
    	try {
    		Scanner scanner = new Scanner(new FileInputStream("/mnt/sdcard/download/testFeeds/readerList.json"));
    		StringBuilder builder = new StringBuilder();
    		while (scanner.hasNext()) {
    			builder.append(scanner.next());
    		}
    		readerJson = builder.toString();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    		//finish();
    		return null;
    	}
    	
    	try {
    		JSONObject object = (JSONObject)new JSONTokener(readerJson).nextValue();
    		JSONArray subscriptions = object.getJSONArray("subscriptions");
    		ArrayList<Map<String,String> > entryList = new ArrayList<Map<String, String> >();
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
    			entryList.add(entry);    			
    		}
    		return entryList;
    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    		//finish();
    		return null;
    	} 
    	//return null;
	}
}
