package ucsd.cse110.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

public class PlaceItFormActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Get the intent sent from the map/add button
		Intent intent = getIntent();
	    Double lat = intent.getDoubleExtra(MainActivity.LAT, 0.0);
	    Double lng = intent.getDoubleExtra(MainActivity.LNG, 0.0);
	    
	    // for proof of concept
//	    EditText editView = (EditText) findViewById(R.id.location);
//	    editView.setText(lat.toString() + lng.toString());

		setContentView(R.layout.activity_place_it_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_it_form, menu);
		return true;
	}

}
