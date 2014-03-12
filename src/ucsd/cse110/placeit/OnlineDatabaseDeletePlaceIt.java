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

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class OnlineDatabaseDeletePlaceIt {
	private Context context;
	private String name;
	private ProgressDialog dialog;
	
	OnlineDatabaseDeletePlaceIt(Context myContext, String placeItName) {
		name = placeItName;
		context = myContext;
	}
	
	public void startRemovingPlaceIt() {
		
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
			      nameValuePairs.add(new BasicNameValuePair("id",
			    		 name));
			      nameValuePairs.add(new BasicNameValuePair("parentid",
				    		 "Data"));
			      nameValuePairs.add(new BasicNameValuePair("action",
				          "DELETE"));
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
