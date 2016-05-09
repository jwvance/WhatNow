package software_engineering.whatnow;

/**
 * Created by Steve on 4/20/16.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;

import android.content.DialogInterface;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import software_engineering.whatnow.firebase_stuff.Constants;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity implements DialogInterface.OnClickListener{

	//Test for location
	public static Context con;
	//
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private RecyclerView recyclerView;

	//testing distance
	private LocationToolBox locTool;
	private LocationListener locListener;
	//private LocationData locationData = LocationData.getLocationData();
	//More testing
	public static String LOG_TAG = "MyMapApplication";
	private LocationData locationData = LocationData.getLocationData();
	private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
	boolean hasLocation = false;

	//-------------------------
	ArrayList<String> categories;
	private ArrayList<TabFragment> fragments;
	//String[] categories = new String[{"ALL","BARS","CLUBS","FOOD","SHOPS","OTHERS"}];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout);

		Firebase.setAndroidContext(this);

		//Get the location toolbox set up and its listener
		//-------------------------------------------------
		//locationData.getLocation();
		con = this;
		locTool = new LocationToolBox(this);
		locListener = locTool.getLocationListener();
		locationData.getLocation();
		locTool.requestLocationUpdate();
		//locListener = locTool.getLocationListener();



		if(locationData.getLocation() != null){
			LocationToolBox.storedLatitude= locationData.getLocation().getLatitude();
			LocationToolBox.storedLongitude =  locationData.getLocation().getLongitude();
		}
		//-------------------------------------------------
		categories = new ArrayList<String>();	// THESE WILL BE DOWNLOADED FROM OUR SERVER

		categories.add("ALL");
		categories.add("BARS");
		categories.add("CLUBS");
		categories.add("FOOD");
		categories.add("SHOPS");
		categories.add("OTHERS");

		fragments = new ArrayList<TabFragment>();
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		viewPager = (ViewPager) findViewById(R.id.viewpager);

		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);

		//	setupTabIcons();	// TO ADD AN ICON INSIDE THE TAB NAME
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupViewPager(viewPager);
		tabLayout.setupWithViewPager(viewPager);
		locTool.requestLocationUpdate();
		if(locationData.getLocation() != null){
			LocationToolBox.storedLatitude= locationData.getLocation().getLatitude();
			LocationToolBox.storedLongitude =  locationData.getLocation().getLongitude();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// PROBABLY WORK IN HERE TO ADD ICONS IN THE TOP BAR (SEARCH, PROFILE, ORDER, ETC)
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_sort) {
			showSortDialog();

			return true;
		} else if (id == R.id.action_profile) {
			startActivity(new Intent(this, MyProfileActivity.class));

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showSortDialog() {
		AlertDialog levelDialog;
		final CharSequence[] items = {" Popularity ", " Incoming ", " Distance ", " Recent "};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Sort by...");
		builder.setSingleChoiceItems(items, -1, this);
		/*new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("itemSelected", item);
				editor.commit();
				switch (item) {
					final ArrayList<TabFragment>

					// PROBABLY THE BEST OPTION TO SOLVE THIS IS THAT EVENT IS COMPARABLE OR COMPARATOR
					// AND THEN INSIDE COMPARETO THERE IS AN INT CHECK (BASED ON THIS, DEFAULT RECENT)
					// SO THAT IT CAN APPLY DIFFERENT CRITERIA
					case 0:
						// popularity
						for (int i = 0; i < fragments; i++) {

						}
						break;
					case 1:
						// incoming
						break;
					case 2:
						// distance
						break;
					case 3:
						// recent
						break;
				}
				dialog.dismiss();
			}
		});*/
		levelDialog = builder.create();
		levelDialog.show();
	}

/*	private void setupTabIcons() {
		for (int i = 0; i < 6; i++) {
			tabLayout.getTabAt(i).setIcon(R.drawable.ic_people_black_24dp);
		}
	}*/

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		TabFragment fragment;
		for (int i = 0; i < categories.size(); i++) {
			fragment = new TabFragment();
			fragment.setContext(this);
			fragment.setCategory(categories.get(i));    //EITHER THIS OR DOWNLOAD EVENTS HERE AND USE setEvents(events)
			adapter.addFragment(fragment, categories.get(i));
			fragments.add(fragment);
		}
		viewPager.setAdapter(adapter);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("itemSelected", which);
		editor.commit();
		setSorting(which);
	/*	switch (which) {

			// PROBABLY THE BEST OPTION TO SOLVE THIS IS THAT EVENT IS COMPARABLE OR COMPARATOR
			// AND THEN INSIDE COMPARETO THERE IS AN INT CHECK (BASED ON THIS, DEFAULT RECENT)
			// SO THAT IT CAN APPLY DIFFERENT CRITERIA
			case 0:
				// popularity
				setSorting(0);
				break;
			case 1:
				// incoming
				setSorting(1);
				break;
			case 2:
				// distance
				setSorting(2);
				break;
			case 3:
				// recent
				setSorting(3);
				break;
		}*/
		dialog.dismiss();
	}

	private void setSorting(int which) {
		for (int i = 0; i < fragments.size(); i++) {
			fragments.get(i).setSortingCriteria(which, this);
		}
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}

	public void newEvent(View view) {
		Intent intent = new Intent(this, AddEventActivity.class);
		startActivity(intent);
	}

	//-------------------------------------------------
	//Location stuff

	/**
	private void requestLocationUpdate() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager != null &&
				(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
						locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
					PackageManager.PERMISSION_GRANTED) {

				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000, 10, locationListener);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

				hasLocation = true;

				Log.i(LOG_TAG, "requesting location update");
			} else {
				// Should we show an explanation?
				if (ActivityCompat.shouldShowRequestPermissionRationale(this,
						Manifest.permission.ACCESS_FINE_LOCATION)) {

					// Show an expanation to the user *asynchronously* -- don't block
					// this thread waiting for the user's response! After the user
					// sees the explanation, try again to request the permission.
					Log.i(LOG_TAG, "please allow to use your location");

				} else {

					// No explanation needed, we can request the permission.

					ActivityCompat.requestPermissions(this,
							new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
							MY_PERMISSIONS_REQUEST_FINE_LOCATION);

					// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
					// app-defined int constant. The callback method gets the
					// result of the request.
				}
			}
		} else {
			Log.i(LOG_TAG, "requesting location update from user");
			//prompt user to enable location
			Intent gpsOptionsIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(gpsOptionsIntent);
		}
	}

	LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {

			Location lastLocation = locationData.getLocation();

			// Do something with the location you receive.
			double newAccuracy = location.getAccuracy();

			long newTime = location.getTime();
			// Is this better than what we had?  We allow a bit of degradation in time.
			boolean isBetter = ((lastLocation == null) ||
					newAccuracy < lastLocation.getAccuracy() + (newTime - lastLocation.getTime()));
			if (isBetter) {
				// We replace the old estimate by this one.
				locationData.setLocation(location);
			}
			hasLocation = true;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
	};

	**/


}
