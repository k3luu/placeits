package ucsd.cse110.placeit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class OnlineDatabaseAddPlaceIt {
	
	private Context context;
	private PlaceIt placeIt;
	private ProgressDialog dialog;
	
	OnlineDatabaseAddPlaceIt(Context myContext, PlaceIt pl) {
		context = myContext;
		placeIt = pl;
	}
	
	public void startAddingPlaceIt() {
		final PlaceIt place = placeIt;
		
		if (context != null) {
			dialog = ProgressDialog.show(context,
					"Posting Data...", "Please wait...", false);
		}
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(PlaceItUtil.ONLINEDATABASE);

			    try {
			      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			      nameValuePairs.add(new BasicNameValuePair("name",
			    		  place.getTitle().toString()));
			      nameValuePairs.add(new BasicNameValuePair("placeItStatus",
			    		  place.getStatus().toString()));
			      nameValuePairs.add(new BasicNameValuePair("placeItDescription",
			    		  place.getDescription().toString()));
			      nameValuePairs.add(new BasicNameValuePair("placeItLatitude",
			    		  ""+place.getLocation().latitude));
			      nameValuePairs.add(new BasicNameValuePair("placeItLongitude",
			    		  ""+place.getLocation().longitude));
			      nameValuePairs.add(new BasicNameValuePair("placeItLocationString",
			    		  place.getLocation_str().toString()));
			      nameValuePairs.add(new BasicNameValuePair("placeItScheduledOption",
			    		  place.getSchedule().getScheduled_option().toString()));
			      
			      if (place.getSchedule().getScheduled_dow() == null) {
			    	  nameValuePairs.add(new BasicNameValuePair("placeItScheduledDow",
				    		  ""));
			      }
			      else {
			    	  nameValuePairs.add(new BasicNameValuePair("placeItScheduledDow",
				    		  place.getSchedule().getScheduled_dow().toString()));
			      }
			      
			      if (place.getSchedule().getScheduled_week() == null) {
			    	  nameValuePairs.add(new BasicNameValuePair("placeItScheduledWeek",
			    			  ""));
			      }
			      else {
			    	  nameValuePairs.add(new BasicNameValuePair("placeItScheduledWeek",
				    		  place.getSchedule().getScheduled_week().toString()));
			      }
			      nameValuePairs.add(new BasicNameValuePair("placeItScheduledMinutes",
			    		  ""+place.getSchedule().getScheduled_minutes()));
			      nameValuePairs.add(new BasicNameValuePair("placeItCategory1",
			    		  ""+place.getCategories()[0]));
			      nameValuePairs.add(new BasicNameValuePair("placeItCategory2",
			    		  ""+place.getCategories()[1]));
			      nameValuePairs.add(new BasicNameValuePair("placeItCategory3",
			    		  ""+place.getCategories()[2]));
			      nameValuePairs.add(new BasicNameValuePair("placeItUsername",
			    		  ""+place.getUsername()));
			      
			      
			      
			      nameValuePairs.add(new BasicNameValuePair("action",
				          "put"));
			      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 
			      HttpResponse response = client.execute(post);
			      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			      String line = "";
			      while ((line = rd.readLine()) != null) {
			        Log.d("HAHA", line);
			      }

			    } catch (IOException e) {
			    	Log.d("NONO", "IOException while trying to conect to GAE");
			    }
			    if (context != null) {
			    	dialog.dismiss();
			    }
			}
		};

		t.start();
		if (context != null) {
			dialog.show();
		}
	}
}
