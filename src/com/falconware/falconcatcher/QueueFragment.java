package com.falconware.falconcatcher;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class QueueFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//ListView view = new ListView(getActivity());
		ListView view = (ListView)inflater.inflate(R.layout.list_content_simple, container, false);
		Database db = new Database(getActivity());
		Cursor c = db.getQueue();
		QueueAdapter adapter = new QueueAdapter(getActivity(), c);
		view.setAdapter(adapter);
		
		return view;		
	}
	
	private class QueueAdapter extends SimpleCursorAdapter {
		public QueueAdapter(Activity activity, Cursor cursor) {	
			super(activity, R.layout.queue_fragment, cursor, 
					new String[] {Database.TableFeed.IMAGE, Database.TableFile.EPISODE_TITLE}, 
					new int[] {R.id.image, R.id.text});
			
			setViewBinder(new ViewBinder() {			
				@Override
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
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
		

		
	}
}
