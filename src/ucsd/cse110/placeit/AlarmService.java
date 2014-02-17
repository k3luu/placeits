package ucsd.cse110.placeit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * Restarts all the PlaceIt alarms/proximityAlerts on reboot
 */
public class AlarmService extends Service {
	
	private PlaceItDbHelper db;
	private ProximityAlertManager paManager; 

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void onCreate() {
		db = new PlaceItDbHelper(getBaseContext());
		paManager = new ProximityAlertManager(getBaseContext());
		db = new PlaceItDbHelper(getBaseContext());
	}

	@Override
	public void onDestroy() {
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		// get placeIt that fired off this alarm
		long placeIt_id = intent.getLongExtra(PlaceItUtil.PLACEIT_ID, -1);
		PlaceIt placeIt = db.getPlaceIt(placeIt_id);
		
		if(placeIt != null) {
			// set the status of the placeIt to active
			placeIt.setStatus(PlaceItUtil.ACTIVE);
			paManager.addProximityAlert(placeIt);
			db.updatePlaceIt(placeIt);
		}
		
		db.close();
	}

	

}
