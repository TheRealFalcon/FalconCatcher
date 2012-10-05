
package com.falconware.falconcatcher;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;


public class SubscriptionsFragment extends Fragment {
	private int mSelectedGroupRow;
	private int mSelectedChildRow;
	private SubscriptionsAdapter mAdapter;
	private Database mDb;
	private MainActivity mActivity;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity)getActivity();
		mDb = new Database(mActivity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ExpandableListView view = (ExpandableListView)inflater.inflate(android.R.layout.expandable_list_content, container, false);
		registerForContextMenu(view);
		
		//Activity currentActivity = mActivity;
		mAdapter = new SubscriptionsAdapter(mActivity.getApplicationContext(), 
				mDb.getFeeds(), mDb);
		view.setAdapter(mAdapter);

		//new DownloadFeedTask(currentActivity, mDb, view).execute("http://10.0.2.2:8080/freakonomics.xml");
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//mAdapter.notifyDataSetChanged(true);
		mAdapter.setGroupCursor(mDb.getFeeds());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		ExpandableListView.ExpandableListContextMenuInfo info =
	            (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
		mSelectedGroupRow = ExpandableListView.getPackedPositionGroup(info.packedPosition);
	    if (ExpandableListView.getPackedPositionType(info.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {	    	
	    	mSelectedChildRow = ExpandableListView.getPackedPositionChild(info.packedPosition);
	    	mActivity.getMenuInflater().inflate(R.menu.child_row, menu);
	    }
	    else {
	    	mSelectedChildRow = -1;
	    	mActivity.getMenuInflater().inflate(R.menu.group_row, menu);
	    }
	    System.out.println("Selected group: " + mSelectedGroupRow);
	    System.out.println("Selected child: " + mSelectedChildRow);
	}
	
	//TODO: Put the logic to get the file path in a common place
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals(getString(R.string.menu_download))) {
			Cursor episodeCursor = mAdapter.getChild(mSelectedGroupRow, mSelectedChildRow);
			Intent intent = new Intent(mActivity, DownloadService.class);
			intent.setAction(DownloadService.ACTION_DOWNLOAD);
			intent.putExtra("episodeId", episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.TITLE)));
			//UNCOMMENT THIS TO DOWNLOAD!!!!!!!
			//mActivity.startService(intent);			
		}
		else if (itemTitle.equals(getString(R.string.menu_unsubscribe))) {
			Cursor cursor = mAdapter.getGroup(mSelectedGroupRow);
			mDb.removeFeed(cursor.getString(cursor.getColumnIndex(Database.TableFeed.TITLE)));
			mAdapter.setGroupCursor(mDb.getFeeds());
			mAdapter.notifyDataSetChanged();
		}
		else if (itemTitle.equals(getString(R.string.menu_play))) {
			Cursor episodeCursor = mAdapter.getChild(mSelectedGroupRow, mSelectedChildRow);
			//String feedId = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.FEED_ID));
			String episodeTitle = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.TITLE));
			
			//Cursor feedCursor = mDb.getFeed(feedId);
			String feedTitle = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.FEED_TITLE));
			
			System.out.println("Playing file: " + mDb.getApplicationDirectory() + feedTitle + "/" + episodeTitle + ".mp3");
			//String filename = "/storage/sdcard0/FalconCatcher/All About Android/aaa0078.mp3"; 
			String filename = mDb.getApplicationDirectory() + feedTitle + "/" + episodeTitle + ".mp3";
			
//			Button button = (Button)mActivity.findViewById(R.id.play_or_pause_button);
//			button.setBackgroundResource(android.R.drawable.ic_media_pause);
//			button.setTag(getString(R.string.button_pause));
			
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			playerIntent.setAction(PlayerService.ACTION_PLAY);
			playerIntent.putExtra("filename", filename);
			mActivity.startService(playerIntent);
			
			mActivity.startPlayer();
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//mDb.close();
	}
	
}
