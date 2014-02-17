package ucsd.cse110.placeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Starts the service that restarts all the PlaceIt alarms/proximityAlerts on reboot
 */
public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(PlaceItUtil.BOOT_COMPLETED)) {
			
			// start service to restart the ProximityAlerts for active PlaceIts
			// and reset the scheduled alarms for both active and completed PlaceIts
			context.startService(new Intent(context, BootService.class));

        }
	}
}
