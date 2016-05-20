package software_engineering.whatnow;

/**
 * Created by Jason on 5/18/2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookmarkActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<Event> events;
    private ArrayList<Event> savedEvents;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private String searchQuery;
    private int categoryN;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks_res);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ff757575"));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(this);

        //set up preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());

        Intent intent = getIntent();

        events = AddEventActivity.loadEvents(this);


        this.setTitle("Saved Events: ");

        savedEvents = new ArrayList<Event>();

        boolean bookmarkStatus = false;
        for (int i = 0; i < events.size(); i++) {
            int id = events.get(i).getId();
            bookmarkStatus = preferences.getBoolean("bookmarked" + id, false);
            if(bookmarkStatus) {
                savedEvents.add(events.get(i));
            }

        }

        recyclerAdapter = new RecyclerAdapter(savedEvents);


        recyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
    }


    //for back key top left corner
    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }
}

