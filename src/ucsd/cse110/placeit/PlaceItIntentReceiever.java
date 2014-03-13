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
	private Context context;

	// triggered when user enters PlaceIt boundary
	@Override
	public void onReceive(Context myContext, Intent intent) {
		
		// recover the PlaceIt object that set off this alert
		context = myContext;
		PlaceItDbHelper db = new PlaceItDbHelper(context);
		int placeIt_id = intent.getIntExtra(PlaceItUtil.PLACEIT_ID, -1);
		placeIt = db.getPlaceIt(placeIt_id);
		
		if (placeIt == null) return;
		
		Boolean userEntering = intent.getBooleanExtra(lm_key, false);
		if (userEntering) {
						
			// set the status of the PlaceIt to triggered
			placeIt.setStatus(PlaceItUtil.TRIGGERED);
			
			db.updatePlaceIt(placeIt);
			
			// 1. remove online -- 2. add the most  -- 3. Synchronization
			OnlineDatabaseAddPlaceIt odba = new OnlineDatabaseAddPlaceIt (null, placeIt);
			odba.startAddingPlaceIt();
			new OnlineLocalDatabaseSynchronization(context);	
			
			db.close();
			
		}
		else {		
			Log.d(getClass().getSimpleName(), "exiting");
		}
       
        // Intent to display the contents of the PlaceIt
		Intent displayDetailsIntent = new Intent(context,  NotificationActivity.class);
		displayDetailsIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt.getId());
		displayDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent displayDetailsPendingIntent = PendingIntent.getActivity(context, 0, displayDetailsIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Intent to repost the PlaceIt
		Intent repostIntent = new Intent(context,  NotificationActivity.class);
		repostIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt.getId());
		repostIntent.putExtra(PlaceItUtil.REPOST, true);
		PendingIntent repostPendingIntent = PendingIntent.getActivity(context, 1, repostIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		
		// create the notification
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
 			    .setSmallIcon(R.drawable.launch)
 			    .setContentTitle(placeIt.getTitle())
 			    .setContentText(placeIt.getShortDescription())
 			    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
 		        .addAction(R.drawable.ic_launcher, PlaceItUtil.REPOST_OPTION, repostPendingIntent);
		
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder.setContentIntent(displayDetailsPendingIntent);
		mBuilder.setAutoCancel(true);
		mNotifyMgr.notify(placeIt_id, mBuilder.build());
	}
}
