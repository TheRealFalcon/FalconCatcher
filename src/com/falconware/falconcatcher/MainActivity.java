package com.falconware.falconcatcher;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private Database mDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // For each of the sections in the app, add a tab to the action bar.
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section1).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section2).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(R.string.title_section3).setTabListener(this));       
    	mDb = new Database(this);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();

    }
    
    @Override
    protected void onStop() {
    	super.onStop();

    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mDb.close();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
    	if (item.getTitle().equals(getString(R.string.menu_add_subscription))) {
    		final View layout = View.inflate(this, R.layout.add_subscription, null);
    		final Activity activity = this;
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    		alert.setView(layout)
    		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					new DownloadFeedTask(activity, mDb).execute("http://10.0.2.2:8080/freakonomics.xml");				
				}
    			
    		})
    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
    			
    		})
    		.show();
    		
    	}
    	return true;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, show the tab contents in the container
    	Fragment fragment = null;
    	if (tab.getPosition() == 0) {
    		fragment = new SubscriptionsFragment();
    	}
    	else if (tab.getPosition() == 1) {
    		fragment = new DummySectionFragment();
    		Bundle args = new Bundle();
    		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, tab.getPosition() + 1);
    		fragment.setArguments(args);  
    	}
    	else {
    		fragment = new PlayerFragment();    		   		
    	}
    	getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, fragment)
		.commit();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER);
            Bundle args = getArguments();
            textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
            return textView;
        }
    }
    
    public void playOrPauseTrack(View view) {
    	Button button = (Button)findViewById(R.id.play_or_pause_button);
		Intent playerIntent = new Intent(this, PlayerService.class);		
		
    	if (((String)button.getTag()).equals(getString(R.string.button_play))) {
    		playerIntent.setAction(PlayerService.ACTION_PLAY);
    		button.setBackgroundResource(android.R.drawable.ic_media_pause);
    		button.setTag(getString(R.string.button_pause));    		
    	}
    	else {
    		playerIntent.setAction(PlayerService.ACTION_PAUSE);
    		button.setBackgroundResource(android.R.drawable.ic_media_play);
    		button.setTag(getString(R.string.button_play));
    	}
    	startService(playerIntent);
    }
    
    public void nextTrack(View view) {
    	System.out.println("Next track");
    }
}
