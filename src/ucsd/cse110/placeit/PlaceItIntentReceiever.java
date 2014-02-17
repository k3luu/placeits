package ucsd.cse110.placeit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/*
 * Defines what should be done when a proximityAlert is triggered
 */
public class PlaceItIntentReceiever extends BroadcastReceiver {
	
	String lm_key = LocationManager.KEY_PROXIMITY_ENTERING;
	private PlaceIt placeIt;

	// triggered when user enters PlaceIt boundary
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// recover the PlaceIt object that set off this alert
		PlaceItDbHelper db = new PlaceItDbHelper(context);
		int placeIt_id = intent.getIntExtra(PlaceItUtil.PLACEIT_ID, -1);
		placeIt = db.getPlaceIt(placeIt_id);
		
		if (placeIt == null) return;
		
		Boolean userEntering = intent.getBooleanExtra(lm_key, false);
		if (userEntering) {
						
			// set the status of the PlaceIt to triggered
			placeIt.setStatus(PlaceItUtil.TRIGGERED);
			db.updatePlaceIt(placeIt);
			db.close();
			
		}
		else {		
			Log.d(getClass().getSimpleName(), "exiting");
		}
       
        //Pass the notification to the notification manager
		Intent displayDetailsIntent = new Intent(context,  NotificationActivity.class);
		displayDetailsIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt.getId());
		PendingIntent displayDetailsPendingIntent = PendingIntent.getActivity(context, 0, displayDetailsIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Intent repostIntent = new Intent(context,  NotificationActivity.class);
		repostIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt.getId());
		repostIntent.putExtra(PlaceItUtil.REPOST, true);
		PendingIntent repostPendingIntent = PendingIntent.getActivity(context, 0, repostIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	
		// create the notification
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
 			    .setSmallIcon(R.drawable.launch)
 			    .setContentTitle(placeIt.getTitle())
 			    .setContentText(placeIt.getStatus())
 			    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
 			    .addAction(R.drawable.ic_launcher,"Details", displayDetailsPendingIntent) 
 		        .addAction(R.drawable.ic_launcher, "Repost", repostPendingIntent);
		
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		mBuilder.setContentIntent(displayDetailsPendingIntent);
		mNotifyMgr.notify(placeIt_id, mBuilder.build());
	}
}
