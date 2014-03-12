package ucsd.cse110.placeit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
	    
	    // if repost option selected
	    if (repost) {

			db = new PlaceItDbHelper(this);
			PlaceIt placeIt = db.getPlaceIt(placeItId);
			if (placeIt != null) {
				placeIt.setStatus(PlaceItUtil.ACTIVE);
				// 1. remove online -- 2. add the most  -- 3. Synchronization
				OnlineDatabaseDeletePlaceIt odlp = new OnlineDatabaseDeletePlaceIt(this, placeIt.getTitle());
				odlp.startRemovingPlaceIt();
				OnlineDatabaseAddPlaceIt odba = new OnlineDatabaseAddPlaceIt (this, placeIt);
				odba.startAddingPlaceIt();
				OnlineLocalDatabaseSynchronization olds = new OnlineLocalDatabaseSynchronization(this);			
			}
			
			db.close();
			
			// reset the ProximityAlerts
			//ProximityAlertManager paManager = new ProximityAlertManager(this);
			//paManager.addProximityAlert(placeIt);
			
			//finish the NotificationActivity
			finish();
			
			// go back to home screen after the user press repost button.
			startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
	    }
	    else {
			// Display the contents of the PlaceIt
			Details placeItDetails = new Details(this);
			placeItDetails.display(placeItId);
	    }
	}

}
