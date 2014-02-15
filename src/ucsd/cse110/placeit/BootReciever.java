package ucsd.cse110.placeit;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * Restarts all the PlaceIt alarms on reboot
 */
public class BootReciever extends BroadcastReceiver {

	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			
			Log.i("BootReciever","EEE");
			
			PlaceItDbHelper db = new PlaceItDbHelper(context);
			ProximityAlertManager paManager = new ProximityAlertManager(context);
			List<PlaceIt> placeItList = db.getAllPlaceIts(PlaceItUtil.ACTIVE);
			
			// reset the alarms and proximity alerts
			for (PlaceIt placeIt : placeItList) {
				placeIt.getSchedule().setRepeatingAlarm(context, placeIt.getId());
				paManager.addProximityAlert(placeIt);
				
			}			
        }
	}
	
}
