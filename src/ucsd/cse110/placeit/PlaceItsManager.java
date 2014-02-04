package ucsd.cse110.placeit;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

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
		
		Intent checkWhere = getIntent();
		int validate = checkWhere.getIntExtra("ucsd.cs110.placeit.CheckSrouce", 1);
		
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
		(new GetAddressTask(this)).execute(location);
		Log.i(location.toString(), "What is the location");
		Log.i(editView.getText().toString(), "What is there?");
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
    		
    		String title = title_field.getText().toString();
    		String description = description_field.getText().toString();
    		String location_str = location_field.getText().toString();
    		Log.i("title",title);
 
    		
    		// When clicked, first validate
    		if( (title.length() == 0) || (location_str.length() == 0) ) {
    			//throw some error
    		}
    		// Add to DataBase
    		else {
        		
        		db.addPlaceIt(new PlaceIt(title, "Active", location));
    		}

    		
    		Intent intent1 = new Intent(this, MainActivity.class);
    		startActivity(intent1);
    		return true;
    	}
    	else if ( item.getItemId() == R.id.datebase_cancel ) {
    		
    		// When clicked, clear all the fields
    		
    		// Go back to the list view
    		
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
	
	/////////////////////////////// Geocode AsyncTask /////////////////////////
	
	private class GetAddressTask extends AsyncTask<LatLng, Void, String>
	{
		Context mContext;
		public GetAddressTask(Context context) {
		    super();
		    mContext = context;
		}
	/**
	 * Get a Geocoder instance, get the latitude and longitude
	 * look up the address, and return it
	 *
	 * @params params One or more Location objects
	 * @return A string containing the address of the current
	 * location, or an empty string if no address can be found,
	 * or an error message
	 */
		
		

		@Override
		protected String doInBackground(LatLng... params)
		{
			Log.i("INTO", "hey");
		    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		    // Get the current location from the input parameter list
		    LatLng loc = params[0];
		    // Create a list to contain the result address
		    List<Address> addresses = null;
		    try {
		        /*
		         * Return 1 address.
		         */
		        addresses = geocoder.getFromLocation(loc.latitude,
		                loc.longitude, 1);
		    } catch (IOException e1) {
		    Log.e("LocationSampleActivity",
		            "IO Exception in getFromLocation()");
		    e1.printStackTrace();
		    return ("IO Exception trying to get address");
		    } catch (IllegalArgumentException e2) {
		    // Error message to post in the log
		    String errorString = "Illegal arguments " +
		            Double.toString(loc.latitude) +
		            " , " +
		            Double.toString(loc.longitude) +
		            " passed to address service";
		    Log.e("LocationSampleActivity", errorString);
		    e2.printStackTrace();
		    return errorString;
		    }
		    // If the reverse geocode returned an address
		    if (addresses != null && addresses.size() > 0) {
		        // Get the first address
		        Address address = addresses.get(0);
		        /*
		         * Format the first line of address (if available),
		         * city, and country name.
		         */
		        String addressText = String.format(
		                "%s, %s, %s",
		                // If there's a street address, add it
		                address.getMaxAddressLineIndex() > 0 ?
		                        address.getAddressLine(0) : "",
		                // Locality is usually a city
		                address.getLocality(),
		                // The country of the address
		                address.getCountryName());
		        // Return the text
		        return addressText;
		    }
		    else {
		        return "No address found";
		    }
		}
		protected void onPostExecute(String address) {
            // Display the results of the lookup.
			Log.i(address, "the address.!");
            editView.setText(address);
        }
	}
}