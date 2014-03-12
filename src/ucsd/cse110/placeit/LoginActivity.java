package ucsd.cse110.placeit;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity {
	private EditText login;
	private EditText password;
	private Activity activity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load layout
		setContentView(R.layout.login_page);
	}

	protected void onResume() {
		super.onResume();

		// Set login and password to user entry
		login = (EditText) findViewById(R.id.logEdit1);
		password = (EditText) findViewById(R.id.logEdit2);

		final Button reg = (Button) findViewById(R.id.register);
		final Button log = (Button) findViewById(R.id.login);

		log.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (login.length() <= 0 || password.length() <= 0) { // Check if
					// values
					// entered
					Toast.makeText(getApplicationContext(), "Incomplete form",
							Toast.LENGTH_SHORT).show();
				} else { // Check login from database
					// Get string value of entered text to pass to
					// OnlineDatabaseAddLogin()
					String sLogin = login.getText().toString();
					String sPass = password.getText().toString();

					// TODO: Query database, check if login exists, then load
					// account
					ArrayList<Login> list = new ArrayList<Login>();
					try {
						list = new OnlineDatabaseLoginValidation()
								.execute(
										"http://www.cse110group30login.appspot.com/login")
								.get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					boolean valid = false;

					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getUsername().equals(sLogin)
								&& list.get(i).getPassword().equals(sPass)) {
							valid = true;
							PlaceItUtil.USERNAME = sLogin;
							break;
						}
					}
					if (valid) {
						// Go back to the map view after successful login
						Intent mapIntent = new Intent(activity,
								MapActivity.class);
						startActivity(mapIntent);
					} else {
						Toast.makeText(getApplicationContext(),"Invalid username and/or password", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		reg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("register button clicked", "hahaha");
				// Load registration layout
				setContentView(R.layout.registration_page);

				final Button register = (Button) findViewById(R.id.regbutton);

				/*
				 * do { login = (EditText) findViewById(R.id.regEdit1); password
				 * = (EditText) findViewById(R.id.regEdit2); if (login.length()
				 * <= 0 || password.length() <= 0) { // Check if // values //
				 * entered Toast.makeText(getApplicationContext(),
				 * "Incomplete form", Toast.LENGTH_SHORT).show(); } } while
				 * (login.length() <= 0 || password.length() <= 0);
				 */

				// Check login from database
				// Get string value of entered text to pass to
				// OnlineDatabaseAddLogin()

				register.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						login = (EditText) findViewById(R.id.regEdit1);
						password = (EditText) findViewById(R.id.regEdit2);
						String sLogin = login.getText().toString();
						String sPass = password.getText().toString();
						if (login.length() <= 0 || password.length() <= 0) {
							Toast.makeText(getApplicationContext(),
									"Incomplete form", Toast.LENGTH_SHORT)
									.show();
						} else {
							sLogin = login.getText().toString();
							sPass = password.getText().toString();

							OnlineDatabaseAddLogin newLogin = new OnlineDatabaseAddLogin(
									activity, sLogin, sPass);
							newLogin.startAddingLogin();
							Toast.makeText(getApplicationContext(),
									"registed successfully", Toast.LENGTH_SHORT)
									.show();
							setContentView(R.layout.login_page);

						}

					}
				});
			}
		});
	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // If add
	 * button pressed if (item.getItemId() == R.id.login) { // Create and start
	 * intent // Intent listIntent = new Intent(this, ListActivity.class); //
	 * startActivity(listIntent); if (login.length() <= 0 || password.length()
	 * <= 0) { // Check if // values // entered
	 * Toast.makeText(getApplicationContext(), "Incomplete form",
	 * Toast.LENGTH_SHORT).show(); } else { // Check login from database // Get
	 * string value of entered text to pass to // OnlineDatabaseAddLogin()
	 * String sLogin = login.getText().toString(); String sPass =
	 * password.getText().toString();
	 * 
	 * // TODO: Query database, check if login exists, then load // account
	 * 
	 * // Go back to the map view after successful login Intent mapIntent = new
	 * Intent(this, MapActivity.class); startActivity(mapIntent); } return
	 * true;
	 * 
	 * // If register button pressed } else if (item.getItemId() ==
	 * R.id.register) { Log.i("register button clicked", "hahaha"); // Load
	 * registration layout setContentView(R.layout.registration_page); login =
	 * (EditText) findViewById(R.id.regEdit1); password = (EditText)
	 * findViewById(R.id.regEdit2);
	 * 
	 * // Create intent // Intent regIntent = new Intent(this,
	 * ListActivity.class); // startActivity(regIntent); if (login.length() <= 0
	 * || password.length() <= 0) { // Check if // values // entered
	 * Toast.makeText(getApplicationContext(), "Incomplete form",
	 * Toast.LENGTH_SHORT).show(); } else { // Check login from database // Get
	 * string value of entered text to pass to // OnlineDatabaseAddLogin()
	 * String sLogin = login.getText().toString(); String sPass =
	 * password.getText().toString();
	 * 
	 * OnlineDatabaseAddLogin newLogin = new OnlineDatabaseAddLogin( this,
	 * sLogin, sPass); newLogin.startAddingLogin();
	 * 
	 * // Go back to the map view after successful login Intent mapIntent = new
	 * Intent(this, MapActivity.class); startActivity(mapIntent); }
	 * 
	 * // Go back to the map view Intent mapIntent = new Intent(this,
	 * MapActivity.class); startActivity(mapIntent); return true; } else {
	 * return super.onOptionsItemSelected(item); } }
	 */

	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing
	}
}
