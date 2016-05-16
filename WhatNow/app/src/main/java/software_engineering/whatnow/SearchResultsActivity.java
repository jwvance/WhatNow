package software_engineering.whatnow;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
	private ArrayList<Event> events;
	private ArrayList<Event> searchedEvents;
	private RecyclerAdapter recyclerAdapter;
	private RecyclerView recyclerView;
	private String searchQuery;
	private int categoryN;
	private String category;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_res);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(Color.parseColor("#ff757575"));
		setSupportActionBar(toolbar);

		Intent intent = getIntent();
		searchQuery = intent.getStringExtra("search_query");
		categoryN = intent.getIntExtra("current_tab", 0);

		category = Category.getCategories().get(categoryN);

		Log.wtf("SEARCH", searchQuery);

		events = AddEventActivity.loadEvents(this);

		if(categoryN > 0)
			categorizeEvents();

		try {
			this.setTitle("Results for: " + searchQuery);

			searchQuery.toLowerCase();

			searchedEvents = new ArrayList<Event>();

			searchTitles();
			searchDescriptions();
			searchHosts();

			recyclerAdapter = new RecyclerAdapter(searchedEvents);

			((TextView) findViewById(R.id.categorySearch)).setText("Events only from category " + category + "...");
		}catch(NullPointerException npe){
			if(searchQuery == null)
				Log.wtf("SEARCH", "search_query is null");
			recyclerAdapter = new RecyclerAdapter(events);
		}

		recyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		recyclerView.setAdapter(recyclerAdapter);
	}

	private void categorizeEvents() {
		for (int i = events.size() - 1; i >= 0; i--) {
			if(!events.get(i).getCategory().getName().equals(category)){
				events.remove(i);
			}
		}
	}


	private void searchTitles(){
		for (int i = 0; i < events.size(); i++) {
			if(events.get(i).getName().toLowerCase().contains(searchQuery)){
				searchedEvents.add(events.get(i));
			}
		}
	}

	private void searchDescriptions() {
		for (int i = 0; i < events.size(); i++) {
			if(events.get(i).getDescription().toLowerCase().contains(searchQuery)){
				searchedEvents.add(events.get(i));
			}
		}
	}

	private void searchHosts() {
		for (int i = 0; i < events.size(); i++) {
			if(events.get(i).getHost().getName().toLowerCase().contains(searchQuery)){
				searchedEvents.add(events.get(i));
			}
		}
	}
}
