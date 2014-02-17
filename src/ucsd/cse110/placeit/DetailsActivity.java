package ucsd.cse110.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class DetailsActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get the passed PlaceIt ID
		Intent intent = getIntent();
	    int placeItId = intent.getIntExtra(PlaceItUtil.PLACEIT_ID, -1);
	    
	    // Display the contents of the PlaceIt
		Details placeItDetails = new Details(this);
		placeItDetails.display(placeItId);
		setContentView(R.layout.activity_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
