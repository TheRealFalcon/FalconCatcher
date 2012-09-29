package com.falconware.falconcatcher;

import java.util.ArrayList;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddFeedActivity extends Activity implements OnTaskCompleted {
	private ArrayList<Map<String,String> > mEntryList;

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
    	String url = text.getText().toString();
    	new DownloadFeedTask(this, db).addFeeds(new Pair<String,String>(url, url));
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
    	mEntryList = new ArrayList<Map<String,String> >();
    	parseReader(null);  //delete me

//    	final AccountManager manager = AccountManager.get(this);
//    	//Why risk trying an invalid token and failing?  Just invalidate the cache off the bad and get a valid one
//    	manager.invalidateAuthToken("com.google", null);
//    	Account account = manager.getAccountsByType("com.google")[0];    	
//    	manager.getAuthToken(account, "oauth2:https://www.google.com/reader/api", null, this, new AccountManagerCallback<Bundle>() {
//    		public void run(AccountManagerFuture<Bundle> future) {
//    			//TODO: Do better catching...
//    			try {
//    				Bundle result = future.getResult();
//    				//TODO: Figure out if Google will ever send me back a new intent
//    				//and if so, how to implement the onActivityResult method
//    				String token = result.getString(AccountManager.KEY_AUTHTOKEN);
//    				parseReader(token);
//    			} catch (Exception e) {
//    				e.printStackTrace();
//    			}
//    		}
//    	}, null);
    }
    
    private void parseReader(String token) {
    	ReaderParser parser = new ReaderParser(this, mEntryList, token);
    	parser.execute();   
    }
    
    public void onTaskCompleted(boolean result) {
    	if (result) {
    		Intent intent = new Intent(this, GoogleSelectionActivity.class);
    		intent.putExtra("entryList", mEntryList);
    		intent.putExtra("useCategories", true);
    		startActivity(intent);
    	} else {
    		Toast.makeText(this, "Unable to connect to Google Reader", Toast.LENGTH_LONG).show();
    	}
    }
    
}
