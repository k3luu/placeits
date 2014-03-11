package ucsd.cse110.placeit;

import java.io.IOException;
import java.util.ArrayList;

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

import android.os.AsyncTask;
import android.util.Log;

public class OnlineDatabaseLoginValidation extends AsyncTask<String, Void, ArrayList<Login>> {

	@Override
	protected ArrayList<Login> doInBackground(String... url) {
		JSONObject myjson = null;
		ArrayList<Login> list= new ArrayList<Login>(); 
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			String onlineLoginData = EntityUtils.toString(entity);
			
			try {
				myjson = new JSONObject(onlineLoginData);
				JSONArray array = myjson.getJSONArray("data");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Login temp = new Login(obj.get("name").toString(), obj.get("pWord").toString());
					list.add(temp);
				}
				
			} catch (JSONException e) {
				Log.d("Login data ERROR", "Error in parsing JSON");
			}
		} catch (ClientProtocolException e) {
			
		}catch (IOException e) {

			Log.d("OMG", "IOException while trying to connect to GAE");
		}
		return list;
	}
	protected void onPostExecute(JSONObject data) {
		
	}
}
