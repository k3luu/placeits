package ucsd.cse110.placeit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class Details{
	
	private Context context;
	private PlaceItDbHelper db;
	ProximityAlertManager paManager;
	String activeOrTriggered;
	
	public Details(Context context) {
		paManager = new ProximityAlertManager(context);
		this.context = context;
	}

	// displays the details of the PlaceIt
	public void display(int placeItId) {

		// retrieve the PlaceIt Id and recover the PlaceIt
		Intent detailsIntent = new Intent(context, Details.class);
		detailsIntent.putExtra(PlaceItUtil.PLACEIT_ID, placeItId);
		final PlaceIt placeIt = getPlaceItFromDb(placeItId);
		
		String title_field = "No PlaceIt Found";
    	String info = "Sorry about that";
		
		if(placeIt != null) {
			title_field = placeIt.getTitle();
			info = placeIt.toString();
			
			// determine what middle button should say
			activeOrTriggered = placeIt.getStatus();
			if (activeOrTriggered.equalsIgnoreCase(PlaceItUtil.ACTIVE)) {
				activeOrTriggered = "Modify";
			} else {
				activeOrTriggered = "Reactivate";
			}
		}
		
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		
		alert.setTitle(title_field);
		alert.setMessage(info);
		
		
		alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(context,"Action Cancelled", Toast.LENGTH_SHORT).show();
			}
		});
		
		alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PlaceItDbHelper db = new PlaceItDbHelper(context);
		        
				if(placeIt != null) {
					// remove proximity alert and any alarms set on the PlaceIt
					// then remove it from the database
			        paManager.removeProximityAlert(placeIt.getId());
			        placeIt.getSchedule().removeAlarm(context, placeIt.getId());
			        db.deletePlaceIt(placeIt);
		        
			        // alert the user that the PlaceIt was deleted
			        Toast.makeText(context, placeIt.getTitle() + " PlaceIt deleted", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(context, MainActivity.class);
					
					// save the last location of the users screen
					SaveLastLocation action = new SaveLastLocation(placeIt.getLocation());
					action.saveLastPlaceIt(db);
					
					db.close();
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(intent);
				}
				
			}
		});
	
		alert.setNeutralButton(activeOrTriggered, new DialogInterface.OnClickListener() {
	
			@Override
			public void onClick(DialogInterface dialog, int which) {
		    	
				if(placeIt != null) {
					
					if (placeIt.getStatus().equalsIgnoreCase(PlaceItUtil.TRIGGERED)) {
						placeIt.setStatus(PlaceItUtil.ACTIVE);
						db.updatePlaceIt(placeIt);
						db.close();
						Intent intent = new Intent(context, ListActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
					} else {
						
						// Go to Place-it manager with Intent. case from
						// list view edit. ID. case 3
						// store the Place-It's ID and pass to PlaceItManager to edit
						int passID = placeIt.getId();
				    	String passTitle = placeIt.getTitle();
				    	LatLng passPoint = placeIt.getLocation();
				    	String passDescription = placeIt.getDescription();
				    	
				    	Bundle location_bundle = new Bundle();
				    	location_bundle.putParcelable("ucsd.cs110.placeit.LocationOnly", passPoint);
				    	Intent intent = new Intent(context, PlaceItsManager.class);
				    	intent.putExtra("idIntent", passID);
				    	intent.putExtra("titleIntent", passTitle);
				    	intent.putExtra("locationOnlyBundle", location_bundle);
				    	intent.putExtra("descriptionIntent", passDescription);
				    	intent.putExtra("ucsd.cs110.placeit.CheckSrouce", 3);
				    	
				    	context.startActivity(intent);
					}
			    	
			    	// remove proximity alert and any alarms set on the PlaceIt
					// then remove it from the database
//			    	paManager.removeProximityAlert(placeIt.getId());
//			    	placeIt.getSchedule().removeAlarm(context, placeIt.getId());
//			    	db.deletePlaceIt(placeIt); //delete it after getting info because it will reactivate ?
					
				}
			}
		});
		    	
		alert.show();
	}
	
	private PlaceIt getPlaceItFromDb(int theID) {
    	PlaceIt placeIt;
		db = new PlaceItDbHelper(context);
		placeIt = db.getPlaceIt(theID);
		db.close();
    	return placeIt;
    }
	
}

