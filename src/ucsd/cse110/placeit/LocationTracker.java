package ucsd.cse110.placeit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationTracker extends Service implements LocationListener {
	public static final String TAG = "LocationTracker";

	private PlaceItDbHelper db;

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location = null; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public LocationTracker(Context context) {
		this.mContext = context;
		db = new PlaceItDbHelper(context);
		getLocation();
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network Enabled");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	//returns the latitude
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}

	//returns the longitude
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}

	// Function to check GPS/wifi enabled
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		
		
		List<PlaceIt> categoryPlaceIts = db.getCategoryPlaceIts(PlaceItUtil.USERNAME);
		
		for (PlaceIt placeIt: categoryPlaceIts) {
			// send search request to Google Places to see if user is near any categories
			for (String category: placeIt.getCategories()) {
				if (!category.equals("")) {
					try {
						
						//search this placeIt's categories
						JSONObject placeJSON = search(location.getLatitude(), location.getLongitude(), PlaceItUtil.ALERT_RADIUS, category);
						
						if(placeJSON != null) {
							//check if the status is OK if so notify with first value
							if(placeJSON.has("status") && !placeJSON.get("status").equals("OK")) {
								
								//create alert for this PlaceIt... skipping over any other categories this PLaceIt may have
								
								// Intent to display the contents of the PlaceIt
								Intent displayDetailsIntent = new Intent(mContext,  NotificationActivity.class);
								displayDetailsIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt.getId());
								displayDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								PendingIntent displayDetailsPendingIntent = PendingIntent.getActivity(mContext, 0, displayDetailsIntent, PendingIntent.FLAG_CANCEL_CURRENT);
								
								// Intent to repost the PlaceIt
								Intent repostIntent = new Intent(mContext,  NotificationActivity.class);
								repostIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt.getId());
								repostIntent.putExtra(PlaceItUtil.REPOST, true);
								PendingIntent repostPendingIntent = PendingIntent.getActivity(mContext, 1, repostIntent, PendingIntent.FLAG_CANCEL_CURRENT);

								
								// create the notification
								NotificationCompat.Builder mBuilder =
										new NotificationCompat.Builder(mContext)
						 			    .setSmallIcon(R.drawable.launch)
						 			    .setContentTitle(placeIt.getTitle())
						 			    .setContentText(placeIt.getShortDescription())
						 			    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
						 		        .addAction(R.drawable.ic_launcher, PlaceItUtil.REPOST_OPTION, repostPendingIntent);
								
								NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
								mBuilder.setContentIntent(displayDetailsPendingIntent);
								mBuilder.setAutoCancel(true);
								mNotifyMgr.notify(placeIt.getId(), mBuilder.build());

								break;
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
				
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public JSONObject search(double latitude, double longitude, double radius, String types) throws Exception {
		String url = PlaceItUtil.PLACES_SEARCH_URL + 
					 "key=" + PlaceItUtil.API_KEY +
				 	 "location=" + latitude + "," + longitude +
				 	 "radius=" + radius +
				 	 "sensor=false" +
				 	 "types=" + types;
		 
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);
			
			
			try {
				return new JSONObject(data);
				
			} catch (JSONException e) {
		
		    	Log.d(TAG, "Error in parsing JSON");
			}
			
		} catch (ClientProtocolException e) {
		
			Log.d(TAG, "ClientProtocolException while trying to connect to GAE");
		} catch (IOException e) {
		
			Log.d(TAG, "IOException while trying to connect to GAE");
		}
		return null;
	}

}
