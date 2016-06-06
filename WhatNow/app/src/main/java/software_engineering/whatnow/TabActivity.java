/* 	CLASS DESCRIPTION:
	-	This is the main activity, the one with the tabs
	-	The related layout file is tab_layout but it contains also the Fragments
	-	it implements the DialogListener interface to handle the answer from the
		sorting dialog.
	-	in onCreate, after the usual layout stuff, some location code and deciding
		the categories, the toolbar is set and also the tabs with the ViewPager
	-	in here are retrieved the events from Firebase, thanks to a Listener
		attached to the database
	-	I don't honestly know why but the events I get from Firebase are
		not Event objs but HashMaps, that's why you read HashMap so many times
	-	onCreate contains also the Firebase stuff
	-	the ViewPager is the one that handles the Fragments visualization
	-	there is a Fragment (an obj with a layout related) for each tab
	-	the onCreateOptionsMenu and the onOptionSelectedItem methods respectively
		set the layout menu of the top right corner and associate different
		actions depending on what was clicked in that menu
	-	showSortDialog is just to show the sorting Dialog, the answer is handled
		by the onClick method
	-	the setupViewPager adapter creates the fragments, one related to each
		category, it gives them to the ViewPagerAdapter
	-	onClick is called when a sorting criteria is selected, it calls setSorting:
	-	setSorting sets the new sorting criteria to each Fragment that is gonna set
		it to each Event
	-	then there is a ViewPagerAdapter class which takes care of how to show the
		tabs, fragments and all that tab related stuff
	-	newEvent is called when the FloatingActionButton is pressed
	-	then there are a 1000 lines of code that Carlos put there and commented o.O
*/

package software_engineering.whatnow;

