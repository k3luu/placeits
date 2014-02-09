package ucsd.cse110.placeit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PlaceItIntentReceiever extends BroadcastReceiver {
	
	String lm_key = LocationManager.KEY_PROXIMITY_ENTERING;

	// triggered when user enters PlaceIt boundary
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// recover the PlaceIt object that set off this alert
		PlaceItDbHelper db = new PlaceItDbHelper(context);
		int placeIt_id = intent.getIntExtra(MainActivity.PLACEIT_ID, -1);
		PlaceIt placeIt = db.getPlaceIt(placeIt_id);
		
		Boolean userEntering = intent.getBooleanExtra(lm_key, false);
		if (userEntering) {
						
			// set the status of the PlaceIt to triggered
			placeIt.setStatus(MainActivity.TRIGGERED);
			db.updatePlaceIt(placeIt);
			db.close();
			
		}
		else {			
			Log.d(getClass().getSimpleName(), "exiting");
		}
		
		Intent alertIntent = new Intent(context, MainActivity.class);
		PendingIntent alertPendingIntent = PendingIntent.getActivity(context, placeIt_id, alertIntent, 0);
		
		// create the notification
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
 			    .setSmallIcon(R.drawable.ic_launcher)
 			    .setContentTitle(placeIt.getTitle())
 			    .setContentText(placeIt.getDescription());
		
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.setContentIntent(alertPendingIntent);
		mNotifyMgr.notify(placeIt_id, mBuilder.build());
	}

}
