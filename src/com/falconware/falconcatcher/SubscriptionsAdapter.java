package com.falconware.falconcatcher;

import java.io.ByteArrayInputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorTreeAdapter;

public class SubscriptionsAdapter extends SimpleCursorTreeAdapter {
	
	private Database mDb;

	public SubscriptionsAdapter(Context context, Cursor cursor, Database db) {
		super(context, cursor, R.layout.group_row, new String[] { "image", "title" },
				new int[] { R.id.group_image, R.id.group_name }, R.layout.child_row, new String[] {"title"}, 
				new int[] {R.id.grp_child});
		mDb = db;
		setViewBinder(new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (view instanceof ImageView) {
					byte[] imageByteArray=cursor.getBlob(columnIndex);
					//the cursor is not needed anymore
					//cursor.close();

					//convert it back to an image
					ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
					Bitmap theImage = BitmapFactory.decodeStream(imageStream);
					((ImageView)view).setImageBitmap(theImage);
					return true;
				}
				return false;
			}
		});
	}
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		return mDb.getEpisodes(groupCursor.getString(groupCursor.getColumnIndex("title")));
	}

}
