/* 	CLASS DESCRIPTION:
	-	This is the activity that allows the user to show an event that is in our app
	-	The related layout file is activity_listed_event that contains content_listed_event.
	-	It needs to be updated to get the events from Firebase instead of from the
		Preferences, I think I'll implement it either with a query to the database, or
		saving to the preferences the events that were downloaded (so that there is
		just one call)
	-	searchMap is called when you click the address, it launches Google Maps
		set to search that specific address
*/

package software_engineering.whatnow;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import software_engineering.whatnow.firebase_stuff.Constants;
import software_engineering.whatnow.utils.Utils;

public class ListedEventActivity extends AppCompatActivity implements View.OnClickListener, ChildEventListener, ValueEventListener {
	private TextView description;
	private TextView category;
	private TextView host;
	private TextView date;
	private TextView times;
	private TextView participants;
	private TextView address;
	private TextView distance;
	private ImageView image;
	private ArrayList<Event> events;
	private int eventID;
	private Event event;
	private RecyclerAdapter recyclerAdapter;
	private RecyclerView recyclerView;
	private TextView pastHostEventsText;
	private Firebase firebaseEvents;
	private String key;
	private String hostEmail;
	private String userEmail;
	private HashMap<String, Object> participationsMap;
	private HashMap<String, Object> participantsMap;
	private HashMap<String, Object> eventMap;
	private int participantsN;
	private Firebase firebaseEventParticipants;
	private Firebase firebaseEventUserParticipations;
	private SharedPreferences preferences;
	private int partecipations;
	private boolean canParticipate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listed_event);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
		setSupportActionBar(toolbar);

		eventID = getIntent().getIntExtra("Event_ID", -1);
		description = (TextView) findViewById(R.id.listed_event_description);
		category = (TextView) findViewById(R.id.listed_event_category);
		host = (TextView) findViewById(R.id.listed_event_host);
		date = (TextView) findViewById(R.id.listed_event_date);
		times = (TextView) findViewById(R.id.listed_event_times);
		participants = (TextView) findViewById(R.id.listed_event_participants);
		address = (TextView) findViewById(R.id.listed_event_address);
		distance = (TextView) findViewById(R.id.listed_event_distance);
		image = (ImageView) findViewById(R.id.listed_event_image);

		events = AddEventActivity.loadEvents(getApplicationContext());
		event = null;
		for (int i = 0; i < events.size(); i++) {
			if(events.get(i).getId() == eventID) {
				event = events.get(i);
				events.remove(i);	//TO FILL THE LIST LATER!
				break;
			}
		}

		if(event != null) {
			canParticipate = true;
			key = event.getKey();
			preferences = PreferenceManager.getDefaultSharedPreferences(this);
			userEmail = preferences.getString(Constants.KEY_GOOGLE_EMAIL, "");
			Log.wtf("KEYS CREATING PARTICIPANTS", key);
			Query query = (new Firebase(Constants.DATABASE_URL)).orderByKey().equalTo(key);
			query.addChildEventListener(this);
			/*firebaseEventParticipants = new Firebase(Constants.DATABASE_URL + "/" + key + "/participants");
			firebaseEventParticipants.addChildEventListener(this);*/
			partecipations = preferences.getInt("user_partecipations", 0);
			firebaseEventUserParticipations = new Firebase(Constants.USERS_URL + Utils.encodeEmail(userEmail) + "/partecipations/");
			firebaseEventUserParticipations.addListenerForSingleValueEvent(this);

			this.setTitle(event.getName());
			description.setText(event.getDescription());
			String categoryS = event.getCategory().getName().toLowerCase();
			category.setText(categoryS.substring(0, 1).toUpperCase() + categoryS.substring(1));
			host.setText(event.getHost().getName());
			host.setTextColor(Color.parseColor("#33a0ff"));
			date.setText(event.getDateString());	//ADD multi date
			times.setText(event.getStartTime() + " - " + event.getEndTime());
			int p = event.getNumberOfGuests();
			participants.setText(event.getNumberOfGuests() + " participants");
			address.setText(event.getLocation());
			address.setTextColor(Color.parseColor("#33a0ff"));
			distance.setText(event.getDistance() + " away");

			byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			image.setImageBitmap(bitmap);

			hostEmail = event.getHost().getBusinessEmail();

			key = event.getKey();
			Log.wtf("KEYS LISTED EVENT", key);

			Log.wtf("HOST LISTED EVENT", hostEmail);
			/*if(event.getHost().getBusinessEmail() != null){
				String encodedEmail = Utils.encodeEmail(event.getHost().getBusinessEmail());
				Firebase firebase = new Firebase(Constants.FIREBASE_URL + "users/" + encodedEmail);
				firebase.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						//for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
						try{
							HashMap e = dataSnapshot.getValue(HashMap.class);
							//User user = dataSnapshot.getValue(User.class);
							if(e != null)
								Log.wtf("HOST LISTED EVENT", e.get("name").toString());
						//}
						}catch (Exception e){
							Log.wtf("HOST LISTED EVENT", "no host info");
						}
					}

					@Override
					public void onCancelled(FirebaseError firebaseError) {

					}
				});
			}*/

			if(!hostEmail.equals(userEmail)){
				findViewById(R.id.listed_edit_delete_layout).setVisibility(View.GONE);
				findViewById(R.id.listed_event_final_separator).setVisibility(View.GONE);
			}

		}else{
			//error
		}

		recyclerView = (RecyclerView) findViewById(R.id.listed_event_recycler_view);
		pastHostEventsText = (TextView) findViewById(R.id.listed_event_past_events);
		//pastHostEventsText.setGravity(View.TEXT_ALIGNMENT_CENTER);

		if(events.size() == 0) {
			recyclerView.setVisibility(View.GONE);
			pastHostEventsText.setText("This is the only event by this host:");
		}else{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
			params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
			if(events.size() == 1){
				params.height = RelativeLayout.LayoutParams.MATCH_PARENT;

				pastHostEventsText.setText("Other events from this host:");
			}else{
				params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

				pastHostEventsText.setText("This host has " + events.size() + " postings:");
			}
			recyclerView.setLayoutParams(params);
		}
		recyclerAdapter = new RecyclerAdapter(events);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		recyclerView.setAdapter(recyclerAdapter);

		((FloatingActionButton) findViewById(R.id.listed_event_fab)).setOnClickListener(this);
	}

	public void searchMap(View view){
		String toMaps = "https://www.google.com/maps/place/" + address.getText();
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(toMaps));
		startActivity(i);
	}

	public void viewHost(View view){
		if(hostEmail == null){
			Toast.makeText(ListedEventActivity.this, "Missing Host...", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i = new Intent(this, HostProfileActivity.class);
		i.putExtra("encodedEmail", Utils.encodeEmail(hostEmail));
		startActivity(i);
	}

	public void deleteDialogue(final View view){

		//dialogue "are you sure"

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
		alertDialogBuilder
				.setMessage("Are you sure you want to delete this event?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteEvent(view);
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		//show dialogue
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();



	}

	public void deleteEvent(View view) {
		//call to firebase to delete
		Firebase.setAndroidContext(this);
		firebaseEvents = new Firebase(Constants.DATABASE_URL);
		firebaseEvents.child(event.getKey()).removeValue();

		//status message... not working
		Snackbar snackbar;
		snackbar = Snackbar.make(view, "Event deleted", Snackbar.LENGTH_LONG);
		snackbar.show();

		//exit to previous activity
		finish();
	}


	public void editEvent(View view){

		//call to firebase to delete

		//call to preferences to delete

		//call to

	}

	@Override
	public void onClick(View v) {
		if(canParticipate) {
			try {
				event.increaseGuests();

				Log.wtf("PARTECIPATIONS #", "" + partecipations);
				participationsMap.put("partecipation_" + (partecipations + 1), key);
				firebaseEventUserParticipations.setValue(participationsMap);

				Log.wtf("PARTICIPATIONS", "saved participationsMap");

				//firebaseEventParticipants = firebaseEventParticipants.child("participant_" + participantsN);
				userEmail = Utils.encodeEmail(userEmail);
				if (participantsMap == null)
					participantsMap = new HashMap<String, Object>();
				//Map<String, Object> newParticipantMap = new HashMap<String, Object>();
				participantsMap.put("participant_" + participantsN, userEmail);
				firebaseEventParticipants.setValue(participantsMap);

				Log.wtf("PARTICIPATIONS", "saved participantsMap");

				SharedPreferences.Editor editor = preferences.edit();
				partecipations++;
				editor.putInt("user_partecipations", partecipations);
				editor.commit();
				Log.wtf("PARTICIPATIONS", "saved in preferences");
			} catch (Exception e) {
				Log.wtf("PARTICIPATION ON CLICK", e.getMessage());
			}
		}else
			Toast.makeText(ListedEventActivity.this, "Sorry, You have already participated...", Toast.LENGTH_SHORT).show();
			//finish();
	}

	//for event
	@Override
	public void onChildAdded(DataSnapshot dataSnapshot, String s) {
		try{
			eventMap = dataSnapshot.getValue(HashMap.class);
			participantsMap = (HashMap) eventMap.get("participants");
			if(participantsMap.containsValue(Utils.encodeEmail(userEmail))){
				canParticipate = false;
				return;
			}
		}catch (Exception e){
			Log.wtf("PARTICIPATIONS EVENT map, maybe because no participants yet", e.getMessage());
		}
		if(participantsMap == null)
			participantsMap = new HashMap<String, Object>();
		Log.wtf("PARTICIPATIONS", "got participantsMap");
		try{
			participantsN = Integer.parseInt(participantsMap.get("number").toString());
		}catch (Exception e){
			Log.wtf("PARTICIPATIONS EVENT", e.getMessage() + "\tno number");
		}
	}

	@Override
	public void onChildChanged(DataSnapshot dataSnapshot, String s) {
		try{
			participantsMap = dataSnapshot.getValue(HashMap.class);
		}catch (Exception e){
			Log.wtf("PARTICIPATIONS EVENT map", e.getMessage());
		}
		if(participantsMap == null)
			participantsMap = new HashMap<String, Object>();
		Log.wtf("PARTICIPATIONS", "updated participantsMap");
		try{
			participantsN = Integer.parseInt(participantsMap.get("number").toString());
		}catch (Exception e){
			Log.wtf("PARTICIPATIONS EVENT", e.getMessage() + "\tno number");
		}
	}

	@Override
	public void onChildRemoved(DataSnapshot dataSnapshot) {

	}

	@Override
	public void onChildMoved(DataSnapshot dataSnapshot, String s) {

	}

	//for user
	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		participationsMap = dataSnapshot.getValue(HashMap.class);
		Log.wtf("PARTICIPATIONS", "got participationsMap");
		if(participationsMap == null)
			participationsMap = new HashMap<String, Object>();
	}

	@Override
	public void onCancelled(FirebaseError firebaseError) {

	}

}
