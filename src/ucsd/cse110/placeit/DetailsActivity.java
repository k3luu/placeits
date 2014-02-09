package ucsd.cse110.placeit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class DetailsActivity extends Activity {

	private PlaceItDbHelper db;
	private PlaceIt placeIt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		Intent intent = getIntent();
		int placeIt_id = intent.getIntExtra(MainActivity.PLACEIT_ID, -1);
		
		TextView title_field = (TextView) findViewById(R.id.title);
		TextView description_field = (TextView) findViewById(R.id.description);
		TextView location_field = (TextView) findViewById(R.id.location);
		TextView schedule_field = (TextView) findViewById(R.id.schedule);
		
		if(placeIt_id != -1) {
			db = new PlaceItDbHelper(this);
			placeIt = db.getPlaceIt(placeIt_id);
			
			title_field.setText(placeIt.getTitle());
			description_field.setText(placeIt.getDescription());
			location_field.setText(placeIt.getLocation_str());
			schedule_field.setText(placeIt.getScheduled_date());
		}
		else {
			title_field.setText("Invalid PlaceIt");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}
	
	public void deletePlaceIt(View view) {
	    Intent intent = new Intent(this, MainActivity.class);

	    // delete the PlaceIt from the database
	    db.deletePlaceIt(placeIt);
	    
	    // remove the ProximityAlert
	    removeProximityAlert(placeIt.getId());
	    
	    startActivity(intent);
	    db.close();
	}
	
	
	private void removeProximityAlert(int placeIt_id) {

        String context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(context);

        Intent intent = new Intent(MainActivity.PROX_ALERT_INTENT);
        PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), placeIt_id , intent, 0);
        locationManager.removeProximityAlert(operation);
    }
}
