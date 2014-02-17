package ucsd.cse110.placeit;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/*
 * Handles the geocoding of positions
 */
public class GetAddressTask extends AsyncTask<LatLng, Void, String>
{
	Context mContext;
	
	public GetAddressTask(Context context) {
	    super();
	    mContext = context;
	}
/**
 * Get a Geocoder instance, get the latitude and longitude
 * look up the address, and return it
 *
 * @params params One or more Location objects
 * @return A string containing the address of the current
 * location, or an empty string if no address can be found,
 * or an error message
 */
	
	

	@Override
	protected String doInBackground(LatLng... params)
	{
	    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
	    // Get the current location from the input parameter list
	    LatLng loc = params[0];
	    // Create a list to contain the result address
	    List<Address> addresses = null;
	    try {
	        /*
	         * Return 1 address.
	         */
	        addresses = geocoder.getFromLocation(loc.latitude,
	                loc.longitude, 1);
	    } catch (IOException e1) {
	    Log.e("LocationSampleActivity",
	            "IO Exception in getFromLocation()");
	    e1.printStackTrace();
	    return ("IO Exception trying to get address");
	    } catch (IllegalArgumentException e2) {
	    // Error message to post in the log
	    String errorString = "Illegal arguments " +
	            Double.toString(loc.latitude) +
	            " , " +
	            Double.toString(loc.longitude) +
	            " passed to address service";
	    Log.e("LocationSampleActivity", errorString);
	    e2.printStackTrace();
	    return errorString;
	    }
	    // If the reverse geocode returned an address
	    if (addresses != null && addresses.size() > 0) {
	        // Get the first address
	        Address address = addresses.get(0);
	        /*
	         * Format the first line of address (if available),
	         * city, and country name.
	         */
	        
	        String city;
	        if (address.getLocality() == null) {
	        	city = address.getSubLocality();
	        }
	        else {
	        	city = address.getLocality();
	        }
	        
	        String addressText = String.format(
	                "%s, %s, %s",
	                // If there's a street address, add it
	                address.getMaxAddressLineIndex() > 0 ?
	                        address.getAddressLine(0) : "",
	                // Locality is usually a city
	                        
	                // address.getLocality(), (REPLACED this with above null checker)
	                        city,
	                        
	                // The country of the address
	                address.getCountryName());
	        // Return the text
	        return addressText;
	    }
	    else {
	        return "No address found";
	    }
	}
	protected void onPostExecute(String address) {
        // Display the results of the lookup.
    }
}