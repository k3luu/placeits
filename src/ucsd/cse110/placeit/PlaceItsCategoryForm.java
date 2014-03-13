package ucsd.cse110.placeit;

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

/*
 * Displays the form of the PlaceIt
 */
public class PlaceItsCategoryForm extends FragmentActivity implements
ActionBar.TabListener, OnItemSelectedListener {

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private EditText editView;
	private EditText editViewTitle;
	private EditText editViewDescription;
	private int data_id = -1;
	private String title;
	private String originalTitle;
	private String description;
	private ProximityAlertManager paManager;
	
	// category field
	private Spinner cateSpinner_1;
	private Spinner cateSpinner_2;
	private Spinner cateSpinner_3;
	private String categories[] = {"", "", ""};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load layout
		setContentView(R.layout.activity_place_its_category_form);
		
		// Three cases
		// Case 1: called by search from search bar or pressOnMap [LatLng given]
		// Case 2: called by ListView [All info]
		// cases ended
				
		Intent info = getIntent();
				
		// categories can only be created from List
		data_id = info.getIntExtra(PlaceItUtil.PLACEIT_ID, -1);
		PlaceItDbHelper db = new PlaceItDbHelper(this);
		PlaceIt placeIt = db.getPlaceIt(data_id);

		title = placeIt.getTitle();
		originalTitle = title;
		description = placeIt.getDescription();
		categories = placeIt.getCategories();
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
		
		// fills the Location with the string version passed by the map/search bar
		editView = (EditText) findViewById(R.id.location);
		editViewTitle = (EditText) findViewById(R.id.editTextTitle);
		editViewDescription = (EditText) findViewById(R.id.editTextDesc);
		
		Spinner spinner = (Spinner) findViewById(R.id.scheduling_option_spinner);
		spinner.setOnItemSelectedListener(this);
		
		cateSpinner_1 = (Spinner) findViewById(R.id.cate_spinner1);
		cateSpinner_2 = (Spinner) findViewById(R.id.cate_spinner2);
		cateSpinner_3 = (Spinner) findViewById(R.id.cate_spinner3);
		
		editViewTitle.setText(title);
		editViewDescription.setText(description);
			
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// the confirm button pressed
    	if ( item.getItemId() == R.id.database_add_edit ) {
    		
    		// get an instance of our database to add
    		PlaceItDbHelper db = new PlaceItDbHelper(this);
    		
    		// recover the strings form the edit views
    		EditText title_field = (EditText) findViewById(R.id.editTextTitle);
    		EditText description_field = (EditText) findViewById(R.id.editTextDesc);
    		EditText location_field = (EditText) findViewById(R.id.location);
    		
    		String title = title_field.getText().toString();
    		String description = description_field.getText().toString();
    		
    		// scheduling fields
    		Spinner scheduling_field = (Spinner) findViewById(R.id.scheduling_option_spinner);
    		Spinner day_field = (Spinner) findViewById(R.id.day_spinner);
    		Spinner week_interval_field = (Spinner) findViewById(R.id.week_spinner);
    		EditText minutes_field = (EditText) findViewById(R.id.minute_field);
    		
    		cateSpinner_1 = (Spinner) findViewById(R.id.cate_spinner1);
    		cateSpinner_2 = (Spinner) findViewById(R.id.cate_spinner2);
    		cateSpinner_3 = (Spinner) findViewById(R.id.cate_spinner3);
    		
    		categories[0] = String.valueOf (cateSpinner_1.getSelectedItem());
    		categories[1] = String.valueOf (cateSpinner_2.getSelectedItem());
    		categories[2] = String.valueOf (cateSpinner_3.getSelectedItem());
    		
    		String schedulingOption = String.valueOf(scheduling_field.getSelectedItem());
    		String scheduleDOW = String.valueOf(day_field.getSelectedItem());
    		String scheduleWeekInterval = String.valueOf(week_interval_field.getSelectedItem());
    		int scheduleMinutes = PlaceItUtil.NOTSET;
    		try {
    			scheduleMinutes = Integer.parseInt(minutes_field.getText().toString());
    		} catch (NumberFormatException e) {}

    		// let Scheduler class determine the PlaceIt's schedule
    		Scheduler schedule = new Scheduler(schedulingOption, scheduleDOW, scheduleWeekInterval, scheduleMinutes);
    		
    		PlaceIt placeIt;
    		// if a PlaceIt is already in the database
    		if (data_id != PlaceItUtil.NOTSET) {
				placeIt = db.getPlaceIt(data_id);
				Log.i("manager checker", "11111");
				paManager.removeProximityAlert(placeIt.getId());
				Log.i("manager checker", "111111");
		        placeIt.getSchedule().removeAlarm(this, placeIt.getId());
		        Log.i("manager checker", "11111111");
				placeIt.setStatus(PlaceItUtil.PLACEIT_ID);
				placeIt.setTitle(title);
				placeIt.setDescription(description);
				placeIt.setSchedule(schedule);
				placeIt.setCategories(categories);
			}
			else {
				// create a new PlaceIt
	    		placeIt = new PlaceIt(title, PlaceItUtil.ACTIVE, description, null, "", schedule, categories);
			}
    		
    		Log.i("manager checker", "1");
    		PlaceItDataChecker checker = new PlaceItDataChecker(placeIt);
    		Boolean validData = checker.checkNormal();
    		PlaceIt ppp = placeIt;
    		Log.i("manager checker", "2");
    		if (data_id != PlaceItUtil.NOTSET) {
    			if (ppp.getTitle().equals(originalTitle)) {
    			}
    			else {
    				validData = false;
    			}
    		}
    		Log.i("manager checker", "3");
    		
    		// If valid add it to the database otherwise prompt user
    		if (validData) {
    			
    			//long placeItId;
    			
    			// add the PlaceIt to our database
    			if (data_id != PlaceItUtil.NOTSET) {
    				//placeItId = db.updatePlaceIt(placeIt);
    				
    				// 1. remove online -- 2. add the most  -- 3. Synchronization
    				OnlineDatabaseAddPlaceIt odba = new OnlineDatabaseAddPlaceIt (this, placeIt);
    				odba.startAddingPlaceIt();
    				OnlineLocalDatabaseSynchronization olds = new OnlineLocalDatabaseSynchronization(this);
    			}
    			else {
    				//placeItId = db.addPlaceIt(placeIt);
    				OnlineDatabaseAddPlaceIt odap = new OnlineDatabaseAddPlaceIt(this, placeIt);
    				odap.startAddingPlaceIt();
    				//instead of updating local database, add placeit to online database first, then overwrite local.
    				OnlineLocalDatabaseSynchronization olds = new OnlineLocalDatabaseSynchronization(this);
    			}
    			
    		
    			
    			// WEIJIE paManager = new ProximityAlertManager(this);
    			// WEIJIE paManager.addProximityAlert(placeIt);
    			
    			// save the location to return to later on
    			// WEIJIE SaveLastLocation action = new SaveLastLocation(location);
    			// WEIJIE action.saveLastPlaceIt(db);
    			
    			// navigate back to the list
    			Intent listIntent = new Intent(this, ListActivity.class);
        		startActivity(listIntent);
    		}
    		else {
    			
    			if (data_id != PlaceItUtil.NOTSET) {
    				Toast.makeText(getApplicationContext(), "Modifying must keep same title", Toast.LENGTH_SHORT).show();
        		}
    			Toast.makeText(getApplicationContext(), "Incomplete form", Toast.LENGTH_SHORT).show();
    		}
    		db.close();
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
    		Intent mapIntent = new Intent(this, MapActivity.class);
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