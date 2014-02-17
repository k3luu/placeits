package ucsd.cse110.placeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Reposts a PlaceIt based on it's schedule
 * Is called when a PlaceIt's scheduled repost time is hit
 */
public class AlarmReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// start the alarm service to reactive the PlaceIt
		context.startService(new Intent(context, AlarmService.class));
		
	}
}