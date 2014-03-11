package ucsd.cse110.placeit;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class RemovePlaceitFromUrl extends AsyncTask<String, Void, JSONObject>{

	private String deleteObjName;
	
	RemovePlaceitFromUrl(String name) {
		deleteObjName = name;
	}
	
	ArrayList<PlaceIt> placeItData= new ArrayList<PlaceIt>();
	@Override
	protected JSONObject doInBackground(String... url) {

		//ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		JSONObject myjson = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost  request = new HttpPost (url[0]);
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
		
		Log.i("trying to delete", ""+myjson);
		return myjson;
	}
	
	protected void onPostExecute(JSONObject data) {
		try {
			
			JSONArray array = data.getJSONArray("data");			
			JSONObject obj;
			for (int i = 0; i < array.length(); i++) {
				obj = array.getJSONObject(i);
				
				if (obj.get("name").toString().equals(deleteObjName)) {
					obj.remove(deleteObjName);
				}
			}

		} catch (JSONException e) {
			Log.d("ERROR", "Error in parsing JSON");
		}
	}

	

}
