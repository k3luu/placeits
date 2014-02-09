package ucsd.cse110.placeit;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements 
OnMapLongClickListener, OnCameraChangeListener, OnInfoWindowClickListener { 
	
	/////////////////////////////// Constants //////////////////////////////////
	
	public final static String LAT = "ucsd.cse110.placeit.LAT";
	public final static String LNG = "ucsd.cse110.placeit.LNG";
	public final static String LATLNG = "ucsd.cse110.placeit.LATLNG";
	public final static String TRIGGERED = "Triggered";
	public final static String ACTIVE = "Active";
	public static final String PLACEIT_ID = "ucsd.cse110.placeit.PLACEIT_ID";
	
	private final static float ALERT_RADIUS = 804.672f; //1.5 mile
	private final static int ALERT_EXPIRATION = -1;
	public final static String PROX_ALERT_INTENT = "ucsd.cse110.placeit.ALERT";
	
	private static boolean alerts_set = false;
	
	
	///////////////////////////// Private Variables ///////////////////////////
	
	private GoogleMap mMap; 						// the google map 
	private LocationManager mLocationManager;		// manage users location
	private HashMap<String, Integer> placeItMarkers 
			= new HashMap<String, Integer>(); 		// HashMap between our markers and PLaceIts
	private List<PlaceIt> activePlaceItList;		// a list of all the active placeIt
	private List<PlaceIt> systemPlaceItList;
	
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
        
        if (!alerts_set) {
	        // add markers to the map and prximity sensors
	        generatePlaceIts();
    	}
        PlaceIt placeTmp;
        initTheFirstDatabase();
        placeTmp = db.getAllPlaceIts("HACKER").get(0);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeTmp.getLocation(), 12),2000,null);
        
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
                mMap.setOnInfoWindowClickListener(this);
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
    
    // populates the map with all the PlaceIt's stored in the database
    public void generatePlaceIts() {
    	
    	PlaceIt placeIt;
		
    	// loop through all active PlaceIt's and populate map with them
    	activePlaceItList = db.getAllPlaceIts(ACTIVE);
    	
    	
		for (int i = 0; i < activePlaceItList.size(); i++) {
			placeIt = activePlaceItList.get(i);
			
			// create markers
			Log.i("MARKER MADE", placeIt.getTitle() + String.valueOf(placeIt.getId()));
			Marker marker = mMap.addMarker(new MarkerOptions()
									        .position(placeIt.getLocation())
									        .title(placeIt.getTitle())
									        .draggable(false)
									        );
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.placeit));
			placeItMarkers.put(marker.getId(), placeIt.getId());
			
			// create proximity alerts
			// NOTE: Maybe should not be calling it from here
			addProximityAlert(placeIt);
		}
		db.close();
    }
    
    // create proximity alerts for each PlaceIt
    private void addProximityAlert(PlaceIt placeIt) {
    	int placeIt_Id = placeIt.getId();
    	
        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra(PLACEIT_ID, placeIt_Id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, placeIt_Id, intent, 0);
        mLocationManager.addProximityAlert(
        		placeIt.getLocation().latitude, 
        		placeIt.getLocation().longitude, 
                ALERT_RADIUS, 
                ALERT_EXPIRATION, 
                pendingIntent 
        );

        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(new PlaceItIntentReceiever(), filter);
//        Toast.makeText(getApplicationContext(),"PlaceIt Proximity Alert Added"+ placeIt_Id,Toast.LENGTH_SHORT).show();
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
    
    ////////////////////// onInfoWindowListener Methods ///////////////////////
    
    // opens the detail page of a given placeIt
    public void onInfoWindowClick(Marker marker) {
    	
    	Intent detailsIntent = new Intent(this, DetailsActivity.class);
    	detailsIntent.putExtra(PLACEIT_ID, placeItMarkers.get(marker.getId()));
    	//startActivity(detailsIntent);
    	
    	final PlaceIt placeIt = getPlaceItObjectFromMarker(placeItMarkers.get(marker.getId()));
    	String title_field;
    	String description_field;
    	String location_field;
    	String schedule_field;
    	
		title_field = placeIt.getTitle();
		description_field = placeIt.getDescription();
		location_field = placeIt.getLocation_str();
		schedule_field = placeIt.getScheduled_date();
		
		
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	
    	String info = "Description:  "
    			+ description_field+"\n\n"
    			+ "Address:  "
    			+ location_field+"\n\n"
    			+ "Scheduled Day:  "
    			+ schedule_field+"\n";
    	alert.setTitle(title_field);
    	alert.setMessage(info);
    	
    	alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplication(),"Action Cancelled", Toast.LENGTH_SHORT).show();
			}
		});
    	
    	alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PlaceItDbHelper db = new PlaceItDbHelper(getApplication());
		        db.deletePlaceIt(placeIt);
		        removeProximityAlert(placeIt.getId());
		        Toast.makeText(getApplication(),"Place-Its Deleted", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplication(), MainActivity.class);
				
				SaveLastLocation action = new SaveLastLocation(placeIt.getLocation());
				action.lastSavedPlaceIt(db);
				
				db.close();
	        	startActivity(intent);
			}
		});

    	alert.setNeutralButton("Modify", new DialogInterface.OnClickListener() {
	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Bundle location_bundle = new Bundle();
		    	
		    	// TODO SCHEDULE
		    	int passID = placeIt.getId();
		    	String passTitle = placeIt.getTitle();
		    	LatLng passPoint = placeIt.getLocation();
		    	String passDescription = placeIt.getDescription();
		    	
		    	location_bundle.putParcelable("ucsd.cs110.placeit.LocationOnly", passPoint);
		    	Intent intent = new Intent(getApplication(), PlaceItsManager.class);
		    	intent.putExtra("idIntent", passID);
		    	intent.putExtra("titleIntent", passTitle);
		    	intent.putExtra("locationOnlyBundle", location_bundle);
		    	intent.putExtra("descriptionIntent", passDescription);
		    	intent.putExtra("ucsd.cs110.placeit.CheckSrouce", 3);
		    	
		    	db.deletePlaceIt(placeIt); //delete it after getting info because it will reactivate ?
		    	removeProximityAlert(placeIt.getId());
		    	startActivity(intent);
				
				Toast.makeText(getApplication(),"Modifying Plact-Its", Toast.LENGTH_SHORT).show();
			}
		});
		    	
    	alert.show();
	}
    
    public PlaceIt getPlaceItObjectFromMarker(int theID) {
    	PlaceIt place;
		db = new PlaceItDbHelper(this);
		place = db.getPlaceIt(theID);
    	return place;
    }
    
    private void removeProximityAlert(int placeIt_id) {

        String context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(context);

        Intent intent = new Intent(MainActivity.PROX_ALERT_INTENT);
        PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), placeIt_id , intent, 0);
        locationManager.removeProximityAlert(operation);
    }
    
    /* Follow SRP
    private void lastSavedPlaceIt(LatLng loc)
    {
    	systemPlaceItList = db.getAllPlaceIts("HACKER");
    	PlaceIt place = systemPlaceItList.get(0);
    	place.setLocation(loc);
    	db.updatePlaceIt(place);
    	db.close();
    	Log.i("loc is", loc.toString());
    }
    */
    
    private void initTheFirstDatabase()
    {
    	systemPlaceItList = db.getAllPlaceIts("HACKER");
    	
    	if (systemPlaceItList.size() == 0)
    	{
	    	LatLng loc = new LatLng(32.8804, -117.242);
	    	PlaceIt place = new PlaceIt();
	    	place.setLocation(loc);
	    	place.setStatus("HACKER");
	    	place.setTitle("Hidden");
	    	place.setDescription("YOU SHOULD NEVER SEE THIS OR MODIFY THIS.!");
	    	PlaceItDbHelper database = new PlaceItDbHelper(this);
			database.addPlaceIt(place);
			database.close();
    	}    	
    }
}
