package ucsd.cse110.placeit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PlaceItsCategoryForm extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_its_category_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_its_category_form, menu);
		return true;
	}

}
