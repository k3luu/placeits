package ucsd.cse110.placeit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/*
 * Displays the PlaceIts in a list view
 */
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
			Intent intent1 = new Intent(this, MapActivity.class);
			startActivity(intent1);
			return true;
		} else if (item.getItemId() == R.id.list_view_btn) {
			Intent intent2 = new Intent(this, ListActivity.class);
			intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent2);
			return true;
		} else if (item.getItemId() == R.id.add_category_btn) {
			Intent intent1 = new Intent(this, PlaceItsCategoryForm.class);
			startActivity(intent1);
			return true;
		}
		else {
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
			args.putInt(PlaceItUtil.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}
		@Override
		public int getCount() {
			return 2;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment implements
			OnItemClickListener , OnItemLongClickListener{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		
		private ArrayList<PlaceIt> currentPlaceItList;
		private PlaceItDbHelper db;
		public DummySectionFragment() {
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			
			OnlineLocalDatabaseSynchronization tongBuShuJuKu = new OnlineLocalDatabaseSynchronization(getActivity());
			db = new PlaceItDbHelper(getActivity());
			if (getArguments().getInt(PlaceItUtil.ARG_SECTION_NUMBER) == 1) { 
				// Here is the Pending List View fragment
				//PlaceIt placeIt;
				currentPlaceItList = db.getAllPlaceItsByUsernameAndStatus(PlaceItUtil.USERNAME, PlaceItUtil.ACTIVE);
				
				ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				for (PlaceIt item : currentPlaceItList) {
					Map<String, String> datum = new HashMap<String, String>(3);
					datum.put("title", "Title: "+item.getTitle());
				    datum.put("desc", "Description: "+item.getShortDescription());
				    datum.put("id", ""+item.getId());
				    data.add(datum);
				}
				
				SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), data, 
						android.R.layout.simple_list_item_2,
                        new String[] {"title", "desc"},
                        new int[] {android.R.id.text1,
                                   android.R.id.text2});
				ListView listView = (ListView) rootView.findViewById(R.id.listViewItems);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(this);
				listView.setOnItemLongClickListener(this);
			} else { // Here is the Completed List View fragment
				currentPlaceItList = db.getAllPlaceItsByUsernameAndStatus(PlaceItUtil.USERNAME, PlaceItUtil.TRIGGERED);
				ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				for (PlaceIt item : currentPlaceItList) {
					Map<String, String> datum = new HashMap<String, String>(3);
				    datum.put("title", "Title: "+item.getTitle());
				    datum.put("desc", "Description: "+item.getShortDescription());
				    datum.put("id", ""+item.getId());
				    data.add(datum);
				}
				
				SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), data, 
						android.R.layout.simple_list_item_2,
                        new String[] {"title", "desc"},
                        new int[] {android.R.id.text1,
                                   android.R.id.text2});
				ListView listView = (ListView) rootView.findViewById(R.id.listViewItems);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(this);
				listView.setOnItemLongClickListener(this);
			}
			db.close();
			return rootView;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			HashMap<String, String> datum = (HashMap<String, String>) arg0.getItemAtPosition(arg2);
			int placeItId = Integer.parseInt(datum.get("id"));
									
			// Display the details of the PlaceIt
			Details placeItDetails = new Details(getActivity());
			placeItDetails.display(placeItId);
			
		}
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}