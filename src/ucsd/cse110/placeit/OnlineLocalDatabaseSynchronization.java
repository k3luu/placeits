package ucsd.cse110.placeit;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class OnlineLocalDatabaseSynchronization implements AsyncResponse{

	
	private GetPlaceitFromUrl asyncTask = new GetPlaceitFromUrl();
	private PlaceItDbHelper localDatabase;
	private ArrayList<PlaceIt> list = new ArrayList<PlaceIt>();
	private Activity activity;
	
	OnlineLocalDatabaseSynchronization(Activity myActivity) {
		activity = myActivity;
		localDatabase = new PlaceItDbHelper(myActivity);
		asyncTask.delegate = this;
		asyncTask.execute("http://cse110group30db.appspot.com/product");
	}
	
	@Override
	public void processFinish(JSONObject data) {
		// Check every single items between two databases. [always override local]
		
		
		// Parsing online database
		ArrayList<PlaceIt> onlineList = new ArrayList<PlaceIt>();
		
		try {
			
			JSONArray array = data.getJSONArray("data");

			for (int i = 0; i < array.length(); i++) {
				PlaceIt pl = new PlaceIt();
				JSONObject obj = array.getJSONObject(i);
				pl.setTitle(obj.get("name").toString());
				pl.setStatus(obj.get("placeItStatus").toString());
				pl.setDescription(obj.get("placeItDescription").toString());
				pl.setLocation(new LatLng(Double.parseDouble(obj.get("placeItLatitude").toString()), Double.parseDouble(obj.get("placeItLongitude").toString())));
				pl.setLocation_str(obj.get("placeItLocationString").toString());
				pl.setSchedule(new Scheduler(obj.get("placeItScheduledOption").toString(), obj.get("placeItScheduledDow").toString(), obj.get("placeItScheduledWeek").toString(), Integer.parseInt(obj.get("placeItScheduledMinutes").toString())));
				onlineList.add(pl);
			}

		} catch (JSONException e) {
			Log.d("ERROR", "Error in parsing JSON");
		}
		
		// Load local database
		list = localDatabase.getAllPlaceItsByUsername(PlaceItUtil.USERNAME);
		Log.i("local database", "size is "+list.size());
		
		int largest = 0;
		int smallest = 0;
		PlaceIt tempPL = new PlaceIt();
		if (list.size() > onlineList.size()) {
			Log.i("IMPORTANT", "Local > ONLINE");
			largest = list.size();
			smallest = onlineList.size();
			for (int i = 0; i < largest; i++) {
				if (i < smallest) {
					tempPL = onlineList.get(i);
					tempPL.setId(list.get(i).getId());
					localDatabase.updatePlaceIt(tempPL);
					Log.i("IMPORTANT", "SAME rewrote");
				}
				else {
					localDatabase.deletePlaceIt(list.get(i));
					Log.i("IMPORTANT", "Diff Deleted");
				}
			}
		}
		else if (list.size() < onlineList.size()){
			Log.i("IMPORTANT", "Local < ONLINE");
			largest = onlineList.size();
			smallest = list.size();
			for (int i = 0; i < largest; i++) {
				if (i < smallest) {
					tempPL = onlineList.get(i);
					tempPL.setId(list.get(i).getId());
					localDatabase.updatePlaceIt(tempPL);
					Log.i("IMPORTANT", "SAME rewrote");
				}
				else {
					localDatabase.addPlaceIt(onlineList.get(i));
					Log.i("IMPORTANT", "Diff added");
				}
			}
		}
		else {
			Log.i("IMPORTANT", "Local = ONLINE");
			for (int i = 0; i < onlineList.size(); i++) {
				tempPL = onlineList.get(i);
				tempPL.setId(list.get(i).getId());
				localDatabase.updatePlaceIt(tempPL);
			}
		}
		
		// after Synchronization, set alarm and others
		
		for (PlaceIt place:localDatabase.getAllPlaceItsByUsername(PlaceItUtil.USERNAME)) {
			// set up the Alarm for the PlaceIt if possible
			place.getSchedule().setRepeatingAlarm(activity, place.getId());
		}
		
		for (PlaceIt place:localDatabase.getAllPlaceIts(PlaceItUtil.ACTIVE)) {
			// set up proximity
			// WEIJIE paManager = new ProximityAlertManager(myActivity);
			// WEIJIE paManager.addProximityAlert(place);
		}
	}

}
