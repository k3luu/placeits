package ucsd.cse110.placeit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class GetPlaceitFromUrl extends AsyncTask<String, Void, JSONObject>{

	public AsyncResponse delegate=null;
	ArrayList<PlaceIt> placeItData= new ArrayList<PlaceIt>();
	@Override
	protected JSONObject doInBackground(String... url) {

		//ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		JSONObject myjson = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String placeItOnlineData = EntityUtils.toString(entity);
			
			try {
				myjson = new JSONObject(placeItOnlineData);
			} catch (JSONException e) {
				Log.d("ERROR", "Error in parsing JSON");
			}
		} catch (ClientProtocolException e) {
			Log.d("ERROR", "ClientProtocolException thrown while trying to Connect to GAE");	
		} catch (IOException e) {
			Log.d("ERROR", "IOException thrown while trying to Conncet to GAE");
		}
		return myjson;
	}
	
	protected void onPostExecute(JSONObject data) {
		delegate.processFinish(data);		
	}

	

}
