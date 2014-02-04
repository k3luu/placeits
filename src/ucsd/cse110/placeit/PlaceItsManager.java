package ucsd.cse110.placeit;

import java.text.DecimalFormat;

import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
//import android.app.ActionBar.Tab;
//import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
//import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
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
		
		// Three cases
		// Case 1: called by createButton [no info]
		// Case 2: called by pressOnMap [LatLng given]
		// Case 3: called by ListView [All info]
		// cases ended
		
		Intent checkWhere = getIntent();
		int validate = checkWhere.getIntExtra("ucsd.cs110.placeit.CheckSrouce", 1);
		
		double lat, lng;
		
		// Add case 3 later
		if (validate == 1)
		{
			Intent intent = getIntent();
			lat = intent.getDoubleExtra(MainActivity.LAT, 0.0);
			lng = intent.getDoubleExtra(MainActivity.LNG, 0.0);
		}
		else  //change to else if later on
		{
			//Method A
			Bundle b = getIntent().getParcelableExtra("locationOnlyBundle");
			LatLng location = b.getParcelable("ucsd.cs110.placeit.LocationOnly");
			Log.i("abc", "abc");
			if (location == null)
			{
				lat = 0.0;
				lng = 0.0;
			}
			else
			{
				lat = location.latitude;
				lng = location.longitude;
			}
		}
		
		
		//
		
		/*
		// Get the intent sent from the map/add button
		Intent intent = getIntent();
		double lat = intent.getDoubleExtra(MainActivity.LAT, 0.0);
		double lng = intent.getDoubleExtra(MainActivity.LNG, 0.0);
		*/
		
		/*String reverseGeo = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat.toString() +","+
	    		lng.toString() + "&sensor=false";*/
	    
	    DecimalFormat df = new DecimalFormat("#.#######");
	    String latFormat = df.format(lat);
	    String lngFormat = df.format(lng);
	    // for proof of concept
	    EditText editView = (EditText) findViewById(R.id.location);
        editView.setText(latFormat.toString() + "; " + lngFormat.toString());
        
        // debugging
        Log.i(latFormat, "lat val");
        Log.i(lngFormat, "lng val");
        String str = editView.getText().toString();
        Log.i(str, "what's being passed");
	    //editView.setText(reverseGeo);

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
    	if ( item.getItemId() == R.id.database_add_edit ) {
    		Intent intent1 = new Intent(this, MainActivity.class);
    		startActivity(intent1);
    		return true;
    	}
    	else if ( item.getItemId() == R.id.datebase_cancel ) {
    		Intent intent2 = new Intent(this, ListActivity.class);
        	startActivity(intent2);
        	return true;
    	}
    	else {
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