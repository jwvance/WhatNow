package software_engineering.whatnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ListedEventActivity extends AppCompatActivity {
	private TextView description;
	private TextView date;
	private TextView times;
	private TextView participants;
	private TextView address;
	private TextView distance;
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

		description = (TextView) findViewById(R.id.listed_event_description);
		date = (TextView) findViewById(R.id.listed_event_date);
		times = (TextView) findViewById(R.id.listed_event_times);
		participants = (TextView) findViewById(R.id.listed_event_participants);
		address = (TextView) findViewById(R.id.listed_event_address);
		distance = (TextView) findViewById(R.id.listed_event_distance);

		events = EventTestCreatorActivity.loadEvents(getApplicationContext());
		event = null;

		for (int i = 0; i < events.size(); i++) {
			if(events.get(i).getId() == eventID)
				event = events.get(i);
		}

		this.setTitle(event.getName());
		description.setText(event.getDescription());
		date.setText(event.getDateString());
		times.setText(event.getStartTime());
		address.setText(event.getLocation());

	//	address.setText("Santa Cruz");
	}

	public void searchMap(View view){
		String toMaps = "https://www.google.com/maps/place/" + address.getText();
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(toMaps));
		startActivity(i);
	}
}
