package ucsd.cse110.placeit;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements 
OnMarkerClickListener, OnMapLongClickListener, OnCameraChangeListener,
ConnectionCallbacks, OnConnectionFailedListener, LocationListener { 
	
	///////////////////////////// Keys ///////////////////////////
	
	public final static String LAT = "ucsd.cse110.placeit.LAT";
	public final static String LNG = "ucsd.cse110.placeit.LNG";
	
	///////////////////////////// Private Variables ///////////////////////////
	
	private GoogleMap mMap; 				// the google map 
	private LocationClient mLocationClient; // tracks user movement

 
	// These settings are the same as the settings for the map. They will 
    // in fact give you updates at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	
    ///////////////////////////// Activity States ///////////////////////////	
    
    // Defines what to do when this activity initially opens
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        final ActionBar actionBar = getActionBar();
        // Hide Action bar Icon
        actionBar.setDisplayShowHomeEnabled(false);
 
        // Hide Action bar Title
        //actionBar.setDisplayShowTitleEnabled(false);

        setUpMapIfNeeded();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
    	
        // Inflate the menu; adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// Menu buttons click to associated activity
    	switch (item.getItemId()) {
    	
    	case R.id.list_view_btn:
    		Log.i("MainActive", "list button click");
    		Intent intent1 = new Intent(this, ListActivity.class);
        	startActivity(intent1);
    	return true;
    	
    	case R.id.create_event_btn:
    		Log.i("MainActive", "create button click");
    		Intent intent2 = new Intent(this, PlaceItsManager.class);
        	startActivity(intent2);
    	return true;

    	default:
    		return super.onOptionsItemSelected(item);		
    	}
    }  
    
    // Defines what to do when this activity is opened again
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        
    }
    
    ///////////////////////////// Setup Methods /////////////////////////////
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
            		.findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
            	mMap.setOnMapLongClickListener(this);
                mMap.setOnCameraChangeListener(this);
                mMap.setOnMarkerClickListener(this);
                mMap.setMyLocationEnabled(true);
            }
        }
    }
    
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    ///////////////////////////// Other Methods /////////////////////////////
    
    // This is where we create a PlaceIt from the map
    public void onMapLongClick(LatLng point) {
    	mMap.addMarker(new MarkerOptions()
	        .position(point)
	        .draggable(true))
	        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
    	
    	Intent intent = new Intent(this, PlaceItFormActivity.class); 

    	intent.putExtra(LAT, point.latitude);
    	intent.putExtra(LNG, point.longitude);
	    startActivity(intent);
    }

    @Override
    public void onCameraChange(final CameraPosition position) {
    	//Do nothing... for now
    }

	// Should show a dialog 
	public boolean onMarkerClick(Marker marker) {
		
		// Code to show info box
		
		return false;
	}
	
	public void onLocationChanged(Location location) {
		// We need to figure out some way to check if this location corresponds to 
		// a location of one the PlaceIt's in the DB. 
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing... for now
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
    
}
