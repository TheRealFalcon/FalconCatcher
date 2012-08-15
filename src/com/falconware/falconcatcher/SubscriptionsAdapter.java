package com.falconware.falconcatcher;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorTreeAdapter;

public class SubscriptionsAdapter extends SimpleCursorTreeAdapter {
	
	

	public SubscriptionsAdapter(Context context, Cursor cursor,
			int groupLayout, String[] groupFrom, int[] groupTo,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
				childTo);
	}

	public SubscriptionsAdapter(Context context, Cursor cursor) {
		super(context, cursor, R.layout.group_row, new String[] { "episodeTitle" },
				new int[] { R.id.row_name }, R.layout.child_row, new String[] {"feedTitle"}, 
				new int[] {R.id.grp_child});
	}
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		// TODO Auto-generated method stub
		return null;
	}

}
