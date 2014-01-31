package ucsd.cse110.placeit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;

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

public class MainActivity extends FragmentActivity implements OnMarkerClickListener, OnMapLongClickListener, OnCameraChangeListener { 
	
	private GoogleMap mMap;
//    private TextView mTapTextView;
//    private TextView mCameraTextView;

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        
//        mTapTextView = (TextView) findViewById(R.id.tap_text);
//        mCameraTextView = (TextView) findViewById(R.id.camera_text);

        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onMapLongClick(LatLng point) {
    	mMap.addMarker(new MarkerOptions()
	        .position(point)
	        .draggable(true))
	        .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
        //mTapTextView.setText("long pressed, point=" + point);
    }

    @Override
    public void onCameraChange(final CameraPosition position) {
        //mCameraTextView.setText(position.toString());
    }

	@Override
	public boolean onMarkerClick(Marker marker) {
		
		Intent intent = new Intent(this, PlaceItFormActivity.class); 
	    //intent.putExtra(marker.getPosition(), position );
	    startActivity(intent);
		
		
		return false;
	}
    
}
