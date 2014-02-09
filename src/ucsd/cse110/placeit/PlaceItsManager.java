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
	private EditText editViewTitle;
	private EditText editViewDescription;
	private LatLng location;
	private int id;
	private String title;
	private String description;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load layout
		setContentView(R.layout.place_its_manager_activity);
		
		
		
		// Three cases
				// Case 1: called by search from search bar
				// Case 2: called by pressOnMap [LatLng given]
				// Case 3: called by ListView [All info]
				// cases ended
				
				Intent info = getIntent();
				int validate = info.getIntExtra("ucsd.cs110.placeit.CheckSrouce", 2);
				
				// Add case 3 later
				if (validate == 1)
				{
					// do nothing for now
				}
				else if (validate == 2)//change to else if later on
				{
					Bundle b = getIntent().getParcelableExtra("locationOnlyBundle");
					location = b.getParcelable("ucsd.cs110.placeit.LocationOnly");
				}
				else // case 3
				{
					Log.i("I am inside case 3", "Case 3");
					Bundle b = getIntent().getParcelableExtra("locationOnlyBundle");
					location = b.getParcelable("ucsd.cs110.placeit.LocationOnly");
					id = info.getIntExtra("idIntent", 0) ;
					title = info.getStringExtra("titleIntent");
					description = info.getStringExtra("descriptionIntent");
					
				}
        

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
		editViewTitle = (EditText) findViewById(R.id.editTextTitle);
		editViewDescription = (EditText) findViewById(R.id.editTextDesc);
		try {
			editView.setText((new GetAddressTask(this)).execute(location).get());
			editViewTitle.setText(title);
			editViewDescription.setText(description);
			Log.i("what are title and desc?", title+" and "+description);
			
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
    		
    		EditText title_field = (EditText) findViewById(R.id.editTextTitle);
    		EditText description_field = (EditText) findViewById(R.id.editTextDesc);
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
    			//Log.i("True", "Checker passed!");
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
    		EditText title_field = (EditText) findViewById(R.id.editTextTitle);
    		EditText description_field = (EditText) findViewById(R.id.editTextDesc);
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