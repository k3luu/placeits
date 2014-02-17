package ucsd.cse110.placeit;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * Restarts all the PlaceIt alarms/proximityAlerts on reboot
 */
public class BootService extends Service {
	
	private ProximityAlertManager paManager;
	private List<PlaceIt> activePlaceItList;
	private List<PlaceIt> triggeredPlaceItList;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void onCreate() {
		PlaceItDbHelper db = new PlaceItDbHelper(getBaseContext());
		paManager = new ProximityAlertManager(getBaseContext());
		activePlaceItList = db.getAllPlaceIts(PlaceItUtil.ACTIVE);
		triggeredPlaceItList = db.getAllPlaceIts(PlaceItUtil.TRIGGERED);
		db.close();
	}

	@Override
	public void onDestroy() {
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		
		// reset the alarms and proximity alerts of active PlaceIts
		for (PlaceIt activePlaceIt : activePlaceItList) {
			activePlaceIt.getSchedule().setRepeatingAlarm(getBaseContext(), activePlaceIt.getId());
			paManager.addProximityAlert(activePlaceIt);
		}
		
		// reset the alarms and proximity alerts of completed PlaceIts
		for (PlaceIt triggeredPlaceIt : triggeredPlaceItList) {
			triggeredPlaceIt.getSchedule().setRepeatingAlarm(getBaseContext(), triggeredPlaceIt.getId());
		}
	}

	

}
