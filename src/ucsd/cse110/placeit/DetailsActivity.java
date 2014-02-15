package ucsd.cse110.placeit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
		int placeIt_id = intent.getIntExtra(PlaceItUtil.PLACEIT_ID, -1);
		
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
//			schedule_field.setText(placeIt.getScheduled_date());
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
	    removeAlarm(placeIt.getId());
	    
	    startActivity(intent);
	    db.close();
	}
	
	
	private void removeProximityAlert(long l) {

        String context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(context);

        Intent intent = new Intent(PlaceItUtil.PROX_ALERT_INTENT);
        PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), (int) l , intent, 0);
        locationManager.removeProximityAlert(operation);
    }
	
	private void removeAlarm(long l) {

		Intent reactivatePlaceIt = new Intent(this, AlarmReceiver.class);
		reactivatePlaceIt.putExtra(PlaceItUtil.PLACEIT_ID, l);
	    PendingIntent recurringActivation = PendingIntent.getBroadcast(this,
	    		(int) l, reactivatePlaceIt, PendingIntent.FLAG_CANCEL_CURRENT);
	    AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	    
	    if (alarms!= null) {
	    	Log.i("REMOVED", "ALARM");
	    	alarms.cancel(recurringActivation);
	    }
	    
    }
}
