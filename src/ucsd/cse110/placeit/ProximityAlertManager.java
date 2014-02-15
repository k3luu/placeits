package ucsd.cse110.placeit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class ProximityAlertManager { 
	
	private Context context;
	private LocationManager mLocationManager;		// manage users location
	private Location lastKnowLocation;
	private Location placeItLocation;
	
	public ProximityAlertManager(Context context) {
		this.context = context;
		 mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		 placeItLocation = new Location(LocationManager.NETWORK_PROVIDER);
	}
	
	// create proximity alerts for the given PlaceIt
    public void addProximityAlert(final PlaceIt placeIt) {
    	
    	/*placeItLocation.setLatitude(placeIt.getLocation().latitude);
    	placeItLocation.setLongitude(placeIt.getLocation().longitude);
    	lastKnowLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	
    	// add the sensor after 45 Minutes if the user is within range
    	if (lastKnowLocation.distanceTo(placeItLocation) <= PlaceItUtil.ALERT_RADIUS) {
    		
    		Log.i("User in proximity", "Waiting 45 minutes");
    		
    		final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                	runCommand(placeIt);
                }
            }, PlaceItUtil.INTERVAL_45_MINUTES);
    	}
    	else {
    		runCommand(placeIt);
    	}*/
    	runCommand(placeIt);
    }
    
    // command to add proximityAlert
    private void runCommand(PlaceIt placeIt) {
    	int placeIt_Id = placeIt.getId();
    	
        Intent intent = new Intent(PlaceItUtil.PROX_ALERT_INTENT);
        intent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt_Id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, placeIt_Id, intent, 0);
        mLocationManager.addProximityAlert(
        		placeIt.getLocation().latitude, 
        		placeIt.getLocation().longitude, 
        		PlaceItUtil.ALERT_RADIUS, 
        		PlaceItUtil.ALERT_EXPIRATION, 
                pendingIntent 
        );

        IntentFilter filter = new IntentFilter(PlaceItUtil.PROX_ALERT_INTENT);
        context.registerReceiver(new PlaceItIntentReceiever(), filter);
        Toast.makeText(context,"PlaceIt Proximity Alert Added"+ placeIt_Id,Toast.LENGTH_SHORT).show();
    }
    
    // remove proximity alerts for the given PlaceIt
    public void removeProximityAlert(int placeIt_id) {

        String lsContext = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) context.getSystemService(lsContext);

        Intent intent = new Intent(PlaceItUtil.PROX_ALERT_INTENT);
        PendingIntent operation = PendingIntent.getBroadcast(context.getApplicationContext(), placeIt_id , intent, 0);
        locationManager.removeProximityAlert(operation);
    }

}
