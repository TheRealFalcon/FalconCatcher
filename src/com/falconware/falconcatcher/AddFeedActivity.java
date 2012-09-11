package com.falconware.falconcatcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class AddFeedActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subscription_dialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.jj, menu);
        return true;
    }
    
    public void onSearchPressed(View view) {
    	Database db = new Database(this);
    	EditText text = (EditText)findViewById(R.id.text_add_dialog);
    	System.out.println(text.getText().toString());
    	new DownloadFeedTask(this, db).execute(text.getText().toString());
    	//final View layout = View.inflate(this, R.layout.add_subscription_popup, null);
    	//final Activity activity = this;
    	//AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	//alert.setView(layout)
    	//.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	//	@Override
    	//	public void onClick(DialogInterface dialog, int which) {
    	//		Database db = new Database(AddFeedActivity.this);
    	//		new DownloadFeedTask(activity, db).execute("http://10.0.2.2:8080/freakonomics.xml");				
    	//	}
        //
    	//})
    	//.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	//	@Override
    	//	public void onClick(DialogInterface dialog, int which) {
    	//		// TODO Auto-generated method stub
        //
    	//	}
        //
    	//})
    	//.show();
    }
    
    public void onGooglePressed(View view) {
    	
    	//ListView newView = new ListView(this);
    	//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
    	//newView.setAdapter(adapter);
    	Intent intent = new Intent(this, GoogleSelectionActivity.class);
    	//intent.putExtra("categories", categories);
    	startActivity(intent);
    	//newView.set
    	
    }
    
}