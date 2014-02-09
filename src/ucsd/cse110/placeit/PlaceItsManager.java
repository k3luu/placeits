package ucsd.cse110.placeit;

import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
//import android.app.ActionBar.Tab;
//import android.app.Activity;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;

public class PlaceItsManager extends FragmentActivity implements
ActionBar.TabListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private EditText editView;
	private LatLng location;
	
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
		
		//Intent checkWhere = getIntent();
		//int validate = checkWhere.getIntExtra("ucsd.cs110.placeit.CheckSrouce", 1);
		
		// Add case 3 later
		/*if (validate == 1)
		{
			Intent intent = getIntent();
			lat = intent.getDoubleExtra(MainActivity.LAT, 0.0);
			lng = intent.getDoubleExtra(MainActivity.LNG, 0.0);
		}
		else  //change to else if later on
		{
			//Method A
			Bundle b = getIntent().getParcelableExtra("locationOnlyBundle");
			location = b.getParcelable("ucsd.cs110.placeit.LocationOnly");
		}*/
		
		Bundle b = getIntent().getParcelableExtra("locationOnlyBundle");
		location = b.getParcelable("ucsd.cs110.placeit.LocationOnly");
		
		//
		
		/*
		// Get the intent sent from the map/add button
		Intent intent = getIntent();
		double lat = intent.getDoubleExtra(MainActivity.LAT, 0.0);
		double lng = intent.getDoubleExtra(MainActivity.LNG, 0.0);
		*/
		
		/*String reverseGeo = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat.toString() +","+
	    		lng.toString() + "&sensor=false";*/
	    
		/*
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
	     * 
	     */
        
        

		setContentView(R.layout.place_its_manager_activity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_it_manager_menu, menu);
		return true;
	}

	protected void onResume()
	{
		super.onResume();
		
		// Geocoder
		editView = (EditText) findViewById(R.id.location);
		try {
			editView.setText((new GetAddressTask(this)).execute(location).get());
		} catch (InterruptedException e) {
			editView.setText("");
			e.printStackTrace();
		} catch (ExecutionException e) {
			editView.setText("");
			e.printStackTrace();
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// Menu buttons click to associated activity
    	if ( item.getItemId() == R.id.database_add_edit ) {
    		
    		// get an instance of our database to add
    		PlaceItDbHelper db = new PlaceItDbHelper(this);
    		
    		EditText title_field = (EditText) findViewById(R.id.editText1);
    		EditText description_field = (EditText) findViewById(R.id.editText2);
    		EditText location_field = (EditText) findViewById(R.id.location);
    		Spinner day_field = (Spinner) findViewById(R.id.day_spinner);
    		Spinner week_field = (Spinner) findViewById(R.id.week_spinner);
    		
    		String title = title_field.getText().toString();
    		String description = description_field.getText().toString();
    		String location_str = location_field.getText().toString();
    		String scheduleData = String.valueOf(day_field.getSelectedItem());
    		String scheduleWeek = String.valueOf(week_field.getSelectedItem());
    		
    		PlaceIt data = new PlaceIt(title, "Active", description, location, 
    				location_str, scheduleData, scheduleWeek);
    		
    		PlaceItDataChecker checker = new PlaceItDataChecker(data);
    		// When clicked, first validate
    		
    		if (checker.checkNormal()) {
    			Log.i("True", "Checker passed!");
    			db.addPlaceIt(data);
    			Intent intent1 = new Intent(this, MainActivity.class);
        		startActivity(intent1);
    		}
    		else {
    			Toast.makeText(getApplicationContext(), "Incomplete form", Toast.LENGTH_SHORT).show();
    		}
    		
    		/* modified by weijie (Reason: follow SRP to get higher grade? LOL)
    		if( (title.length() == 0) || (location_str.length() == 0) ) {
    			// prompt user for invalid input
    		}
    		// Add to DataBase
    		else {
        		db.addPlaceIt(new PlaceIt(title, "Active", location, location_str));
        		
    		}
			*/
    		
    		
    		return true;
    	}
    	else if ( item.getItemId() == R.id.datebase_cancel ) {
    		
    		// When clicked, clear all the fields
    		EditText title_field = (EditText) findViewById(R.id.editText1);
    		EditText description_field = (EditText) findViewById(R.id.editText2);
    		EditText location_field = (EditText) findViewById(R.id.location);
    		title_field.clearComposingText();
    		description_field.clearComposingText();
    		location_field.clearComposingText();
    		// set toggles back to OFF position.
    		
    		// Go back to the map view
    		
    		Intent intent2 = new Intent(this, MainActivity.class);
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