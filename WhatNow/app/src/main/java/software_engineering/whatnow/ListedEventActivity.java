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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;

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
	private ArrayList<Event> events;
	private int eventID;
	private Event event;
	private RecyclerAdapter recyclerAdapter;
	private RecyclerView recyclerView;
	private TextView pastHostEventsText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listed_event);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
		setSupportActionBar(toolbar);


		//	this.setTitle(getIntent().getStringExtra("Name"));
		eventID = getIntent().getIntExtra("Event_ID", -1);
		Log.wtf("ListedEventActivity", "Event ID: " + eventID);

		description = (TextView) findViewById(R.id.listed_event_description);
		category = (TextView) findViewById(R.id.listed_event_category);
		host = (TextView) findViewById(R.id.listed_event_host);
		date = (TextView) findViewById(R.id.listed_event_date);
		times = (TextView) findViewById(R.id.listed_event_times);
		participants = (TextView) findViewById(R.id.listed_event_participants);
		address = (TextView) findViewById(R.id.listed_event_address);
		distance = (TextView) findViewById(R.id.listed_event_distance);
		image = (ImageView) findViewById(R.id.listed_event_image);

		Log.wtf("ListedEventActivity", "About to load!");
		events = AddEventActivity.loadEvents(getApplicationContext());
		event = null;

		for (int i = 0; i < events.size(); i++) {
			if(events.get(i).getId() == eventID) {
				event = events.get(i);
				Log.wtf("ListedEventActivity", "Found matching id!");
				events.remove(i);	//TO FILL LATER THE LIST!
				break;
			}
		}

		if(event != null) {
			this.setTitle(event.getName());
			description.setText(event.getDescription());
			String categoryS = event.getCategory().getName().toLowerCase();
			category.setText(categoryS.substring(0, 1).toUpperCase() + categoryS.substring(1));
			host.setText(event.getHost().getName());
			date.setText(event.getDateString());	//ADD multi date
			times.setText(event.getStartTime() + " - " + event.getEndTime());
			address.setText(event.getLocation());
			distance.setText(event.getDistance() + " away");

			byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			image.setImageBitmap(bitmap);
		}else{
			//error
		}


		/*
		Bitmap bitmap = BitmapFactory.decodeFile(event.getImageAsString());
		image.setImageBitmap(bitmap);*/
		//image.setImageURI(Uri.fromFile(new File(event.getImageAsString())));

		//	address.setText("Santa Cruz");

		//THE EVENTS WILL BE DOWNLOADED FROM FIREBASE

		recyclerView = (RecyclerView) findViewById(R.id.listed_event_recycler_view);
		pastHostEventsText = (TextView) findViewById(R.id.listed_event_past_events);
		//pastHostEventsText.setGravity(View.TEXT_ALIGNMENT_CENTER);

		if(events.size() == 0) {
			recyclerView.setVisibility(View.GONE);
			pastHostEventsText.setText("This is the first event posted by this Host");
		}else{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
			params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
			if(events.size() == 1){
				params.height = RelativeLayout.LayoutParams.MATCH_PARENT;

				pastHostEventsText.setText("This Host has already posted\nanother event\nthrough this app");
			}else{
				params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

				pastHostEventsText.setText("This Host has already posted\n" + events.size() + " events\nthrough this app");
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
}
