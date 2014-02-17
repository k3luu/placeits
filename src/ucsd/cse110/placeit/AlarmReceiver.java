package ucsd.cse110.placeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * Reposts a PlaceIt based on it's schedule
 * Is called when a PlaceIt's scheduled repost time is hit
 */
public class AlarmReceiver extends BroadcastReceiver {
	
	private PlaceItDbHelper db;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		db = new PlaceItDbHelper(context);
		
		// get placeIt that fired off this alarm
		long placeIt_id = intent.getLongExtra(PlaceItUtil.PLACEIT_ID, -1);
		PlaceIt placeIt = db.getPlaceIt(placeIt_id);
		Log.i("AlarmReceiver", String.valueOf(placeIt_id));
		
		if(placeIt != null) {
			Log.i("AlarmReceiver", placeIt.getTitle());
			// set the status of the placeIt to active
			placeIt.setStatus(PlaceItUtil.ACTIVE);
			db.updatePlaceIt(placeIt);
		}
		
		db.close();
		
	}
}