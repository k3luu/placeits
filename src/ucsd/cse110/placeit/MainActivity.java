package ucsd.cse110.placeit;

import java.util.HashMap;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

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
OnMapLongClickListener, OnCameraChangeListener, OnMarkerDragListener { 
	
	/////////////////////////////// Constants //////////////////////////////////
	
	public final static String LAT = "ucsd.cse110.placeit.LAT";
	public final static String LNG = "ucsd.cse110.placeit.LNG";
	public final static String LATLNG = "ucsd.cse110.placeit.LATLNG";
	public final static String TRIGGERED = "Triggered";
	public final static String ACTIVE = "Active";
	public static final String PLACEIT_ID = "ucsd.cse110.placeit.PLACEIT_ID";
	
	private final static float ALERT_RADIUS = 804.672f;
	private final static int ALERT_EXPIRATION = -1;
	private final static String ALERT_INTENT = "ucsd.cse110.placeit.ALERT";
	
	
	///////////////////////////// Private Variables ///////////////////////////
	
	private GoogleMap mMap; 						// the google map 
	private LocationManager mLocationManager;		// manage users location
	private HashMap<String, Integer> placeItMarkers 
			= new HashMap<String, Integer>(); 		// HashMap between our markers and PLaceIts
	private List<PlaceIt> activePlaceItList;		// a list of all the active placeIt
	
	// get an instance of our database to add
	PlaceItDbHelper db = new PlaceItDbHelper(this);
	
	
    ///////////////////////////// Activity States /////////////////////////////
    
    // Defines what to do when this activity initially opens
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Hide Action bar Icon
        getActionBar().setDisplayShowHomeEnabled(false);
        
        // set up map and searchbar
        setUpMapIfNeeded();
        setUpSearchbar();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        // add markers to the map and prximity sensors
        generatePlaceIts();
        
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
    		//Intent intent2 = new Intent(this, PlaceItsManager.class);
    		//intent2.putExtra("ucsd.cs110.placeit.CheckSrouce", 1);
        	//startActivity(intent2);
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

    }
    
    ///////////////////////////// Setup Methods ///////////////////////////////
    
    // creates an instance of GoogleMap and sets the appropriate settings
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
    
    // sets up the search bar
    private void setUpSearchbar() {
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
    

    ///////////////////////////// Other Methods ///////////////////////////////
    
    // create proximity alerts for each PlaceIt
    private void addProximityAlert(PlaceIt placeIt) {
    	int placeIt_Id = placeIt.getId();
    	
        Intent intent = new Intent(ALERT_INTENT);
        intent.putExtra(PLACEIT_ID, placeIt_Id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, placeIt_Id, intent, 0);
        mLocationManager.addProximityAlert(
        		placeIt.getLocation().latitude, 
        		placeIt.getLocation().longitude, 
                ALERT_RADIUS, 
                ALERT_EXPIRATION, 
                pendingIntent 
        );

        IntentFilter filter = new IntentFilter(ALERT_INTENT);
        registerReceiver(new PlaceItIntentReceiever(), filter);
        Toast.makeText(getApplicationContext(),"PlaceIt Added",Toast.LENGTH_SHORT).show();
    }
    
    
    // populates the map with all the PlaceIt's stored in the database
    public void generatePlaceIts() {
    	
    	PlaceIt placeIt;
		
    	// loop through all active PlaceIt's and populate map with them
    	activePlaceItList = db.getAllPlaceIts(ACTIVE);
    	
		for (int i = 0; i < activePlaceItList.size(); i++) {
			placeIt = activePlaceItList.get(i);
			
			// create markers
			Marker marker = mMap.addMarker(new MarkerOptions()
									        .position(placeIt.getLocation())
									        .title(placeIt.getTitle())
									        .snippet(placeIt.getLocation_str())
									        .draggable(true)
									        );
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
			placeItMarkers.put(marker.getId(), placeIt.getId());
			
			// create proximity alerts
			// NOTE: Maybe should not be calling it from here
			addProximityAlert(placeIt);
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


	/////////////////////// OnMarkerDragListener Methods //////////////////////
	public void onMarkerDrag(Marker marker) {
		// do nothing
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
//		// get the PlaceIt that corresponds to the this marker
//		int pl_ID = placeItMarkers.get(marker.getId()).intValue();
//		PlaceIt tmp_Pl = db.getPlaceIt(pl_ID);
//		
//		// update the location of the PlaceIt
//		LatLng location = marker.getPosition();
//		String location_str = "";
//		 
//		try {
//			location_str = (new GetAddressTask(this)).execute(location).get();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}
//		
//		tmp_Pl.setLocation_str(location_str);
//		marker.setTitle("kljahsdflkjahs");
//		db.updatePlaceIt(tmp_Pl);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}

    
}
