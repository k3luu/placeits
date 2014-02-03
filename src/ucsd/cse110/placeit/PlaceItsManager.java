package ucsd.cse110.placeit;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class PlaceItsManager extends FragmentActivity implements
ActionBar.TabListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the intent sent from the map/add button
		Intent intent = getIntent();
	    Double lat = intent.getDoubleExtra(MainActivity.LAT, 0.0);
	    Double lng = intent.getDoubleExtra(MainActivity.LNG, 0.0);
	    
	    // for proof of concept
//	    EditText editView = (EditText) findViewById(R.id.location);
//	    editView.setText(lat.toString() + lng.toString());

		setContentView(R.layout.place_its_manager_activity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_it_manager_menu, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// Menu buttons click to associated activity
    	switch (item.getItemId()) {
    	case R.id.map_view_btn:
    		Log.i("MainActive", "map button click");
    		Intent intent1 = new Intent(this, MainActivity.class);
        	startActivity(intent1);
    	return true;
    	
    	case R.id.list_view_btn:
    		Log.i("MainActive", "list button click");
    		Intent intent2 = new Intent(this, ListActivity.class);
        	startActivity(intent2);
    	return true;

    	default:
    		return super.onOptionsItemSelected(item);		
    	}
    }

    @Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
}