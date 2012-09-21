package com.falconware.falconcatcher;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class DownloadFeedTask extends AsyncTask<Pair<String,String>, String, Boolean> {
	private Activity mActivity;
	private Database mDb;
	private Dialog mDialog;
	private TextView mTextView;
	
	public DownloadFeedTask(Activity context, Database db) {
		mActivity = context;
		mDialog = new Dialog(context);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(R.layout.import_dialog);
		mTextView = (TextView)mDialog.findViewById(R.id.text);
		mDb = db;
	}
	
	public void addFeeds(Pair<String,String>... nameUrlPairs) {
		this.execute(nameUrlPairs);
	}

	@Override
	protected Boolean doInBackground(Pair<String,String>... nameUrlPairs) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mDialog.show();				
			}				
		});
		
		for (Pair<String,String> nameUrlPair : nameUrlPairs) {
			String name = nameUrlPair.first;
			String url = nameUrlPair.second;
			publishProgress("Importing " + name);

			try {
				new LocalParser(url, mActivity, mDb);
				//publishProgress("Successfully parsed " + name);
			} catch (Exception e) { //TODO: Don't catch top level Exception
				e.printStackTrace();
				publishProgress("Failed to import " + name + "!");
	    		try {
					Thread.sleep(500);
				} catch (InterruptedException ee) {
					// TODO Auto-generated catch block
					ee.printStackTrace();
				}
			}
		}
		return true;
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
    	if (mDialog != null) {
    		mTextView.setText(values[0]);
    	}   	
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mActivity == null) {
			return;
		}
		
		if (mDialog != null) {
			mDialog.dismiss();
			return;
		}
		if (result.booleanValue() == true) {
			ExpandableListView view = (ExpandableListView)mActivity.findViewById(R.id.subscriptionList);
			if (view == null) {
				return;
			}
			SubscriptionsAdapter adapter = (SubscriptionsAdapter)view.getExpandableListAdapter();
			adapter.setGroupCursor(mDb.getSubscriptions());
			adapter.notifyDataSetChanged();
		}
//		else {
//			Toast.makeText(mActivity, "Failed parsing feeds: \nfeed!", Toast.LENGTH_LONG).show();
//		}
	}
}