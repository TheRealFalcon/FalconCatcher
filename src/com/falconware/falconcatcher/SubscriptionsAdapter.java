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
				//TODO: Bug that if there is no image associated with this podcast, it'll steal somebody else's icon
				if (view instanceof ImageView) {
					byte[] imageByteArray=cursor.getBlob(columnIndex);
					if (imageByteArray != null) {
						ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
						Bitmap theImage = BitmapFactory.decodeStream(imageStream);
						((ImageView)view).setImageBitmap(theImage);
					}
					else {
						((ImageView)view).setImageResource(R.drawable.feed_icon_28x28);
					}
					return true;
				}
				return false;
			}
		});
	}
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		return mDb.getEpisodes(groupCursor.getString(groupCursor.getColumnIndex(Database.TableFeed.TITLE)));
	}

}
