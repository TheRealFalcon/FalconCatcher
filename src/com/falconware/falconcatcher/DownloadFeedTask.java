package com.falconware.falconcatcher;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class DownloadFeedTask extends AsyncTask<String, Void, Boolean> {
	private Context mContext;
	private Database mDb;
	private ExpandableListView mView;
	
	public DownloadFeedTask(Context context, Database db, ExpandableListView view) {
		mContext = context;
		mDb = db;
		mView = view;
	}

	@Override
	protected Boolean doInBackground(String... url) {
		try {
			FeedParser parser = new FeedParser(url[0]);
			parser.parseFeedProperties(mDb);
			parser.parseEpisodeList(mDb);
		} catch (Exception e) { //TODO: Don't catch top level Exception
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		
		if (result.booleanValue() == true) {
			SubscriptionsAdapter adapter = new SubscriptionsAdapter(mContext, mDb.getSubscriptions(), mDb);
			mView.setAdapter(adapter);
		}
		else {
			Toast.makeText(mContext, "Can't parse feed", Toast.LENGTH_SHORT).show();
		}
	}
}