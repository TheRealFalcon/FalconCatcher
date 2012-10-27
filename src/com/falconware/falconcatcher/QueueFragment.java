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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.falconware.falconcatcher.Database.TableFile;
import com.falconware.falconcatcher.TouchInterceptor.DragListener;
import com.falconware.falconcatcher.TouchInterceptor.DropListener;
import com.falconware.falconcatcher.TouchInterceptor.RemoveListener;

public class QueueFragment extends Fragment {
	//private DragNDropListView view;
	private TouchInterceptor view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//ListView view = new ListView(getActivity());
		//ListView view = (ListView)inflater.inflate(R.layout.list_content_simple, container, false);
		//TouchInterceptor view = new TouchInterceptor(getActivity(), null);
		view = new TouchInterceptor(getActivity(), null);
		Database db = new Database(getActivity());
		Cursor c = db.getQueue();
		QueueAdapter adapter = new QueueAdapter(getActivity(), c);
		view.setAdapter(adapter);
		view.setOnItemClickListener(clickListener);
		
		view.setDropListener(mDropListener);
		view.setRemoveListener(mRemoveListener);
		view.setDragListener(mDragListener);
		
		return view;		
	}
	
	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Cursor queueCursor = (Cursor) ((QueueAdapter)parent.getAdapter()).getItem(position);
			long fileId = queueCursor.getLong(queueCursor.getColumnIndex(TableFile.ID));
//			String feedTitle = queueCursor.getString(queueCursor.getColumnIndex(Database.TableEpisode.FEED_TITLE));
//			String episodeTitle = queueCursor.getString(queueCursor.getColumnIndex(Database.TableFile.EPISODE_TITLE));
//			String filename = queueCursor.getString(queueCursor.getColumnIndex(Database.TableFile.PATH));
			((MainActivity)QueueFragment.this.getActivity()).playFile(fileId); //(feedTitle, episodeTitle, filename);
		}

		
	};
	
	private class QueueAdapter extends SimpleCursorAdapter {
		private Activity mActivity;
		
		public QueueAdapter(Activity activity, Cursor cursor) {	
			super(activity, R.layout.queue_fragment, cursor, 
					new String[] {Database.TableFile.EPISODE_TITLE, Database.TableEpisode.FEED_TITLE}, 
					new int[] {R.id.text1, R.id.text2});
			mActivity = activity;
			
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
		
		public void onRemove(int which) {
			System.out.println("In adapter onRemove");
			if (which < 0 || which > getCount()) {
				return;
			}
			Cursor c = getCursor();
			c.moveToPosition(which);
			String episodeTitle = c.getString(c.getColumnIndex(Database.TableFile.EPISODE_TITLE));
			c.close();
			Database db = new Database(mActivity);
			db.removeFile(episodeTitle);
			changeCursor(db.getQueue());			
		}
		
		public void onDrop(int from, int to) {
			System.out.println("From: " + from);
			System.out.println("To: " + to);
			Cursor queueCursor = getCursor();
			queueCursor.moveToPosition(from);
			String episodeTitle = queueCursor.getString(queueCursor.getColumnIndex(Database.TableFile.EPISODE_TITLE));
			queueCursor.close();
			Database db = new Database(mActivity);				
			Cursor fileCursor = db.getFile(episodeTitle);
			System.out.println("Removing: " + episodeTitle);
			db.removeFile(episodeTitle);
			fileCursor.moveToFirst();
			String path = fileCursor.getString(fileCursor.getColumnIndex(Database.TableFile.PATH));
			fileCursor.close();
			db.addFile(episodeTitle, path, to);
			changeCursor(db.getQueue());
		}
	}
	
	private DropListener mDropListener = new DropListener() {
		public void drop(int from, int to) {
			System.out.println("In onDrop");
			QueueAdapter adapter = (QueueAdapter)view.getAdapter();
			adapter.onDrop(from, to);
			view.invalidateViews();	        	
		}
	};

	private RemoveListener mRemoveListener = new RemoveListener() {
		public void remove(int which) {
			System.out.println("In onRemove");
			QueueAdapter adapter = (QueueAdapter)view.getAdapter();
			adapter.onRemove(which);
			view.invalidateViews();	        	
		}
	};

	private DragListener mDragListener = new DragListener() {

		int backgroundColor = 0xe0103010;
		int defaultBackgroundColor;

		public void drag(int x, int y) {
			// TODO Auto-generated method stub
		}

		public void startDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
			itemView.setBackgroundColor(backgroundColor);
			ImageView iv = (ImageView)itemView.findViewById(R.id.image);
			if (iv != null) iv.setVisibility(View.INVISIBLE);
		}

		public void stopDrag() {
//			itemView.setVisibility(View.VISIBLE);
//			itemView.setBackgroundColor(defaultBackgroundColor);
//			ImageView iv = (ImageView)itemView.findViewById(R.id.image);
//			if (iv != null) iv.setVisibility(View.VISIBLE);
		}

	};
}
