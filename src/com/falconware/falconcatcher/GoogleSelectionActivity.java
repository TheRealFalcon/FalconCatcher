package com.falconware.falconcatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class GoogleSelectionActivity extends Activity {
	private ActionMode mActionMode;
	private Database mDb;
	private ListView mView;
	
	public void getUserCredentials() {
//		AccountManager manager = AccountManager.get(this);
//		Account account = manager.getAccountsByType("com.google")[0];
//		System.out.println(account.name);
//		AccountManagerFuture<Bundle> managerFuture = manager.getAuthToken(account, "Google Reader", null, this, null, null); //Who the heck named that class?
//		try {			
//			managerFuture.getResult();
//		} catch (AuthenticatorException e) {
//			//Invalidate token and try to reauthenticate
//			//manager.invalidate("com.google", managerFuture.)
//			e.printStackTrace();
//			finish();
//		}
//		catch (Exception e) { //TODO: Don't catch top level exception
//			e.printStackTrace();
//			finish();
//		}
		//Account account = accounts[0];
		//manager.getAuthToken(...);
		
		//manager.getAccountsByType(type);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getUserCredentials();
		
		mActionMode = null;
		mDb = new Database(this);
		//ListView view = getListView();
		mView = new ListView(this);
		mView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);		
		
		//http://www.google.com/reader/api/0/subscription/list?output=json
    	//List<Map<String,String> > entryList = parseReader();
		final List<Map<String,String> > entryList = (ArrayList<Map<String,String> >)getIntent().getSerializableExtra("entryList");
    	GoogleAdapter adapter = new GoogleAdapter(getIntent().getBooleanExtra("useCategories", false));
    	
    	for (Map<String,String> entry : entryList) {
    		adapter.addEntry(entry);
    	}    	
    	
    	mView.setAdapter(adapter);
    	mView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GoogleAdapter adapter = ((GoogleAdapter)mView.getAdapter());
				if (adapter.isCategory(position)) {
					String category = (String)adapter.getItem(position);
					mView.setItemChecked(position, false);
					Intent intent = new Intent(GoogleSelectionActivity.this, GoogleSelectionActivity.class);
					ArrayList<Map<String,String> > newEntryList = new ArrayList<Map<String,String> >();
					for (Map<String,String> entry : entryList) {
						if (entry.get("category").equals(category)) {
							newEntryList.add(entry);
						}
					}
					intent.putExtra("entryList", newEntryList);
					startActivity(intent);
					return;
				}
				int checkedCount = mView.getCheckedItemCount();
				if (checkedCount > 0) {
					if (mActionMode == null) {
						mActionMode = startActionMode(new ModeCallback());
						
					}
					
					switch (checkedCount) {
					case 1:
						mActionMode.setTitle("1 selected");
						break;
					default:
						mActionMode.setTitle("" + checkedCount + " selected");
						break;
					}
				}
				
				else {
					mActionMode.finish();
					//mActionMode = null;
				}
			}
		});
    	setContentView(mView);
	}
	

	
	private class GoogleAdapter extends BaseAdapter {
		private List<String> mCategories;
		private List<Map<String,String> > mEntries;
		private LayoutInflater mInflater;
		private boolean mUseCategories;
		
		public GoogleAdapter(boolean useCategories) {
			mCategories = new LinkedList<String>();
			mEntries = new ArrayList<Map<String,String> >();
			mInflater = getLayoutInflater();
			mUseCategories = useCategories;
		}
		
		public void addEntry(Map<String,String> entry) {
			//mEntries.add(entry);
			if (mUseCategories) {
				String category = entry.get("category");
				if (!category.isEmpty()) {
					addCategory(entry.get("category"));
				}
				else {
					mEntries.add(entry);
				}
			}
			else {
				mEntries.add(entry);
			}
		}
		
		private void addCategory(String category) {
			if (mCategories.contains(category)) {
				return;
			}			
			mCategories.add(category);
			//TODO: This is probably really inefficient...
			Collections.sort(mCategories);
		}
		
		public boolean isCategory(int position) {
			return position < mCategories.size();
		}
		
		public boolean isEntry(int position) {
			return position >= mCategories.size();
		}

		@Override
		public int getCount() {
			return mCategories.size() + mEntries.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < mCategories.size()) {
				return mCategories.get(position);
			}
			return mEntries.get(position-mCategories.size());
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (position < mCategories.size()) {
				//Its a category
				String currentItem = (String)getItem(position);
				view = mInflater.inflate(R.layout.folder_list_item, null);
				TextView textView = (TextView)view.findViewById(R.id.list_item_text);
				textView.setText(currentItem);
			}
			else {
				//Its an entry
				@SuppressWarnings("unchecked")  //Thats right
				Map<String,String> currentItem = (Map<String,String>)getItem(position);
				view = mInflater.inflate(R.layout.entry_list_item, null);
				TextView textView = (TextView)view.findViewById(R.id.list_item_text);
				textView.setText(currentItem.get("title"));
			}
			return view;
		}		
	}
	
	   private class ModeCallback implements ListView.MultiChoiceModeListener {

	        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	            MenuInflater inflater = getMenuInflater();
	            inflater.inflate(R.menu.google_import_menu, menu);
	            mode.setTitle("Select Items");
	            return true;
	        }

	        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	            return true;
	        }

	        @SuppressWarnings("unchecked")
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//	            switch (item.getItemId()) {
//	            case R.id.share:
//	                Toast.makeText(List16.this, "Shared " + getListView().getCheckedItemCount() +
//	                        " items", Toast.LENGTH_SHORT).show();
//	                mode.finish();
//	                break;
//	            default:
//	                Toast.makeText(List16.this, "Clicked " + item.getTitle(),
//	                        Toast.LENGTH_SHORT).show();
//	                break;
//	            }
	        	ListView view = mView;
	        	List<Pair<String,String> > nameUrlPairs = new LinkedList<Pair<String,String> >();
	        	for (long id : view.getCheckedItemIds()) {
	        		@SuppressWarnings("unchecked")
					Map<String,String> entry = (Map<String,String>)((GoogleAdapter)view.getAdapter()).getItem((int)id);
	        		nameUrlPairs.add(new Pair<String,String>(entry.get("title"), entry.get("id")));	        			        		
	        	}
	        	//new DownloadFeedTask(getParent(), mDb).addFeeds(nameUrlPairs.toArray(new Pair[nameUrlPairs.size()]));
	        	new DownloadFeedTask(GoogleSelectionActivity.this, mDb).addFeeds(nameUrlPairs.toArray(new Pair[nameUrlPairs.size()]));

	        	//GoogleSelectionActivity.this.setContentView(R.layout.import_dialog);
	        	mode.finish();
	            return true;
	        }
	        
	        

	        public void onDestroyActionMode(ActionMode mode) {
	        	ListView view = mView;
	        	view.clearChoices();
	        	((GoogleAdapter)view.getAdapter()).notifyDataSetChanged();
	        	mActionMode = null;
	        }

	        public void onItemCheckedStateChanged(ActionMode mode,
	                int position, long id, boolean checked) {
	        	//Not getting called
	        	return;
	        }
	        
	    }
}
