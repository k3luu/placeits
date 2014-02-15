package ucsd.cse110.placeit;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

/*
 *  this NotificationActivity class is used for reactive a event when the user press the repost button, 
 *  so, placeit will notice the user (next time)
 */

public class NotificationActivity extends Activity {
	
	private PlaceItDbHelper db = new PlaceItDbHelper(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		Intent detailDialog = getIntent();
		//boolean repostKey = detailDialog.getBooleanExtra(PlaceItUtil.REPOST, false);
		int placeItId = detailDialog.getIntExtra(PlaceItUtil.PLACEIT_ID, -1);
		
		db = new PlaceItDbHelper(this);
		PlaceIt placeIt = db.getPlaceIt(placeItId);
		Log.i("HEY", "GONNA TRY TO REPOSTED");
		if (placeIt != null) {
			Log.i("HEY", "JUST REPOSTED");
			placeIt.setStatus(PlaceItUtil.ACTIVE);
			db.updatePlaceIt(placeIt);
			Log.i("HEY", placeIt.getStatus());
			
		}
		db.close();
		//finish the NotificationActivity
		finish();
		// go back to home screen after the user press repost button.
		startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
	}

}