/**
 * Created by Steve on 4/20/16.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;

import android.content.DialogInterface;


import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import software_engineering.whatnow.firebase_stuff.Constants;
import software_engineering.whatnow.login.BaseActivity;
import software_engineering.whatnow.login.base.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabActivity extends AppCompatActivity implements GeoQueryEventListener, DialogInterface.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

	//Test for location
	public static Context con;
	//
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private RecyclerView recyclerView;
	private Context context;
	private float maximumDistance = -1.0f;
	private int cont =0;
	private int cont1 =0;

	//testing distance
	private LocationToolBox locTool;
	private LocationListener locListener;

	private ArrayList<String> closeEventsKeys = new ArrayList<String>();

	private GeoLocation myPosition = null;
	GeoQuery geoQuery = null;

	//private LocationData locationData = LocationData.getLocationData();
	//More testing
	public static String LOG_TAG = "MyMapApplication";
	private LocationData locationData = LocationData.getLocationData();
	private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
	boolean hasLocation = false;

	//-------------------------
	ArrayList<String> categories;
	private ArrayList<TabFragment> fragments;
	private SharedPreferences mSharedPref;
//	private ArrayList<Event> events = new ArrayList<Event>();
	private MenuItem searchAction;
	private boolean isSearchOpened = false;
	private EditText editSearch;
	private int sortingCriteria;
	private ArrayList<ArrayList<Event>> eventsEvents;
	//String[] categories = new String[{"ALL","BARS","CLUBS","FOOD","SHOPS","OTHERS"}];
	private Bundle savedInstanceState;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;

	//	Log.wtf("LOGIN", "inside TabActivity");
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		if(!mSharedPref.getBoolean("logged_in", false)){
			Log.wtf("LOGIN", "inside Tab, going to LoginActivity");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		Log.wtf("LOGIN: logged in as", mSharedPref.getString(Constants.KEY_SIGNUP_EMAIL, null));

		setContentView(R.layout.tab_layout);

		context = this;

		//Get the location toolbox set up and its listener
		//-------------------------------------------------
		//locationData.getLocation();
		con = this;
		locTool = new LocationToolBox(this);
		locListener = locTool.getLocationListener();
		locationData.getLocation();
		locTool.requestLocationUpdate();
		//locListener = locTool.getLocationListener();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sortingCriteria = preferences.getInt("itemSelected", 1);

		Location mLastLocation;
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}


		//Stefi snippet----------------------
		mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (mLastLocation != null){
			LocationToolBox.storedLatitude = mLastLocation.getLatitude();
			LocationToolBox.storedLongitude = mLastLocation.getLongitude();
		}


		if(locationData.getLocation() != null){
			LocationToolBox.storedLatitude= locationData.getLocation().getLatitude();
			LocationToolBox.storedLongitude =  locationData.getLocation().getLongitude();
		}
		//-------------------------------------------------

		this.myPosition = new GeoLocation(LocationToolBox.storedLatitude, LocationToolBox.storedLongitude);
		categories = Category.getCategories();

		eventsEvents = new ArrayList<ArrayList<Event>>();
		for (int i = 0; i < categories.size(); i++) {
			eventsEvents.add(new ArrayList<Event>());
		}

		fragments = new ArrayList<TabFragment>();
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		viewPager = (ViewPager) findViewById(R.id.viewpager);


		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

		maximumDistance = preferences.getFloat("maximumDistance", 50);

		Firebase.setAndroidContext(context);

		GeoFire geoFire = new GeoFire(new Firebase(Constants.GEOFIRE_URL));
		this.geoQuery = geoFire.queryAtLocation(myPosition, maximumDistance);

		this.geoQuery.addGeoQueryEventListener(this);
	}


	@Override
	public void onKeyEntered(String key, GeoLocation location) {
		//Firebase firebase = new Firebase(Constants.DATABASE_URL);


		Query query = (new Firebase(Constants.DATABASE_URL)).orderByKey().equalTo(key);

		query.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				try {
					HashMap e = dataSnapshot.getValue(HashMap.class);

					Host host = new Host((String) ((HashMap) e.get("host")).get("name"));
					String email = (String) ((HashMap) e.get("host")).get("businessEmail");
					if(email == null)
						email = (String) ((HashMap) e.get("host")).get("email");
					host.setBusinessEmail(email);
					Log.wtf("HOST DOWNLOADING", host.getBusinessEmail());

					Category category = new Category(((HashMap) e.get("category")).get("name").toString());

					int categoryN = categories.indexOf(category.getName());

					long timeStamp = Long.parseLong(e.get("timestamp").toString());

					Event event = new Event(Integer.valueOf(e.get("id").toString()), Integer.valueOf(e.get("numberOfGuests").toString()), Integer.valueOf(e.get("hourStart").toString()),
							Integer.valueOf(e.get("minuteStart").toString()), Integer.valueOf(e.get("hourEnd").toString()),
							Integer.valueOf(e.get("minuteEnd").toString()), e.get("location").toString(), host,
							(String) e.get("name"), (String) e.get("description"), category,
							Long.parseLong(e.get("dateStart").toString()),Long.parseLong(e.get("dateEnd").toString()), (String) e.get("imageAsString"), dataSnapshot.getKey(), true, timeStamp);

					event.setMyLoc(context);
					eventsEvents.get(categoryN).add(event);    //specific category
					eventsEvents.get(0).add(event);

					if (event.getDateEnd()>System.currentTimeMillis()) {
						((GlobalEvents) getApplication()).appendEvent(event);
						Log.wtf("LOGGED?", event.getKey());


						setupViewPager(viewPager);

					}


					}catch(Exception e){
						Log.wtf("FIREBASE error", e.getMessage());
					}
					if (eventsEvents.get(0).size() > 0 && eventsEvents.get(0).get(0) == null) {
						eventsEvents.clear();
						Log.wtf("TabActivity", "Clearing events!");
					}

			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //refresh cards
				try{
					Event event = dataSnapshot.getValue(Event.class);
					((GlobalEvents) getApplication()).updateEvent(dataSnapshot.getKey(), event);


					for(int i = 0; i < eventsEvents.size(); i++){
						for(int j = 0; j < eventsEvents.get(i).size(); j++){
							if(eventsEvents.get(i).get(j).getKey().equals(dataSnapshot.getKey())){
								eventsEvents.get(i).remove(j);
							}
						}
					}

					GeoFire geoFire = new GeoFire(new Firebase(Constants.GEOFIRE_URL));

					geoFire.setLocation(dataSnapshot.getKey(), new GeoLocation(event.getMyLoc().latitude, event.getMyLoc().longitude));

					setupViewPager(viewPager);
				}catch(Exception e){
					Log.wtf("EDIT EVENTS", e.getMessage());
				}
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				((GlobalEvents) getApplication()).deleteEvent(dataSnapshot.getKey());


				for(int i = 0; i < eventsEvents.size(); i++){
					for(int j = 0; j < eventsEvents.get(i).size(); j++){
						if(eventsEvents.get(i).get(j).getKey().equals(dataSnapshot.getKey())){
							eventsEvents.get(i).remove(j);
						}
					}
				}

				GeoFire geoFire = new GeoFire(new Firebase(Constants.GEOFIRE_URL));

				geoFire.removeLocation(dataSnapshot.getKey());

				setupViewPager(viewPager);
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {

			}
		});

	}

	@Override
	public void onKeyExited(String key) {

	}

	@Override
	public void onKeyMoved(String key, GeoLocation location) {

	}

	@Override
	public void onGeoQueryReady() {

	}

	@Override
	public void onGeoQueryError(FirebaseError error) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		//setupViewPager(viewPager);
		try {
			setSorting(sortingCriteria);
		}catch(Exception e){
			Log.wtf("TAB ACTIVITY, calling onCreate", e.getMessage());
			onCreate(savedInstanceState);
		}
	//	tabLayout.setupWithViewPager(viewPager);
		locTool.requestLocationUpdate();
		if(locationData.getLocation() != null){
			LocationToolBox.storedLatitude= locationData.getLocation().getLatitude();
			LocationToolBox.storedLongitude =  locationData.getLocation().getLongitude();

		}
	}

	public void onBackPressed(){
		if(isSearchOpened){
			closeSearch(getSupportActionBar());
		}else
			super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// PROBABLY WORK IN HERE TO ADD ICONS IN THE TOP BAR (SEARCH, PROFILE, ORDER, ETC)
		getMenuInflater().inflate(R.menu.menu_main, menu);
		try {
			if (mSharedPref.getBoolean("is_host", false))
				findViewById(R.id.action_host).setVisibility(View.GONE);
			if (mSharedPref.getBoolean("is_user", false))
				findViewById(R.id.action_user).setVisibility(View.GONE);
		}catch(Exception e){
			Log.wtf("MENU", e.getMessage());
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		searchAction = menu.findItem(R.id.action_search);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		}else if(id == R.id.action_addEvent){
			Intent intent = new Intent(this, AddEventActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.action_sort) {
			showSortDialog();
			return true;
		} else if (id == R.id.action_profile) {
			startActivity(new Intent(this, MyProfileActivity.class));
			return true;
		} else if(id == R.id.action_logout){
			logout();
			return true;
		} else if(id == R.id.action_search){
			handleMenuSearch();
			return true;
		}else if(id == R.id.action_bookmarks){
			startActivity(new Intent(this, BookmarkActivity.class));
			return true;
		}else if(id == R.id.action_host){
			Intent intent = new Intent(this, HostQActivity.class);
			intent.putExtra("from_login", false);
			startActivity(intent);
			return true;
		}else if(id == R.id.action_user){
			Intent intent = new Intent(this, UserQActivity.class);
			intent.putExtra("from_login", false);
			try{
				startActivity(intent);
			}catch(Exception e){
				Log.wtf("USER INFO", e.getMessage());
				Toast.makeText(TabActivity.this, "Sorry, unable to retrieve info", Toast.LENGTH_SHORT).show();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void logout() {
		Log.wtf("LOGOUT", "inside Base about to log out");

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();
		/**
		 * Build a GoogleApiClient with access to the Google Sign-In API and the
		 * options specified by gso.
		 */

        /* Setup the Google API object to allow Google+ logins */
		GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

		String mProvider = mSharedPref.getString(Constants.KEY_PROVIDER, null);
        /* Logout if mProvider is not null */
		if (mProvider != null) {
			Log.e(LOG_TAG, "PROVIDER:" + mProvider);

			(new Firebase(Constants.FIREBASE_URL)).unauth();

			if (mProvider.equals(Constants.GOOGLE_PROVIDER)) {

				try {
					/* Logout from Google+ */
					Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
							new ResultCallback<Status>() {
								@Override
								public void onResult(Status status) {
									//nothing
								}
							});
				} catch (Exception e) {
					//hide like there's no tomorrow
				}
			}
		}

		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.remove("logged_in");
		editor.commit();
		startActivity(new Intent(this, LoginActivity.class));
		finish();
	}

	protected void handleMenuSearch(){
		ActionBar action = getSupportActionBar();

		if(isSearchOpened){ //test if the search is open
			//closeSearch(action);
			doSearch();
		} else { //open the search entry

			action.setDisplayShowCustomEnabled(true);
			action.setCustomView(R.layout.search_bar);
			action.setDisplayShowTitleEnabled(false);

			editSearch = (EditText)action.getCustomView().findViewById(R.id.editSearch);

			editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						doSearch();
						return true;
					}
					return false;
				}
			});

			editSearch.requestFocus();

			//open the keyboard focused in the editSearch
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(editSearch, /*InputMethodManager.SHOW_IMPLICIT*/0);

			//add the close icon
			//searchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));

			isSearchOpened = true;
		}
	}

	private void closeSearch(ActionBar action) {
		action.setDisplayShowCustomEnabled(false);
		action.setDisplayShowTitleEnabled(true);

		//hides the keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
	//	imm.hideSoftInputFromInputMethod(editSearch.getWindowToken(), 0);

		//add the search icon in the action bar
		//searchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));

		isSearchOpened = false;
	}

	private void doSearch() {
		Toast toast = Toast.makeText(this, "Showing only events containing: \"" + editSearch.getText() + "\"", Toast.LENGTH_LONG);
		toast.show();

		int currentTab = viewPager.getCurrentItem();

		Intent intent = new Intent(this, SearchResultsActivity.class);
		intent.putExtra("search_query", editSearch.getText().toString());
		intent.putExtra("current_tab", currentTab);

		startActivity(intent);

		closeSearch(getSupportActionBar());
	}

	private void showSortDialog() {
		AlertDialog levelDialog;
		final CharSequence[] items = {" Most Popular ", " Upcoming ", " Distance from me ", " Recently added "};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Sort by...");
		builder.setSingleChoiceItems(items, -1, this);

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

			fragment.setEvents(eventsEvents.get(i));
			adapter.addFragment(fragment, categories.get(i));
			fragments.add(fragment);
		}
		viewPager.setAdapter(adapter);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = preferences.edit();
		sortingCriteria = which;
		editor.putInt("itemSelected", which);
		editor.commit();
		setSorting(which);

		dialog.dismiss();
	}

	private void setSorting(int which) {
		for (int i = 0; i < fragments.size(); i++) {
			fragments.get(i).setSortingCriteria(which, this);
		}
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.wtf("OnConnectionFailed", connectionResult.getErrorMessage());
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
}
