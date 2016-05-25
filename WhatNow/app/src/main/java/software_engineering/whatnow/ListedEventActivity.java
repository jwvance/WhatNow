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

import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.util.ArrayList;

import software_engineering.whatnow.firebase_stuff.Constants;

public class ListedEventActivity extends AppCompatActivity {
	private TextView description;
	private TextView category;
	private TextView host;
	private TextView date;
	private TextView times;
	private TextView participants;
	private TextView address;
	private TextView distance;
	private ImageView image;
	private TextView title;
	private ArrayList<Event> events;
	private int eventID;
	private Event event;
	private RecyclerAdapter recyclerAdapter;
	private RecyclerView recyclerView;
	private TextView pastHostEventsText;
	private Firebase firebaseEvents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listed_event);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
		setSupportActionBar(toolbar);

		title = (TextView) findViewById(R.id.listed_event_title);
		eventID = getIntent().getIntExtra("Event_ID", -1);
		description = (TextView) findViewById(R.id.listed_event_description);
		//category = (TextView) findViewById(R.id.listed_event_category);
		host = (TextView) findViewById(R.id.listed_event_host);
		date = (TextView) findViewById(R.id.listed_event_date);
		times = (TextView) findViewById(R.id.listed_event_times);
		participants = (TextView) findViewById(R.id.listed_event_participants);
		address = (TextView) findViewById(R.id.listed_event_address);
		distance = (TextView) findViewById(R.id.listed_event_distance);
		image = (ImageView) findViewById(R.id.listed_event_image);
		event = null;

		events = ((GlobalEvents) this.getApplication()).getEventList();

		for (Event currEvent : events) {
			if(currEvent.getId() == eventID) {
				event = currEvent;
				break;
			}
		}

		if(event != null) {
			this.setTitle("");
			title.setText(event.getName());
			description.setText(event.getDescription());
			//String categoryS = event.getCategory().getName().toLowerCase();
			//category.setText(categoryS.substring(0, 1).toUpperCase() + categoryS.substring(1));
			host.setText(event.getHost().getName());
			host.setTextColor(Color.parseColor("#33a0ff"));
			date.setText(event.getDateString());	//ADD multi date
			times.setText(event.getStartTime() + " - " + event.getEndTime());
			address.setText(event.getLocation());
			address.setTextColor(Color.parseColor("#33a0ff"));
			distance.setText(event.getDistance() + " away");

			byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			image.setImageBitmap(bitmap);

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
	}

	public void searchMap(View view){
		String toMaps = "https://www.google.com/maps/place/" + address.getText();
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(toMaps));
		startActivity(i);
	}

	public void viewHost(View view){
		Intent i = new Intent(this, MyProfileActivity.class);
		startActivity(i);
	}

	public void deleteDialogue(final View view){
		//dialogue "are you sure"
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
		alertDialogBuilder
				.setMessage("Are you sure you want to delete this event? This will be permanent.")
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
		Firebase firebase = new Firebase(Constants.DATABASE_URL);
		firebase.child(event.getKey()).removeValue();

		//status message... not working
		Toast.makeText(this, "Event Listing Deleted.", Toast.LENGTH_LONG).show();

		//exit to previous activity
		finish();
	}

	public void editEvent(View view){
		//start edit event activity
		event.getKey();
		Intent intent = new Intent(this, EditEventActivity.class);
		intent.putExtra("KEY", event.getKey());
		startActivity(intent);

	}

}
