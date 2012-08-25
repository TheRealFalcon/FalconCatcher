package com.falconware.falconcatcher;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorTreeAdapter;

public class SubscriptionsAdapter extends SimpleCursorTreeAdapter {
	
	private Database mDb;

//	public SubscriptionsAdapter(Context context, Cursor cursor,
//			int groupLayout, String[] groupFrom, int[] groupTo,
//			int childLayout, String[] childFrom, int[] childTo, Database db) {
//		super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
//				childTo);
//		mDb = db;
//	}

	public SubscriptionsAdapter(Context context, Cursor cursor, Database db) {
		super(context, cursor, R.layout.group_row, new String[] { "title" },
				new int[] { R.id.row_name }, R.layout.child_row, new String[] {"title"}, 
				new int[] {R.id.grp_child});
		mDb = db;
	}
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		return mDb.getEpisodes(groupCursor.getString(groupCursor.getColumnIndex("title")));
	}

}
