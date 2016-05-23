package software_engineering.whatnow;/* 	CLASS DESCRIPTION:
	-	This is the activity that allows the user to see his/her profile.
	-	The related layout file is activity_my_profile that contains content_my_profile.
	-	still extremely empty, will contain info from facebook and stuff
*/


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import software_engineering.whatnow.Event;
import software_engineering.whatnow.R;
import software_engineering.whatnow.RecyclerAdapter;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener{
	private ArrayList<Event> events;
	private RecyclerAdapter recyclerAdapter;
	private RecyclerView recyclerView;
	private TextView pastEventsText;

	//--------Fields for the user
	String Name, FElink, city, sex;
	private SharedPreferences mSharedPref;
	//---------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_profile);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		//------Fields for the host--------
		final TextView host_name = (TextView) findViewById(R.id.profile_name);
		final TextView host_sex = (TextView) findViewById(R.id.profile_sex);
		final TextView host_city = (TextView) findViewById(R.id.profile_city);
		//---------------------------------

		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(this);

		this.setTitle("My Profile");

		//testing
		events = AddEventActivity.loadEvents(this);

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

		FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				AccessToken accessToken = loginResult.getAccessToken();
				Profile profile = Profile.getCurrentProfile();
				//Uri uri = Uri.parse("https://www.facebook.com/carlos.espinosa.56");
				//Profile profile = new Profile(uri);

				// Facebook Email address
				GraphRequest request = GraphRequest.newMeRequest(
						loginResult.getAccessToken(),
						new GraphRequest.GraphJSONObjectCallback() {
							@Override
							public void onCompleted(
									JSONObject object,
									GraphResponse response) {
								Log.v("LoginActivity Response ", response.toString());

								try {
									Name = object.getString("name");

									FElink = object.getString("link");
									sex = object.getString("gender");
									city = object.getString("location");

									host_name.setText(Name);
									host_sex.setText(sex);
									host_city.setText(city);

									Log.v("link = ", " " + FElink);
									Log.v("name = ", " " + Name);
									Log.v("gender = ", " " + sex);
									Log.v("location = ", " " + city);
									Toast.makeText(getApplicationContext(), "Name " + Name, Toast.LENGTH_LONG).show();


								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
				Bundle parameters = new Bundle();
				parameters.putString("fields", "id,name,email,gender, birthday");
				request.setParameters(parameters);
				request.executeAsync();


			}

			@Override
			public void onCancel() {
				LoginManager.getInstance().logOut();

			}

			@Override
			public void onError(FacebookException e) {

			}
		};


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

	/**
	private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
		@Override
		public void onSuccess(LoginResult loginResult) {
			AccessToken accessToken = loginResult.getAccessToken();
			Profile profile = Profile.getCurrentProfile();

			// Facebook Email address
			GraphRequest request = GraphRequest.newMeRequest(
					loginResult.getAccessToken(),
					new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(
								JSONObject object,
								GraphResponse response) {
							Log.v("LoginActivity Response ", response.toString());

							try {
								Name = object.getString("name");

								FElink = object.getString("link");
								sex = object.getString("gender");
								city = object.getString("location");

								Log.v("link = ", " " + FElink);
								Log.v("name = ", " " + Name);
								Log.v("gender = ", " " + sex);
								Log.v("location = ", " " + city);
								Toast.makeText(getApplicationContext(), "Name " + Name, Toast.LENGTH_LONG).show();


							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
			Bundle parameters = new Bundle();
			parameters.putString("fields", "id,name,email,gender, birthday");
			request.setParameters(parameters);
			request.executZZ


		}

		@Override
		public void onCancel() {
			LoginManager.getInstance().logOut();

		}

		@Ove
		public void onError(FacebookException e) {

		}
	};
	**/
}
