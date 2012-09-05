package com.falconware.falconcatcher;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

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
    	//http://www.google.com/reader/api/0/subscription/list
    	//JSONObject object = (JSONObject)new JSONTokener("");
    	
    }
}
