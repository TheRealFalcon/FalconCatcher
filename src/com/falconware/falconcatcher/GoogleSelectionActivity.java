package com.falconware.falconcatcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class GoogleSelectionActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ListView view = new ListView(this);
		//String[] categories = getIntent().getStringArrayExtra("categories");
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
		//view.setAdapter(adapter);
		//setContentView(view);
		
		
		//http://www.google.com/reader/api/0/subscription/list?output=json
    	List<Map<String,String> > entryList = parseReader();
    	Set<String> categorySet = new TreeSet<String>();
    	List<String> noCategoryList = new LinkedList<String>();
    	for (Map<String,String> entry : entryList) {
    		String category = entry.get("category");
    		if (category.isEmpty()) {
    			noCategoryList.add(entry.get("title"));
    		}
    		else {
    			categorySet.add(category);
    		}
    	}
    	Collections.sort(noCategoryList);
    	
    	List<Map<String,String> > displayList = new LinkedList<Map<String,String> >();
    	for (String category : categorySet) {
    		Map<String,String> entryMap = new HashMap<String,String>();
    		entryMap.put("display", category);
    		displayList.add(entryMap);
    	}
    	for (String entry : noCategoryList) {
    		Map<String,String> entryMap = new HashMap<String,String>();
    		entryMap.put("display", entry);
    		displayList.add(entryMap);
    	}
    	SimpleAdapter adapter = new SimpleAdapter(this, displayList, R.layout.google_selection_dialog, new String[] {"display"}, new int[] {R.id.google_folder_view});
    	view.setAdapter(adapter);
    	System.out.println("hi");
    	setContentView(view);
    	
	}
	
	private List<Map<String,String> > parseReader() {
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
    		finish();
    	}
    	
    	try {
    		JSONObject object = (JSONObject)new JSONTokener(readerJson).nextValue();
    		JSONArray subscriptions = object.getJSONArray("subscriptions");
    		List<Map<String,String> > entryList = new LinkedList<Map<String, String> >();
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
    		finish();
    	} 
    	return null;
	}
}
