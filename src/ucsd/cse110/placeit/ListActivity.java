package ucsd.cse110.placeit;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
//import android.support.v4.app.NavUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;

public class ListActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private int currentTab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Hide Action bar Icon
		actionBar.setDisplayShowHomeEnabled(false);

		// Hide Action bar Title
		actionBar.setDisplayShowTitleEnabled(false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Menu buttons click to associated activity
		if (item.getItemId() == R.id.map_view_btn) {
			Intent intent1 = new Intent(this, MainActivity.class);
			startActivity(intent1);
			return true;
		} else if (item.getItemId() == R.id.list_view_btn) {
			Intent intent2 = new Intent(this, ListActivity.class);
			startActivity(intent2);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3-1 total pages.
			return 3 - 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
				/*
				 * case 2: return
				 * getString(R.string.title_section3).toUpperCase(l);
				 */
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment implements
			OnItemClickListener/* , OnItemLongClickListener */{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public final static String TRIGGERED = "Triggered";
		public final static String ACTIVE = "Active";
		private ArrayList<PlaceIt> activePlaceItList;
		private PlaceItDbHelper db;

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);

			db = new PlaceItDbHelper(getActivity());

			if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) { // Here is the
																	// Pending
																	// List View
																	// fragment

				PlaceIt placeIt;
				activePlaceItList = db.getAllPlaceIts(ACTIVE);

				ArrayList<PlaceIt> placeItArray = new ArrayList<PlaceIt>();
				for (int i = 0; i < activePlaceItList.size(); i++) {
					placeIt = activePlaceItList.get(i);
					placeItArray.add(placeIt);
				}
				ArrayAdapter<PlaceIt> adapter = new ArrayAdapter<PlaceIt>(
						getActivity(), android.R.layout.simple_list_item_1,
						placeItArray);

				ListView listView = (ListView) rootView
						.findViewById(R.id.listViewItems);
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(this);
				// listView.setOnItemLongClickListener(this);

			} else { // Here is the Completed List View fragment
				PlaceIt placeIt;
				activePlaceItList = db.getAllPlaceIts(TRIGGERED);

				ArrayList<PlaceIt> placeItArray = new ArrayList<PlaceIt>();
				for (int i = 0; i < activePlaceItList.size(); i++) {
					placeIt = activePlaceItList.get(i);
					placeItArray.add(placeIt);
				}

				ArrayAdapter<PlaceIt> adapter = new ArrayAdapter<PlaceIt>(
						getActivity(), android.R.layout.simple_list_item_1,
						placeItArray);

				ListView listView = (ListView) rootView
						.findViewById(R.id.listViewItems);
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(this);
			}
			db.close();
			return rootView;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

			PlaceIt place = (PlaceIt) arg0.getItemAtPosition(arg2);
			String activeOrTriggered = place.getStatus();
			if (activeOrTriggered.equalsIgnoreCase(ACTIVE)) {
				activeOrTriggered = "Modify";
			} else {
				activeOrTriggered = "Reactivate";
			}

			alert.setTitle("Delete or " + activeOrTriggered);
			alert.setMessage("Do you want to DELETE or "
					+ activeOrTriggered.toUpperCase(Locale.ENGLISH)
					+ " this Place-It?");

			final int id = place.getId();
			alert.setNeutralButton(activeOrTriggered,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							PlaceItDbHelper db = new PlaceItDbHelper(
									getActivity());
							PlaceIt place = (db.getPlaceIt(id));

							if (place.getStatus().equalsIgnoreCase(TRIGGERED)) {
								place.setStatus(ACTIVE);
								db.updatePlaceIt(place);
								db.close();
								// ////////////////////////////////////
							} else {
								// Go to Place-it manager with Intent. case from
								// list view edit. ID. case 3

								// store the LatLng position of the the clicked
								// position to pass into the form
								// Bundle location_bundle = new Bundle();

								int passID = place.getId();
								// String passTitle = place.getTitle();
								//LatLng passPoint = place.getLocation();
								// String passDescription =
								// place.getDescription();

								// location_bundle.putParcelable("ucsd.cs110.placeit.LocationOnly",
								// passPoint);
								Intent intent = new Intent(getActivity(),
										PlaceItsManager.class);
								intent.putExtra("idIntent", passID);
								// intent.putExtra("titleIntent", passTitle);
								// intent.putExtra("locationOnlyBundle",
								// location_bundle);
								// intent.putExtra("descriptionIntent",
								// passDescription);
								intent.putExtra(
										"ucsd.cs110.placeit.CheckSrouce", 2);

								//SaveLastLocation action = new SaveLastLocation(
								//		passPoint);
								//action.saveLastPlaceIt(db);
								//db.deletePlaceIt(place); // delete it after
															// getting info
															// because it will
															// reactivate ?
								
								startActivity(intent);
							}
							// Toast.makeText(getActivity(),"clicked to modify ",
							// Toast.LENGTH_SHORT).show();

						}
					});
			alert.setNegativeButton("Delete",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PlaceItDbHelper db = new PlaceItDbHelper(
									getActivity());

							SaveLastLocation action = new SaveLastLocation(db
									.getPlaceIt(id).getLocation());
							db.deletePlaceIt(db.getPlaceIt(id));
							action.saveLastPlaceIt(db);

							Toast.makeText(getActivity(), "Item deleted",
									Toast.LENGTH_SHORT).show();
							// Intent intent = new Intent(getActivity(),
							// ListActivity.class);
							// startActivity(intent);

							// Toast.makeText(getActivity(),"clicked to change status to completed",
							// Toast.LENGTH_SHORT).show();
						}
					});
			alert.setPositiveButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(getActivity(), "Action Cancelled",
									Toast.LENGTH_SHORT).show();

						}
					});
			alert.show();
		}

		/*
		 * @Override public boolean onItemLongClick(AdapterView<?> arg0, View
		 * arg1, int arg2, long arg3) { // TODO Auto-generated method stub
		 * AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		 * alert.setTitle("Are you sure?");
		 * alert.setMessage("Warning! This action cannot be undone!"); PlaceIt
		 * place = (PlaceIt) arg0.getItemAtPosition(arg2); final int id =
		 * place.getId(); alert.setPositiveButton("Cancel", new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) { //
		 * TODO Auto-generated method stub
		 * Toast.makeText(getActivity(),"Action Cancelled",
		 * Toast.LENGTH_SHORT).show(); } }); alert.setNegativeButton("Delete",
		 * new DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * PlaceItDbHelper db = new PlaceItDbHelper(getActivity());
		 * db.deletePlaceIt(db.getPlaceIt(id));
		 * Toast.makeText(getActivity(),"Item deleted",
		 * Toast.LENGTH_SHORT).show(); Intent intent = new Intent(getActivity(),
		 * ListActivity.class); startActivity(intent);
		 * 
		 * } });
		 * 
		 * alert.show(); return true; }
		 */
	}
}
