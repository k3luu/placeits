package ucsd.cse110.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/*
 *  this NotificationActivity class is used for reactive a event when the user press the repost button, 
 *  so, placeit will notice the user (next time)
 */

public class NotificationActivity extends Activity {
	
	private PlaceItDbHelper db = new PlaceItDbHelper(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get the passed PlaceIt ID
		Intent detailDialog = getIntent();
	    int placeItId = detailDialog.getIntExtra(PlaceItUtil.PLACEIT_ID, -1);
	    boolean repost = detailDialog.getBooleanExtra(PlaceItUtil.REPOST, false);
	    Log.i("booloean", String.valueOf(repost));
	    
	    if (repost) {
	    	Toast.makeText(this,"Reposting", Toast.LENGTH_SHORT).show();
			db = new PlaceItDbHelper(this);
			PlaceIt placeIt = db.getPlaceIt(placeItId);
			if (placeIt != null) {
				placeIt.setStatus(PlaceItUtil.ACTIVE);
				db.updatePlaceIt(placeIt);				
			}
			db.close();
			
			//finish the NotificationActivity
			finish();
			
			// go back to home screen after the user press repost button.
			startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
	    }
	    else {
	    	Toast.makeText(this,"Displaying", Toast.LENGTH_SHORT).show();
	    	// Display the contents of the PlaceIt
			Details placeItDetails = new Details(this);
			placeItDetails.display(placeItId);
			
	    }
	}

}
