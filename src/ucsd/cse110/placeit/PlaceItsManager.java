package ucsd.cse110.placeit;

import java.text.DecimalFormat;

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
import android.widget.EditText;

public class PlaceItsManager extends FragmentActivity implements
ActionBar.TabListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load layout
		setContentView(R.layout.place_its_manager_activity);
		
		Bundle b = getIntent().getExtras();
		MyParcelable placeItWithLocationOnly = b.getParcelable("placeItWithLocationOnly");
		// Get the intent sent from the map/add button
		//Intent intent = getIntent();
	    double lat = placeItWithLocationOnly.getLocation().latitude;
	    //////double lng = placeItWithLocationOnly.getLocation().longitude;
	    /*String reverseGeo = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat.toString() +","+
	    		lng.toString() + "&sensor=false";*/
	    
	    DecimalFormat df = new DecimalFormat("#.#######");
	    //String latFormat = df.format(lat);
	    //String lngFormat = df.format(lng);
	    // for proof of concept
	    EditText editView = (EditText) findViewById(R.id.location);
        ////////////editView.setText(latFormat + "; " + lngFormat);
	    //editView.setText(reverseGeo);

		//setContentView(R.layout.place_its_manager_activity);
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