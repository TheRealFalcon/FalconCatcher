package com.falconware.falconcatcher;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    	//mDb.close();
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

    		Intent intent = new Intent(this, AddFeedActivity.class);
    		startActivity(intent);
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
    		fragment = new QueueFragment();
    	}
    	else {
    		fragment = new PlayerFragment();    		   		
    	}
    	getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, fragment)
		.commit();
    }
    
    public void startPlayer() {
    	getActionBar().selectTab(getActionBar().getTabAt(2));
//    	getSupportFragmentManager().beginTransaction()
//    	.replace(R.id.container, new PlayerFragment())
//    	.commit();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


}
