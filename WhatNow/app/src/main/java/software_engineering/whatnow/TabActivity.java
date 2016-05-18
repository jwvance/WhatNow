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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;

import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;


import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import software_engineering.whatnow.firebase_stuff.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabActivity extends AppCompatActivity implements DialogInterface.OnClickListener{

	//Test for location
	public static Context con;
	//
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private RecyclerView recyclerView;
	private Context context;

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
	private ArrayList<Event> events = new ArrayList<Event>();
	//String[] categories = new String[{"ALL","BARS","CLUBS","FOOD","SHOPS","OTHERS"}];



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		Firebase.setAndroidContext(this);


		final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("Loading location. Please wait...");
		dialog.setIndeterminate(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		//Window with spinner waiting for location to be loaded
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				dialog.dismiss();
				try{
					LocationToolBox.storedLatitude= locationData.getLocation().getLatitude();
					LocationToolBox.storedLongitude =  locationData.getLocation().getLongitude();
					//I call onResume() again because it's the only thing i tried that worked.
					onResume();
				} catch (NullPointerException e){
					e.printStackTrace();
				}
			}
		}, 7000);

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

		Firebase.setAndroidContext(context);
		Firebase firebase = new Firebase(Constants.DATABASE_URL/* + "/events_list"*/);
		firebase.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				try{
					HashMap<String, Event> eventHashMap = (HashMap<String, Event>) dataSnapshot.getValue();
					ArrayList<HashMap> weirdEvents = new ArrayList(eventHashMap.values());
					HashMap e;
					for (int i = 0; i < weirdEvents.size(); i++) {
						e = weirdEvents.get(i);
						//	int id = (int) ((long) e.get("id"));
						//	int hourStart = (int) ((long) e.get("hourStart"));
						//	int minuteStart = (int) ((long) e.get("minuteStart"));
						//	int hourEnd = (int) ((long) e.get("hourEnd"));
						//	int minuteEnd = (int) ((long) e.get("minuteEnd"));
						//	String location = (String) e.get("location");
						Host host = new Host((String) ((HashMap) e.get("host")).get("name"));
						//	String name = (String) e.get("name");
						//	String description = (String) e.get("description");
						Category category = new Category((String) ((HashMap) e.get("category")).get("name"));
						long timeStamp = ((long) e.get("timestamp"));
						//	long dateStart = (long) e.get("dateStart");
						//	String imagePath = (String) e.get("imagePath");
						Event event = new Event((int) ((long) e.get("id")), (int) ((long) e.get("hourStart")),
								(int) ((long) e.get("minuteStart")), (int) ((long) e.get("hourEnd")),
								(int) ((long) e.get("minuteEnd")), (String) e.get("location"), host,
								(String) e.get("name"), (String) e.get("description"), category,
								(long) e.get("dateStart"), (String) e.get("imageAsString"), true, timeStamp, context);
						event.setMyLoc(context);
						events.add(event);
						Log.wtf("TabActivity", "Downloaded an event!");
					}
				}catch(Exception e){
					Log.wtf("FIREBASE event name CEL", e.getMessage());
				}
				if(events.size() > 0 && events.get(0) == null) {
					events.clear();
					Log.wtf("TabActivity", "Clearing events!");
				}
				try{
					Log.wtf("TabActivity", "About to save!");
					AddEventActivity.saveEvents(context, events, -1);
					//recyclerAdapter.notifyDataSetChanged();
					setSorting(1);
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				try{
					HashMap<String, Event> eventHashMap = (HashMap<String, Event>) dataSnapshot.getValue();
					ArrayList<HashMap> weirdEvents = new ArrayList(eventHashMap.values());
					HashMap e;
					events.clear();
					for (int i = 0; i < weirdEvents.size(); i++) {
						e = weirdEvents.get(i);
						Host host = new Host((String) ((HashMap) e.get("host")).get("name"));
						Category category = new Category((String) ((HashMap) e.get("category")).get("name"));
						long timeStamp = ((long) e.get("timestamp"));
						Event event = new Event((int) ((long) e.get("id")), (int) ((long) e.get("hourStart")),
								(int) ((long) e.get("minuteStart")), (int) ((long) e.get("hourEnd")),
								(int) ((long) e.get("minuteEnd")), (String) e.get("location"), host,
								(String) e.get("name"), (String) e.get("description"), category,
								(long) e.get("dateStart"), (String) e.get("imageAsString"), true, timeStamp, context);
						event.setMyLoc(context);
						events.add(event);
						Log.wtf("TabActivity", "Downloaded an event!");
					}
				}catch(Exception e){
					Log.wtf("FIREBASE event name CEL", e.getMessage());
				}
				if(events.size() > 0 && events.get(0) == null) {
					events.clear();
					Log.wtf("TabActivity", "Clearing events!");
				}
				try{
					Log.wtf("TabActivity", "About to save!");
					AddEventActivity.saveEvents(context, events, -1);
					setSorting(1);
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {

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
		ArrayList<Event> catEvents;
		for (int i = 0; i < categories.size(); i++) {
			fragment = new TabFragment();
			catEvents = new ArrayList<Event>(events);
			fragment.setContext(this);
			fragment.setCategory(categories.get(i));    //EITHER THIS OR DOWNLOAD EVENTS HERE AND USE setEvents(events)
			fragment.setTabActivity(this);

			if(i > 0)
				for (int j = 0; j < catEvents.size(); j++) {
					if(!catEvents.get(j).getCategory().getName().equals(categories.get(i)))
						catEvents.remove(j);
				}

		//	fragment.setEvents(catEvents);
			fragment.setEvents(events);
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

	public void setSorting(int which) {
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


}
