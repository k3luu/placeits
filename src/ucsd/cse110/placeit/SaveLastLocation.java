/**
 * 
 */
package ucsd.cse110.placeit;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author WeiJie
 *
 */
public class SaveLastLocation {
	private LatLng location;
	SaveLastLocation() {}
	
	SaveLastLocation(LatLng loc) {
		this.location = loc;
	}
	
	public void saveLastPlaceIt(PlaceItDbHelper db)
    {
		List<PlaceIt> systemPlaceItList;
    	systemPlaceItList = db.getAllPlaceIts("HACKER");
    	PlaceIt place = systemPlaceItList.get(0);
    	place.setLocation(location);
    	db.updatePlaceIt(place);
    	db.close();
    }
}
