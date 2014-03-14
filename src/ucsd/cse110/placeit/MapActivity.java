package ucsd.cse110.placeit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/*
 * Displays the map 
 */
public class MapActivity extends FragmentActivity implements
		OnMapLongClickListener, OnInfoWindowClickListener {

	// /////////////////////////// Private Variables ///////////////////////////

	private GoogleMap mMap; // the google map
	private HashMap<String, Integer> placeItMarkers = new HashMap<String, Integer>(); // HashMap
																						// between
																						// our
																						// markers
																						// and
																						// PLaceIts
	private List<PlaceIt> activePlaceItList; // a list of all the active placeIt
	private List<PlaceIt> systemPlaceItList;

	// get an instance of our database to add
	PlaceItDbHelper db = new PlaceItDbHelper(this);
	ProximityAlertManager paManager;

	// /////////////////////////// Activity States /////////////////////////////

	// Defines what to do when this activity initially opens
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("Debugging","1");
		// Then go to main activity
		setContentView(R.layout.activity_main);

		// Hide Action bar Icon
		getActionBar().setDisplayShowHomeEnabled(false);
		Log.i("Debugging","2");
		
		// set up map and searchbar
		setUpMapIfNeeded();
		setUpSearchbar();
		paManager = new ProximityAlertManager(this);
		Log.i("Debugging","3");

		// update local database
		//OnlineLocalDatabaseSynchronization olds = new OnlineLocalDatabaseSynchronization(this);
		Log.i("Debugging","4");

		// add markers to the map and prximity sensors
		generatePlaceIts();
		Log.i("Debugging","5");

		// Get screen width to set zoom level to 0.5 mile radius
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		Log.i("Debugging","6");
		// PlaceIt placeTmp;
		// initTheFirstDatabase();
		// placeTmp = db.getAllPlaceIts("HACKER").get(0);

		
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				32.8804, -117.242), calculateZoomLevel(width)));
		
		//track users location to check for categories
		this.startService(new Intent(this, LocationTrackerService.class));
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Menu buttons click to associated activity

		if (item.getItemId() == R.id.map_view_btn) {
			Intent intent1 = new Intent(this, MapActivity.class);
			startActivity(intent1);
			return true;
		}
		else if (item.getItemId() == R.id.list_view_btn) {
			Intent intent1 = new Intent(this, ListActivity.class);
			startActivity(intent1);
			return true;
		} else if (item.getItemId() == R.id.add_category_btn) {
			Intent intent1 = new Intent(this, PlaceItsCategoryForm.class);
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
		generatePlaceIts();

	}

	protected void onDestroy() {
		db.close();
	}

	// /////////////////////////// Setup Methods ///////////////////////////////

	// creates an instance of GoogleMap and sets the appropriate settings
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				mMap.setOnMapLongClickListener(this);
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
				LatLng location = new LatLng(address.getLatitude(), address
						.getLongitude());
				onMapLongClick(location);
			}
		});
	}

	// /////////////////////////// Other Methods ///////////////////////////////

	// populates the map with all the PlaceIt's stored in the database
	public void generatePlaceIts() {

		// remove any previous placeIts
		mMap.clear();

		// loop through all active PlaceIt's and populate map with them
		activePlaceItList = db.getAllPlaceItsByUsernameAndStatus(PlaceItUtil.USERNAME, PlaceItUtil.ACTIVE);

		PlaceIt placeIt;
		for (int i = 0; i < activePlaceItList.size(); i++) {
			placeIt = activePlaceItList.get(i);

			// create markers
			Marker marker = mMap.addMarker(new MarkerOptions()
					.position(placeIt.getLocation()).title(placeIt.getTitle())
					.draggable(false));
			marker.setIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.placeit));
			placeItMarkers.put(marker.getId(), (int) placeIt.getId());

			// WEIJIE paManager.addProximityAlert(placeIt);
		}
	}

	// /////////////////// OnMapLongClickListener Methods //////////////////////

	// Create a PlaceIt at the position clicked
	public void onMapLongClick(LatLng point) {

		// store the LatLng position of the the clicked position to pass into
		// the form
		Bundle location_bundle = new Bundle();
		location_bundle.putParcelable(PlaceItUtil.LOC_ONLY, point);
		Intent intent = new Intent(this, PlaceItsManager.class);
		intent.putExtra(PlaceItUtil.LOC_BUNDLE, location_bundle);
		intent.putExtra(PlaceItUtil.CHECK_SOURCE, 1);

		startActivity(intent);
	}

	// //////////////////// onInfoWindowListener Methods ///////////////////////

	// opens the detail page of a given placeIt
	public void onInfoWindowClick(Marker marker) {

		Details placeItDetails = new Details(this);
		placeItDetails.display(placeItMarkers.get(marker.getId()));
	}

	// store the first location seen by the user
	private void initTheFirstDatabase() {
		systemPlaceItList = db.getAllPlaceIts("HACKER");

		if (systemPlaceItList.size() == 0) {
			Scheduler schedule = new Scheduler("-", "", "", -1);
			LatLng loc = new LatLng(32.8804, -117.242);
			PlaceIt place = new PlaceIt();
			place.setLocation(loc);
			place.setStatus("HACKER");
			place.setTitle("Hidden");
			place.setDescription("YOU SHOULD NEVER SEE THIS OR MODIFY THIS.!");
			place.setLocation_str("UCSD");
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
		return zoomLevel;
	}

}
