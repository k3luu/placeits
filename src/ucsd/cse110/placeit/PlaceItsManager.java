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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class PlaceItsManager extends FragmentActivity implements
ActionBar.TabListener, OnItemSelectedListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private EditText editView;
	private EditText editViewTitle;
	private EditText editViewDescription;
	private int data_id = -1;
	private LatLng location;
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
				int validate = info.getIntExtra("ucsd.cs110.placeit.CheckSrouce", 1);
				
				Log.i("validate: ", "It is ############### "+validate);
				
				// Add case 3 later
				if (validate == 1)//change to else if later on
				{	
					Log.i("Here", "LALALALALALALA");
					Bundle b = getIntent().getParcelableExtra("locationOnlyBundle");
					location = b.getParcelable("ucsd.cs110.placeit.LocationOnly");
				}
				else // case 3
				{
					data_id = info.getIntExtra("idIntent", -1);
					PlaceItDbHelper db = new PlaceItDbHelper(this);
					PlaceIt place = db.getPlaceIt(data_id);
					//Log.i("I am inside case 2", "Case 2");
					//Bundle b = getIntent().getParcelableExtra("locationOnlyBundle");
					
					
					//Log.i("data_id", "is "+data_id+" "+place.getId()+" "+place.getLocation_str());
					
					//Log.i("info 4", "why why why?");
					title = place.getTitle();
					description = place.getDescription();
					location = place.getLocation();
					//Log.i("info1 ", "is "+data_id+" "+location+" "+title+" "+description);
					//schedule = place.getSchedule();
					
					// listening for the users scheduling option
					//schedulingOption = schedule.getScheduled_option();
					//scheduleDOW = schedule.getScheduled_dow();
					//scheduleWeekInterval = schedule.getScheduled_week();
					//scheduleMinutes = schedule.getScheduled_minutes();
				}
        
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
		Log.i("info ", "is " + data_id + " " + location+" "+title+" "+description);
		// fills the Location with the string version passed by the map/search bar
		editView = (EditText) findViewById(R.id.location);
		editViewTitle = (EditText) findViewById(R.id.editTextTitle);
		editViewDescription = (EditText) findViewById(R.id.editTextDesc);
		
		Spinner spinner = (Spinner) findViewById(R.id.scheduling_option_spinner);
		spinner.setOnItemSelectedListener(this);
		
		Log.i("info ", "is "+data_id+" "+location+" "+title+" "+description);
		try {
			editView.setText((new GetAddressTask(this)).execute(location).get());
			editViewTitle.setText(title);
			editViewDescription.setText(description);
			
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
    		
    		String title = title_field.getText().toString();
    		String description = description_field.getText().toString();
    		String location_str = location_field.getText().toString();
    		
    		// scheduling fields
    		Spinner scheduling_field = (Spinner) findViewById(R.id.scheduling_option_spinner);
    		Spinner day_field = (Spinner) findViewById(R.id.day_spinner);
    		Spinner week_interval_field = (Spinner) findViewById(R.id.week_spinner);
    		EditText minutes_field = (EditText) findViewById(R.id.minute_field);
    		
    		String schedulingOption = String.valueOf(scheduling_field.getSelectedItem());
    		String scheduleDOW = String.valueOf(day_field.getSelectedItem());
    		String scheduleWeekInterval = String.valueOf(week_interval_field.getSelectedItem());
    		int scheduleMinutes = -1;
    		try {
    			scheduleMinutes = Integer.parseInt(minutes_field.getText().toString());
    		} catch (NumberFormatException e) {}

    		// let Scheduler class determine the PlaceIt's schedule
    		Scheduler schedule = new Scheduler(schedulingOption, scheduleDOW, scheduleWeekInterval, scheduleMinutes);
    		
    		PlaceIt placeIt;
    		if (data_id != PlaceItUtil.NOTSET) {
				placeIt = db.getPlaceIt(data_id);
				placeIt.setStatus("Active");
				placeIt.setTitle(title);
				placeIt.setDescription(description);
				placeIt.setLocation(location);
				placeIt.setLocation_str(location_str);
				placeIt.setSchedule(schedule);
			}
			else {
				// create our PlaceIt and validate the contents
	    		placeIt = new PlaceIt(title, "Active", description, location, location_str, schedule);
			}
    		
    		
    		PlaceItDataChecker checker = new PlaceItDataChecker(placeIt);
    		
    		// If valid add it to the database otherwise prompt user
    		if (checker.checkNormal()) {
    			
    			long placeItId;
    			// add the PlaceIt to our database
    			if (data_id != PlaceItUtil.NOTSET) {
    				placeItId = db.updatePlaceIt(placeIt);
    				Log.i("I am fixing", "error");
    			}
    			else {
    				placeItId = db.addPlaceIt(placeIt);
        			db.close();
    			}
    			
    			// set up the Alarm for the PlaceIt if possible
    			placeIt.getSchedule().setRepeatingAlarm(this, placeItId);
    			
    			ProximityAlertManager paManager = new ProximityAlertManager(this);
    			paManager.addProximityAlert(placeIt);
    			
    			SaveLastLocation action = new SaveLastLocation(location);
    			action.saveLastPlaceIt(db);
    			
    			// navigate back to the map
    			Intent mapIntent = new Intent(this, MainActivity.class);
        		startActivity(mapIntent);
    		}
    		else {
    			Toast.makeText(getApplicationContext(), "Incomplete form", Toast.LENGTH_SHORT).show();
    		}
	
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
    		Intent mapIntent = new Intent(this, MainActivity.class);
        	startActivity(mapIntent);
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
	
	// when an item is selected from the Scheduling options make the appropriate spinner appear
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		String schedulingChoice = (String) parent.getItemAtPosition(pos);
		
		LinearLayout weekly_choicesLayout = (LinearLayout) findViewById(R.id.weekly_choices);
		LinearLayout minute_choiceLayout = (LinearLayout) findViewById(R.id.minute_choice);
		
	    if(schedulingChoice.equals(PlaceItUtil.WEEKLY_SCHEDULE)) {
	    	
	    	weekly_choicesLayout.setVisibility(View.VISIBLE);
	    	minute_choiceLayout.setVisibility(View.GONE);
	    }
	    else if(schedulingChoice.equals(PlaceItUtil.MINUTE_SCHEDULE)) {
	    	weekly_choicesLayout.setVisibility(View.GONE);
	    	minute_choiceLayout.setVisibility(View.VISIBLE);
	    }
	    else {
	    	weekly_choicesLayout.setVisibility(View.GONE);
	    	minute_choiceLayout.setVisibility(View.GONE);
	    }
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
	    // do nothing
	}
	
}