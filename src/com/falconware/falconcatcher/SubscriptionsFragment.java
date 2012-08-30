
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
	private SubscriptionsAdapter adapter;
	private Database mDb;
	private Activity mActivity;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		mDb = new Database(mActivity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ExpandableListView view = (ExpandableListView)inflater.inflate(R.layout.subscriptions, container, false);
		registerForContextMenu(view);
		
		//Activity currentActivity = mActivity;
		adapter = new SubscriptionsAdapter(mActivity.getApplicationContext(), 
				mDb.getSubscriptions(), mDb);
		view.setAdapter(adapter);

		//new DownloadFeedTask(currentActivity, mDb, view).execute("http://10.0.2.2:8080/freakonomics.xml");
		return view;
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
			Cursor cursor = adapter.getChild(mSelectedGroupRow, mSelectedChildRow);
			String feedTitle = cursor.getString(cursor.getColumnIndex("feedTitle"));
			String episodeTitle = cursor.getString(cursor.getColumnIndex("title"));
			String url = cursor.getString(cursor.getColumnIndex("url"));
			long downloadId = Storage.downloadEpisode(mActivity, feedTitle, episodeTitle, url);
			
		}
		else if (itemTitle.equals(getString(R.string.menu_unsubscribe))) {
			Cursor cursor = adapter.getGroup(mSelectedGroupRow);
			mDb.removeFeed(cursor.getString(cursor.getColumnIndex("title")));
			adapter.setGroupCursor(mDb.getSubscriptions());
			//adapter.notifyDataSetChanged();
		}
		else if (itemTitle.equals(getString(R.string.menu_play))) {
			System.out.println(mDb.getApplicationDirectory());
			Cursor cursor = adapter.getChild(mSelectedGroupRow, mSelectedChildRow);
			String feedTitle = cursor.getString(cursor.getColumnIndex("feedTitle"));
			String episodeTitle = cursor.getString(cursor.getColumnIndex("title"));
			System.out.println("Playing file: " + mDb.getApplicationDirectory() + feedTitle + "/" + episodeTitle + ".mp3");
			String filename = mDb.getApplicationDirectory() + feedTitle + "/" + episodeTitle + ".mp3";
			
			Button button = (Button)mActivity.findViewById(R.id.play_or_pause_button);
			button.setBackgroundResource(android.R.drawable.ic_media_pause);
			button.setTag(getString(R.string.button_pause));
			Intent playerIntent = new Intent(mActivity, PlayerService.class);
			playerIntent.setAction(PlayerService.ACTION_PLAY);
			//Bundle extras = playerIntent.getExtras();
			//extras.putString("filename", filename);
			playerIntent.putExtra("filename", filename);
			mActivity.startService(playerIntent);	
		}
		return super.onContextItemSelected(item);
	}
	
}


//		SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
//				getActivity(), 
//				createGroupList(), 
//				R.layout.group_row, 
//				new String[] { "Group Item", "hello" }, 
//				new int[] { R.id.row_name }, 
//				createChildList(), 
//				R.layout.child_row, 
//				new String[] {"Sub Item"}, 
//				new int[] {R.id.grp_child});
				

//	}

//	private List<HashMap<String,String> > createGroupList() {
//		List<HashMap<String,String> > result = new ArrayList<HashMap<String,String> >();
//		for( int i = 0 ; i < 15 ; ++i ) { // 15 groups........
//			HashMap<String, String> m = new HashMap<String, String>();
//			m.put( "Group Item","Group Item " + i ); // the key and it's value.
//			result.add( m );
//		}
//		return result;
//	}
//
//	private List<List<HashMap<String,String> > > createChildList() {
//
//		List<List<HashMap<String,String> > > result = new ArrayList<List<HashMap<String,String> > >();
//		for( int i = 0 ; i < 15 ; ++i ) { // this -15 is the number of groups(Here it's fifteen)
//			/* each group need each HashMap-Here for each group we have 3 subgroups */
//			List<HashMap<String,String> > secList = new ArrayList<HashMap<String,String> >();
//			for( int n = 0 ; n < 3 ; n++ ) {
//				HashMap<String,String> child = new HashMap<String,String>();
//				child.put( "Sub Item", "Sub Item " + n );
//				secList.add( child );
//			}
//			result.add( secList );
//		}
//		return result;
//	}
//}
