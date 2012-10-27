
package com.falconware.falconcatcher;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.falconware.falconcatcher.Database.TableEpisode;
import com.falconware.falconcatcher.Database.TableFile;


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
		view.setOnChildClickListener(new OnChildClickListener() {			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				Cursor episodeCursor = mAdapter.getChild(groupPosition, childPosition);
				String description = episodeCursor.getString(episodeCursor.getColumnIndex(TableEpisode.DESCRIPTION));
				Dialog dialog = new Dialog(mActivity);
				TextView html = new TextView(mActivity);
				html.setText(Html.fromHtml(description));
				dialog.setContentView(html);
				dialog.show();
				return true;
			}
		});
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
	
	private void downloadFile(String episodeTitle) {
		if (mDb.getFile(episodeTitle).getCount() > 0) {
			mDb.removeFile(episodeTitle);
		}
		Intent intent = new Intent(mActivity, DownloadService.class);
		intent.setAction(DownloadService.ACTION_DOWNLOAD);
		intent.putExtra("episodeTitle", episodeTitle);
		mActivity.startService(intent);	
	}
	
	//TODO: Put the logic to get the file path in a common place
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals(getString(R.string.menu_download))) {
			Cursor episodeCursor = mAdapter.getChild(mSelectedGroupRow, mSelectedChildRow);
			downloadFile(episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.TITLE)));
		}
		else if (itemTitle.equals(getString(R.string.menu_unsubscribe))) {
			Cursor cursor = mAdapter.getGroup(mSelectedGroupRow);
			mDb.removeFeed(cursor.getString(cursor.getColumnIndex(Database.TableFeed.TITLE)));
			mAdapter.setGroupCursor(mDb.getFeeds());
			mAdapter.notifyDataSetChanged();
		}
		else if (itemTitle.equals(getString(R.string.menu_play))) {
			Cursor episodeCursor = mAdapter.getChild(mSelectedGroupRow, mSelectedChildRow);
			//String feedTitle = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.FEED_TITLE));
			String episodeTitle = episodeCursor.getString(episodeCursor.getColumnIndex(Database.TableEpisode.TITLE));
			Cursor fileCursor = mDb.getFile(episodeTitle);
			//String filename = fileCursor.getString(fileCursor.getColumnIndex(Database.TableFile.PATH));
			fileCursor.close();
			
			//System.out.println("Playing file: " + filename);
			
			long fileId = fileCursor.getLong(fileCursor.getColumnIndex(TableFile.ID));
			//mActivity.playFile(feedTitle, episodeTitle, filename);
			mActivity.playFile(fileId);
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//mDb.close();
	}
	
}
