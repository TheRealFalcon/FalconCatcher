package com.falconware.falconcatcher;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class DownloadFeedTask extends AsyncTask<String, Void, Boolean> {
	private Activity mActivity;
	//private Database mDb;
	//private ExpandableListView mView;
	
	public DownloadFeedTask(Activity context) {
		//System.out.println("DownloadFeedTask ctor");
		mActivity = context;
		//mDb = db;
		//mView = view;
	}

	@Override
	protected Boolean doInBackground(String... url) {
		try {
			new FeedParser(url[0], mActivity);
			//parser.parseFeedProperties(mDb);
			//parser.parseEpisodeList(mDb);
		} catch (Exception e) { //TODO: Don't catch top level Exception
			e.printStackTrace();
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					Toast.makeText(mActivity, "Failed to parse feed!", Toast.LENGTH_SHORT).show();					
				}				
			});			
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		
		if (result.booleanValue() == true) {
			ExpandableListView view = (ExpandableListView)mActivity.findViewById(R.id.subscriptionList);
			if (view == null) {
				return;
			}
			SubscriptionsAdapter adapter = (SubscriptionsAdapter)view.getExpandableListAdapter();
			adapter.setGroupCursor(new Database(mActivity).getSubscriptions());
			adapter.notifyDataSetChanged();
			//System.out.println("container: " + mActivity.findViewById(R.id.container));
			
			//ExpandableListView view = (ExpandableListView)((MainActivity)mActivity).findViewById(R.layout.subscriptions);
			
			//view.getExpandableListAdapter();
			//x.notifyDataSetChanged();
			//.notifyDataSetChanged();//setGroupCursor(new Database(mActivity).getSubscriptions());
//			Database db = new Database(mContext);
//			SubscriptionsAdapter adapter = new SubscriptionsAdapter(mContext, db.getSubscriptions(), mDb);
//			mView.setAdapter(adapter);
		}
		else {
			Toast.makeText(mActivity, "Can't parse feed", Toast.LENGTH_SHORT).show();
		}
	}
}