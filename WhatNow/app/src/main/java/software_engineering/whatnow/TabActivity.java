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

import android.support.v7.app.ActionBar;

import android.support.v4.widget.SwipeRefreshLayout;

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
//	private ArrayList<Event> events = new ArrayList<Event>();
	private MenuItem searchAction;
	private boolean isSearchOpened = false;
	private EditText editSearch;
	private int sortingCriteria;
	private ArrayList<ArrayList<Event>> eventsEvents;
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



		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sortingCriteria = preferences.getInt("itemSelected", 1);

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
						Host host = new Host((String) ((HashMap) e.get("host")).get("name"));
						Category category = new Category((String) ((HashMap) e.get("category")).get("name"));
						int categoryN = categories.indexOf(category.getName());
						long timeStamp = ((long) e.get("timestamp"));
						Event event = new Event((int) ((long) e.get("id")), (int) ((long) e.get("hourStart")),
								(int) ((long) e.get("minuteStart")), (int) ((long) e.get("hourEnd")),
								(int) ((long) e.get("minuteEnd")), (String) e.get("location"), host,
								(String) e.get("name"), (String) e.get("description"), category,
								(long) e.get("dateStart"), (String) e.get("imageAsString"), true, timeStamp, context);
						event.setMyLoc(context);

						eventsEvents.get(categoryN).add(event);	//specific category
						eventsEvents.get(0).add(event);	//ALL

					//	events.add(event);
						Log.wtf("TabActivity", "Downloaded an event!");
					}
				}catch(Exception e){
					Log.wtf("FIREBASE event name CEL", e.getMessage());
				}
				if(eventsEvents.get(0).size() > 0 && eventsEvents.get(0).get(0) == null) {
					eventsEvents.clear();
					Log.wtf("TabActivity", "Clearing events!");
				}
				try{
					Log.wtf("TabActivity", "About to save!");
					AddEventActivity.saveEvents(context, eventsEvents.get(0), -1);
					//recyclerAdapter.notifyDataSetChanged();
				//	findViewById(R.id.fragmentProgressBar).setVisibility(View.INVISIBLE);

					//setSorting(sortingCriteria);
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
					eventsEvents.clear();
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
						int categoryN = categories.indexOf(category.getName());
						long timeStamp = ((long) e.get("timestamp"));
						//	long dateStart = (long) e.get("dateStart");
						//	String imagePath = (String) e.get("imagePath");
						Event event = new Event((int) ((long) e.get("id")), (int) ((long) e.get("hourStart")),
								(int) ((long) e.get("minuteStart")), (int) ((long) e.get("hourEnd")),
								(int) ((long) e.get("minuteEnd")), (String) e.get("location"), host,
								(String) e.get("name"), (String) e.get("description"), category,
								(long) e.get("dateStart"), (String) e.get("imageAsString"), true, timeStamp, context);
						event.setMyLoc(context);

						eventsEvents.get(categoryN).add(event);	//specific category
						eventsEvents.get(0).add(event);	//ALL

						//	events.add(event);
						Log.wtf("TabActivity", "Downloaded an event!");
					}
				}catch(Exception e){
					Log.wtf("FIREBASE event name CEL", e.getMessage());
				}
				if(eventsEvents.get(0).size() > 0 && eventsEvents.get(0).get(0) == null) {
					eventsEvents.clear();
					Log.wtf("TabActivity", "Clearing events!");
				}
				try{
					Log.wtf("TabActivity", "About to save!");
					AddEventActivity.saveEvents(context, eventsEvents.get(0), -1);
					//recyclerAdapter.notifyDataSetChanged();
					//	findViewById(R.id.fragmentProgressBar).setVisibility(View.INVISIBLE);

					setSorting(sortingCriteria);
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
		setSorting(sortingCriteria);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//setupViewPager(viewPager);
		setSorting(sortingCriteria);
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
		} else if(id == R.id.action_search){
			handleMenuSearch();
		}

		return super.onOptionsItemSelected(item);
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
		//ArrayList<Event> catEvents;
		for (int i = 0; i < categories.size(); i++) {
			fragment = new TabFragment();
			fragment.setTabActivity(this);//added this; Carlos
			//catEvents = new ArrayList<Event>(events);
			fragment.setContext(this);
			fragment.setCategory(categories.get(i));    //EITHER THIS OR DOWNLOAD EVENTS HERE AND USE setEvents(events)


		/*	if(i > 0) {
				for (int j = events.size() - 1; j >= 0; j--) {
					if (!events.get(j).getCategory().getName().equals(categories.get(i))) {
						events.remove(j);
					}
				}
			}
			fragment.setEvents(events);*/
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
