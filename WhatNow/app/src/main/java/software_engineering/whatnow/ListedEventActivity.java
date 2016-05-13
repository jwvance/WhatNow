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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListedEventActivity extends AppCompatActivity {
	private TextView description;
	private TextView date;
	private TextView times;
	private TextView participants;
	private TextView address;
	private TextView distance;
	private ImageView image;
	private ArrayList<Event> events;
	private int eventID;
	private Event event;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listed_event);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.listed_event_fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();


			}
		});


		//	this.setTitle(getIntent().getStringExtra("Name"));
		eventID = getIntent().getIntExtra("Event_ID", -1);
		Log.wtf("ListedEventActivity", "Event ID: " + eventID);

		description = (TextView) findViewById(R.id.listed_event_description);
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
				break;
			}
		}

		if(event != null) {
			this.setTitle(event.getName());
			description.setText(event.getDescription());
			date.setText(event.getDateString());
			times.setText(event.getStartTime());
			address.setText(event.getLocation());

			byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			image.setImageBitmap(bitmap);
		}else{

		}

<<<<<<< HEAD
=======
		this.setTitle(event.getName());
		description.setText(event.getDescription());
		date.setText(event.getDateString());
		times.setText(event.getStartTime());
		address.setText(event.getLocation());//event.getLocation()
		distance.setText(event.getDistance()); //added distance ** Didn't work
>>>>>>> catPics

		/*
		Bitmap bitmap = BitmapFactory.decodeFile(event.getImageAsString());
		image.setImageBitmap(bitmap);*/
		//image.setImageURI(Uri.fromFile(new File(event.getImageAsString())));

		//	address.setText("Santa Cruz");
	}

	public void searchMap(View view){
		String toMaps = "https://www.google.com/maps/place/" + address.getText();
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(toMaps));
		startActivity(i);
	}
}
