
package com.falconware.falconcatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;


public class SubscriptionsFragment extends Fragment {
	private Database mDb;
	
	public SubscriptionsFragment(Database db) {
		mDb = db;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ExpandableListView view = (ExpandableListView)inflater.inflate(R.layout.subscriptions, container, false);
		//FeedParser parser = null;

		System.out.println("hey");
		new AsyncTask<String, Void, Void>() {

			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
				try {
					FeedParser parser = new FeedParser(params[0]);
					System.out.println("abc");
					parser.parseFeedProperties(mDb);
					System.out.println("def");
					parser.parseEpisodeList(mDb);
					return null;
				} catch (FeedException e) {
					//Toast.makeText(getActivity(), "Can't parse feed", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					//return view; 
				} catch (IOException e) {
					//Toast.makeText(getActivity(), "Can't parse feed", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					//return view;
				} catch (Exception e) {
					//Toast.makeText(getActivity(), "Can't parse feed", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
					//return view;
				}
				return null;
			}
		}.execute("http://10.0.2.2/podcast1.xml");
			//parser = new FeedParser("http://10.0.2.2/podcast1.xml");
			//System.out.println("abc");
			//parser.parseFeedProperties(mDb);
			//System.out.println("def");
			//parser.parseEpisodeList(mDb);

		SubscriptionsAdapter adapter = new SubscriptionsAdapter(getActivity(), mDb.getSubscriptions(), mDb);

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
		view.setAdapter(adapter);		
		return view;
	}

	private List<HashMap<String,String> > createGroupList() {
		List<HashMap<String,String> > result = new ArrayList<HashMap<String,String> >();
		for( int i = 0 ; i < 15 ; ++i ) { // 15 groups........
			HashMap<String, String> m = new HashMap<String, String>();
			m.put( "Group Item","Group Item " + i ); // the key and it's value.
			result.add( m );
		}
		return result;
	}

	private List<List<HashMap<String,String> > > createChildList() {

		List<List<HashMap<String,String> > > result = new ArrayList<List<HashMap<String,String> > >();
		for( int i = 0 ; i < 15 ; ++i ) { // this -15 is the number of groups(Here it's fifteen)
			/* each group need each HashMap-Here for each group we have 3 subgroups */
			List<HashMap<String,String> > secList = new ArrayList<HashMap<String,String> >();
			for( int n = 0 ; n < 3 ; n++ ) {
				HashMap<String,String> child = new HashMap<String,String>();
				child.put( "Sub Item", "Sub Item " + n );
				secList.add( child );
			}
			result.add( secList );
		}
		return result;
	}
}
