package ucsd.cse110.placeit;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.Marker;

public class PlaceItInfoWindowClick implements OnInfoWindowClickListener {

	@Override
	public void onInfoWindowClick(Marker marker) {
		Log.i("hey", "it worked");
		
	}

}
