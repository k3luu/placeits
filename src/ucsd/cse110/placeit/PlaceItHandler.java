package ucsd.cse110.placeit;

import com.google.android.gms.maps.model.LatLng;


/* PlaceItScheduler handles the scheduling of a PlaceIt.
 * NOTE: NOT SURE IF SHOULD BE USED
 */
public class PlaceItHandler {
	
	// determines whether a PlaceIt should be rescheduled
	public void checkIfTriggered(PlaceIt placeIt, LatLng user_loc) {
	
		// Need to then check if users location is within a 1/2 mile of a PlaceIt
		if (placeIt.getLocation() == user_loc) {
			placeIt.setStatus("Triggered");
			return;
		}
		
		
		return;
	}
	
	// determines whether a PlaceIt should be rescheduled
	public void reSchedule(PlaceIt placeIt) {
		
		if (placeIt.getScheduled_date() == null) {
			return;
		}
	}
	
	
	
}

