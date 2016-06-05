package software_engineering.whatnow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import software_engineering.whatnow.firebase_stuff.Constants;

public class HostProfileActivity extends AppCompatActivity implements ValueEventListener {
	private TextView hostName;
	private TextView address;
	private TextView phone;
	private TextView website;
	private TextView tripadvisor;
	private TextView facebook;
	private TextView yelp;
	private RelativeLayout websiteLayout;
	private RelativeLayout tripadvisorLayout;
	private RelativeLayout facebookLayout;
	private RelativeLayout yelpLayout;
	private ImageView mainPicture;
	private String encodedEmail;
	private String imageAsString;
	private FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_profile);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		encodedEmail = getIntent().getStringExtra("encodedEmail");
		if(encodedEmail == null || encodedEmail.equals("")){
			Toast.makeText(HostProfileActivity.this, "Error retrieving Host info...", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String toTrip = tripadvisor.getText().toString();
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(toTrip));
				startActivity(i);
			}
		});

		//MapView mapView = (MapView) findViewById(R.id.mapview);

		hostName = (TextView) findViewById(R.id.host_name);
		address = (TextView) findViewById(R.id.host_address);
		phone = (TextView) findViewById(R.id.host_phone);
		website = (TextView) findViewById(R.id.host_website);
		tripadvisor = (TextView) findViewById(R.id.host_tripadvisor);
		facebook = (TextView) findViewById(R.id.host_facebook);
		yelp = (TextView) findViewById(R.id.host_yelp);

		mainPicture = (ImageView) findViewById(R.id.host_q_image);

		Firebase hostFirebase = new Firebase(Constants.HOSTS_URL + encodedEmail);
		hostFirebase.addListenerForSingleValueEvent(this);

		websiteLayout = (RelativeLayout) findViewById(R.id.host_website_layout);
		facebookLayout = (RelativeLayout) findViewById(R.id.host_facebook_layout);
		tripadvisorLayout = (RelativeLayout) findViewById(R.id.host_tripadvisor_layout);
		yelpLayout = (RelativeLayout) findViewById(R.id.host_yelp_layout);
	}


	public void searchMapHost(View view){
		String toMaps = "https://www.google.com/maps/place/" + address.getText();
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(toMaps));
		startActivity(i);
	}

	public void openLink(View view){
		String link = ((TextView) view).getText().toString();
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
		startActivity(i);
	}

	public void call(View view){
		//
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		try{
			Map<String, String> map = dataSnapshot.getValue(HashMap.class);
			Log.wtf("HOST PROFILE", map.toString());
			hostName.setText(map.get("host_name").toString());
			address.setText(map.get("host_address").toString());
			phone.setText(map.get("host_phone").toString());

			imageAsString = map.get("host_picture".toString());
			try {
				byte[] imageAsBytes = Base64.decode(imageAsString, Base64.DEFAULT);
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
				mainPicture.setImageBitmap(bitmap);
			}catch(Exception e){
				// no picture
			}

			try {
				website.setText(map.get("host_website").toString());
			}catch(Exception e){
				websiteLayout.setVisibility(View.GONE);
				// no website
			}
			try {
				facebook.setText(map.get("host_facebook").toString());
			}catch(Exception e){
				// no facebook
				facebookLayout.setVisibility(View.GONE);
			}
			try {
				yelp.setText(map.get("host_yelp").toString());
			}catch(Exception e){
				// no yelp
				yelpLayout.setVisibility(View.GONE);
			}
			try {
				tripadvisor.setText(map.get("host_tripadvisor").toString());
			}catch(Exception e){
				fab.setVisibility(View.INVISIBLE);
				// no tripadvisor
				tripadvisorLayout.setVisibility(View.GONE);
			}
			Log.wtf("HOST PROFILE", "got stuff from Firebase");
		}catch (Exception e){
			Log.wtf("HOST PROFILE", e.getMessage());
		}

	}

	@Override
	public void onCancelled(FirebaseError firebaseError) {

	}
}
