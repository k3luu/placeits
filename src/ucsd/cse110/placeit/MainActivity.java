package ucsd.cse110.placeit;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements 
OnMapLongClickListener, OnCameraChangeListener,ConnectionCallbacks, 
OnConnectionFailedListener, LocationListener, OnMarkerDragListener { 
	
	///////////////////////////////// Keys ////////////////////////////////////
	
	public final static String LAT = "ucsd.cse110.placeit.LAT";
	public final static String LNG = "ucsd.cse110.placeit.LNG";
	public final static String LATLNG = "ucsd.cse110.placeit.LATLNG";
	
	///////////////////////////// Private Variables ///////////////////////////
	
	private GoogleMap mMap; 				// the google map 
	private LocationClient mLocationClient; // tracks user movement
	private HashMap<String, Integer> placeItMarkers = new HashMap<String, Integer>(); //keeps a map of our markers and PLaceIts
	
	// get an instance of our database to add
	PlaceItDbHelper db = new PlaceItDbHelper(this);

 
	// These settings are the same as the settings for the map. They will 
    // in fact give you updates at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	
    ///////////////////////////// Activity States /////////////////////////////
    
    // Defines what to do when this activity initially opens
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        
        final ActionBar actionBar = getActionBar();
        // Hide Action bar Icon
        actionBar.setDisplayShowHomeEnabled(false);
 
        // The search bar
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.search_bar);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this));
        
        autoCompView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Address address = (Address) parent.getItemAtPosition(position);
				LatLng location = new LatLng(address.getLatitude(), address.getLongitude());
				onMapLongClick(location);
			}
		});

        
    }

    public boolean onCreateOptionsMenu(Menu menu) {
    	
        // Inflate the menu; adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// Menu buttons click to associated activity
    	
    	if ( item.getItemId() == R.id.list_view_btn ) {
    		Intent intent1 = new Intent(this, ListActivity.class);
        	startActivity(intent1);
        	return true;
    	}
    	else if ( item.getItemId() == R.id.create_event_btn ) {
    		Intent intent2 = new Intent(this, PlaceItsManager.class);
    		intent2.putExtra("ucsd.cs110.placeit.CheckSrouce", 1);
        	startActivity(intent2);
        	return true;
    	}
    	else {
    		return super.onOptionsItemSelected(item);
    	}
    }  
    
    // Defines what to do when this activity is opened again
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
        populateMap();
        
    }
    
    ///////////////////////////// Setup Methods ///////////////////////////////
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
            		.findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
            	mMap.setOnMapLongClickListener(this);
                mMap.setOnCameraChangeListener(this);
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

    ///////////////////////////// Other Methods ///////////////////////////////
    
    // populates the map with all the PlaceIt's stored in the database
    public void populateMap() {
    	
    	LatLng markerPosition;
    	PlaceIt pl;
		
    	// loop through all active PlaceIt's and populate map with them
    	List<PlaceIt> activePlaceItList = db.getAllPlaceIts("Active");
    	
		for (int i = 0; i < activePlaceItList.size(); i++) {
			pl = activePlaceItList.get(i);
			markerPosition = pl.getLocation();
			Marker marker = mMap.addMarker(new MarkerOptions()
									        .position(markerPosition)
									        .title(pl.getTitle())
									        .snippet(pl.getLocation_str())
									        .draggable(true)
									        );
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
			placeItMarkers.put(marker.getId(), pl.getId());
		}
    }
    
    ///////////////////// OnMapLongClickListener Methods //////////////////////
    
    // Create a PlaceIt at the position clicked
    public void onMapLongClick(LatLng point) {
    	
    	// store the LatLng position of the the clicked position to pass into the form
    	Bundle location_bundle = new Bundle();
    	location_bundle.putParcelable("ucsd.cs110.placeit.LocationOnly", point);
    	Intent intent = new Intent(this, PlaceItsManager.class);
    	intent.putExtra("locationOnlyBundle", location_bundle);
    	intent.putExtra("ucsd.cs110.placeit.CheckSrouce", 2);
    
    	startActivity(intent);
    }

    ///////////////////// OnCameraChangeListener Methods //////////////////////
    @Override
    public void onCameraChange(final CameraPosition position) {
    	//Do nothing... for now
    }
	
    /////////////////////// LocationListener Methods //////////////////////////
	public void onLocationChanged(Location location) {
		// We need to figure out some way to check if this location corresponds to 
		// a location of one the PlaceIt's in the DB. 
	}

	////////////////////// ConnectionCallbacks Methods ////////////////////////
	@Override
	public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }
	
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	/////////////////// OnConnectionFailedListener Methods ////////////////////
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing... for now
	}



	/////////////////////// OnMarkerDragListener Methods //////////////////////
	public void onMarkerDrag(Marker marker) {
		// do nothing
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// get the PlaceIt that corresponds to the this marker
		int pl_ID = placeItMarkers.get(marker.getId()).intValue();
		PlaceIt tmp_Pl = db.getPlaceIt(pl_ID);
		
		// update the location of the PlaceIt
		LatLng location = marker.getPosition();
		String location_str = "";
		 
		try {
			location_str = (new GetAddressTask(this)).execute(location).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		tmp_Pl.setLocation_str(location_str);
		marker.setTitle("kljahsdflkjahs");
		db.updatePlaceIt(tmp_Pl);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}

    
}
