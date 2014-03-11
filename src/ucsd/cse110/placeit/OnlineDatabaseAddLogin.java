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
import android.util.Log;

public class OnlineDatabaseAddLogin {

	private Activity activity;
	private String login, password;

	OnlineDatabaseAddLogin(Activity myActivity, String login, String password) {
		activity = myActivity;
		this.login = login;
		this.password = password;
	}

	public void startAddingLogin() {
		final String login = this.login;
		final String password = this.password;
		final ProgressDialog dialog = ProgressDialog.show(activity,
				"Posting Data...", "Please wait...", false);
		Thread t = new Thread() {

			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(
						"http://cse110group30login.appspot.com/login");

				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("name", login));
					nameValuePairs.add(new BasicNameValuePair("pWord", password));
					nameValuePairs.add(new BasicNameValuePair("action",
					          "put"));
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = client.execute(post);
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					String line = "";
					while ((line = rd.readLine()) != null) {
						Log.d("HAHA", line);
					}

				} catch (IOException e) {
					Log.d("NONO", "IOException while trying to conect to GAE");
				}
				dialog.dismiss();
			}
		};

		t.start();
		dialog.show();
	}

}
