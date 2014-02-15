package ucsd.cse110.placeit;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
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
	ProximityAlertManager paManager;
	
	
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
        paManager = new ProximityAlertManager(this);
        
	    // add markers to the map and prximity sensors
        generatePlaceIts();
        
        // Get screen width to set zoom level to 0.5 mile radius


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        // Why do we need to do this?
        // Init the map and take the first ID in DATABASE to store last change of location
        // So we are able to view that on map instead of looking at the random place.
        // Answered by weijie
        PlaceIt placeTmp;
        initTheFirstDatabase();
        placeTmp = db.getAllPlaceIts("HACKER").get(0);
        
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeTmp.getLocation(), calculateZoomLevel(width)));
//        startService(new Intent(this, PlaceItService.class));
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
    	activePlaceItList = db.getAllPlaceIts(PlaceItUtil.ACTIVE);
    	
    	
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
			placeItMarkers.put(marker.getId(), (int) placeIt.getId());
			
			// create proximity alerts
			// NOTE: Maybe should not be calling it from here
			addProximityAlert(placeIt);
		}
		db.close();
    }
    
    // create proximity alerts for each PlaceIt
    private void addProximityAlert(PlaceIt placeIt) {
    	int placeIt_Id = placeIt.getId();
    	
        Intent intent = new Intent(PlaceItUtil.PROX_ALERT_INTENT);
        intent.putExtra(PlaceItUtil.PLACEIT_ID, placeIt_Id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) placeIt_Id, intent, 0);
        mLocationManager.addProximityAlert(
        		placeIt.getLocation().latitude, 
        		placeIt.getLocation().longitude, 
        		PlaceItUtil.ALERT_RADIUS, 
        		PlaceItUtil.ALERT_EXPIRATION, 
                pendingIntent 
        );

        IntentFilter filter = new IntentFilter(PlaceItUtil.PROX_ALERT_INTENT);
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
    	intent.putExtra("ucsd.cs110.placeit.CheckSrouce", 1);
    
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
    	
    	//Intent detailsIntent = new Intent(this, DetailsActivity.class);
    	//detailsIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeItMarkers.get(marker.getId()));
    	//startActivity(detailsIntent);
    	
    	final PlaceIt placeIt = getPlaceItObjectFromMarker(placeItMarkers.get(marker.getId()));
    	
    	String title_field;
    	String description_field;
    	String location_field;
    	String schedule_field;
    	
		title_field = placeIt.getTitle();
		description_field = placeIt.getDescription();
		location_field = placeIt.getLocation_str();
		schedule_field = placeIt.getSchedule().toString();
		
		
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
				
				// remove proximity alert and any alarms set on the PlaceIt
				// then remove it from the database
		        paManager.removeProximityAlert(placeIt.getId());
		        placeIt.getSchedule().removeAlarm(getApplication(), placeIt.getId());
		        db.deletePlaceIt(placeIt);
		        
		        Toast.makeText(getApplication(),"Place-Its Deleted", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplication(), MainActivity.class);
				
				SaveLastLocation action = new SaveLastLocation(placeIt.getLocation());
				action.saveLastPlaceIt(db);
				
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
		    	intent.putExtra("ucsd.cs110.placeit.CheckSrouce", 2);
		    	
		    	// remove proximity alert and any alarms set on the PlaceIt
				// then remove it from the database
		    	//db.deletePlaceIt(placeIt); //delete it after getting info because it will reactivate ?
		    	
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

    // Why do we need to do this?
    // Can't we just pass the LatLng position through the intent?
    
    // Answer, we need this for sure.
    // Because I want to make sure that the first item of the data is reserved
    // It is useful because it takes care of first time running the application
    private void initTheFirstDatabase()
    {
    	systemPlaceItList = db.getAllPlaceIts("HACKER");
    	
    	if (systemPlaceItList.size() == 0)
    	{
    		Scheduler schedule = new Scheduler("","","",-1);
	    	LatLng loc = new LatLng(32.8804, -117.242);
	    	PlaceIt place = new PlaceIt();
	    	place.setLocation(loc);
	    	place.setStatus("HACKER");
	    	place.setTitle("Hidden");
	    	place.setDescription("YOU SHOULD NEVER SEE THIS OR MODIFY THIS.!");
	    	place.setSchedule(schedule);
	    	PlaceItDbHelper database = new PlaceItDbHelper(this);
			database.addPlaceIt(place);
			database.close();
    	}
    }
    
    private int calculateZoomLevel(int screenWidth) {
    	double equatorLength = 40075004; // in meters
    	double widthInPixels = screenWidth;
    	double metersPerPixel = equatorLength / 256;
    	int zoomLevel = 1;
    	while ((metersPerPixel * widthInPixels) > 1609.34) { // 1609.34 m = 1 mi
    		metersPerPixel /= 2;
    		++zoomLevel;
    	}
    	// Log.i("ADNAN", "zoom level = "+zoomLevel);
    	return zoomLevel;
    }

}
