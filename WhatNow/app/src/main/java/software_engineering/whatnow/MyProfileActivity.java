/* 	CLASS DESCRIPTION:
	-	This is the activity that allows the user to see his/her profile.
	-	The related layout file is activity_my_profile that contains content_my_profile.
	-	still extremely empty, will contain info from facebook and stuff
*/

package software_engineering.whatnow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener{
	private ArrayList<Event> events;
	private RecyclerAdapter recyclerAdapter;
	private RecyclerView recyclerView;
	private TextView pastEventsText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(this);

		this.setTitle("My Profile");

		//testing
		events = ((GlobalEvents) getApplication()).getEventList();

		recyclerView = (RecyclerView) findViewById(R.id.profile_recycler_view);
		pastEventsText = (TextView) findViewById(R.id.profile_past_events);
		pastEventsText.setGravity(View.TEXT_ALIGNMENT_CENTER);

		if(events.size() == 0) {
			recyclerView.setVisibility(View.GONE);
			pastEventsText.setText("Has not participated in\nany events through this app yet");
		}else{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
			params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
			if(events.size() == 1){
				params.height = RelativeLayout.LayoutParams.MATCH_PARENT;

				pastEventsText.setText("Has already participated in\n1 event through this app");
			}else{
				params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

				pastEventsText.setText("Has already participated in\n" + events.size() + " events through this app");
			}
			recyclerView.setLayoutParams(params);
		}
		recyclerAdapter = new RecyclerAdapter(events);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		recyclerView.setAdapter(recyclerAdapter);
	}

	private Intent getFBIntent(String facebookId) {
		try {	// Check if FB app is even installed
			getPackageManager().getPackageInfo("com.facebook.katana", 0);

			String facebookScheme = "fb://profile/" + facebookId;
			return new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
		}
		catch(Exception e) {	// Cache and Open a url in browser
			String facebookProfileUri = "https://www.facebook.com/" + facebookId;
			return new Intent(Intent.ACTION_VIEW, Uri.parse(facebookProfileUri));
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = getFBIntent("stefanogasperini.sg");

		if (intent != null)
			startActivity(intent);
	}
}
