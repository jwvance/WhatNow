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

import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
<<<<<<< HEAD
import android.support.design.widget.FloatingActionButton;
=======
import android.provider.MediaStore;
import android.provider.Settings;
>>>>>>> e9f754d7115dc1fb48dcb667d6456e40e624dc35
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

import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

<<<<<<< HEAD
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
=======
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Text;
>>>>>>> e9f754d7115dc1fb48dcb667d6456e40e624dc35

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
	private TextView title;
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
	private boolean participatedAlready;


	private static final int RESULT_LOAD_IMG = 1;
	private String imagePath;
	private Bitmap imageG;
	private String galleryString;
	public	ArrayList<Bitmap> galArray;
	private	ArrayList<String> strArray;
	private ArrayList<String> loadImages;
	private LinearLayout imageGallery;

	private byte[] byteArray;
	private byte[] imageAsBytes;
	private byte[] galleryAsBytes;
	private String imageVal;

	private Firebase mRef;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Firebase.setAndroidContext(this);
		mRef = new Firebase("https://ucscwhatnow.firebaseio.com");

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
		//participants = (TextView) findViewById(R.id.listed_event_participants);
		address = (TextView) findViewById(R.id.listed_event_address);
		distance = (TextView) findViewById(R.id.listed_event_distance);
		image = (ImageView) findViewById(R.id.listed_event_image);
		imageGallery = (LinearLayout)findViewById(R.id.my_gallery);

		event = null;
		events = ((GlobalEvents) this.getApplication()).getEventList();
		for (Event currEvent : events) {
			if(currEvent.getId() == eventID) {
				event = currEvent;
				break;
			}
		}

		if(event != null) {
<<<<<<< HEAD
			canParticipate = true;
			participatedAlready = false;
			key = event.getKey();
			preferences = PreferenceManager.getDefaultSharedPreferences(this);
			userEmail = preferences.getString(Constants.KEY_GOOGLE_EMAIL, "");
			Log.wtf("KEYS CREATING PARTICIPANTS", key);
			firebaseEventParticipants = new Firebase(Constants.DATABASE_URL + "/" + key + "/participants");
			Query query = (new Firebase(Constants.DATABASE_URL)).orderByKey().equalTo(key);
			query.addChildEventListener(this);
			/*firebaseEventParticipants.addChildEventListener(this);*/
			partecipations = preferences.getInt("user_partecipations", 0);
			firebaseEventUserParticipations = new Firebase(Constants.USERS_URL + Utils.encodeEmail(userEmail) + "/partecipations/");
			firebaseEventUserParticipations.addListenerForSingleValueEvent(this);

			this.setTitle(event.getName());
=======
			this.setTitle("");
			title.setText(event.getName());
>>>>>>> e9f754d7115dc1fb48dcb667d6456e40e624dc35
			description.setText(event.getDescription());
			//String categoryS = event.getCategory().getName().toLowerCase();
			//category.setText(categoryS.substring(0, 1).toUpperCase() + categoryS.substring(1));
			host.setText(event.getHost().getName());
			host.setTextColor(Color.parseColor("#33a0ff"));
			date.setText(event.getDateString());	//ADD multi date
			times.setText(event.getStartTime() + " - " + event.getEndTime());
			int p = event.getNumberOfGuests();
			participants.setText(event.getNumberOfGuests() + " participants");
			address.setText(event.getLocation());
			address.setTextColor(Color.parseColor("#33a0ff"));
			distance.setText(event.getDistance() + " away");

			imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			image.setImageBitmap(bitmap);

<<<<<<< HEAD
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

=======
			image.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(ListedEventActivity.this, GalleryActivity.class);
					intent.putExtra("image", imageAsBytes);
					startActivity(intent);
				}
			});
>>>>>>> e9f754d7115dc1fb48dcb667d6456e40e624dc35
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
		recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(recyclerAdapter);

<<<<<<< HEAD
		((FloatingActionButton) findViewById(R.id.listed_event_fab)).setOnClickListener(this);
=======
		galArray = new ArrayList<Bitmap>(5);
		strArray = new ArrayList<String>(5);
		loadImages = new ArrayList<String>(5);

		//hide scroll bar
		HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);

		//check and load gallery images
		Firebase eventRef = mRef.child("gallery");

		//from firebase get gallery:### == event key
		eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot child : dataSnapshot.getChildren()) {
					//key should be the event key, val is image String
					if(child.getKey().equals(event.getKey())) {
						for (DataSnapshot c : child.getChildren()) {
							imageVal = c.getValue().toString();
							galleryAsBytes = Base64.decode(imageVal, Base64.DEFAULT);
							Bitmap bitmapG = BitmapFactory.decodeByteArray(galleryAsBytes, 0, galleryAsBytes.length);
							imageGallery.addView(getImages(bitmapG, galleryAsBytes));
						}
					} else {
						//else don't download anything from firebase
					}
				}
			}
			@Override
			public void onCancelled (FirebaseError firebaseError){
				Toast.makeText(ListedEventActivity.this, firebaseError.toString(), Toast.LENGTH_SHORT).show();
			}
		});
>>>>>>> e9f754d7115dc1fb48dcb667d6456e40e624dc35
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

	public void chooseImageGal(View v){
		// Create intent to Open Image applications like Gallery, Google Photos
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// Start the Intent
		startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null && data.getData() != null) {

			Uri uri = data.getData();

			try {
				//add images to firebase and then show them on imageview
				String boh= "";
				grantUriPermission(boh, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

				imagePath = getRealPathFromURI(this, uri);
				imageG = scaleImage(this, uri);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				imageG.compress(Bitmap.CompressFormat.JPEG, 100, stream);

				byteArray = stream.toByteArray();
				galleryString = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
				strArray.add(galleryString);

				// save to firebase
				final Firebase eventRef = mRef.child("gallery");

				//check if there are images already in the database
				//append else just add
				eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for (DataSnapshot child : dataSnapshot.getChildren()) {
							if(child.getKey().equals(event.getKey())) {
								for (DataSnapshot c : child.getChildren()) {
									//load images from database into array
									loadImages.add(c.getValue().toString());
								}
								//add selected image into array
								loadImages.add(galleryString);
								//overwrite values if images exist into firebase
								eventRef.child(event.getKey()).setValue(loadImages);
								loadImages.clear();
							}
							else {
								//add to no other event
							}
						}
					}
					@Override
					public void onCancelled(FirebaseError firebaseError) {
						Toast.makeText(ListedEventActivity.this, "Something went wrong: " + firebaseError.toString(), Toast.LENGTH_LONG).show();
					}
				});

				//add image to firebase
				eventRef.child(event.getKey()).setValue(strArray);
				strArray.clear();

				//add images to gallery
				galArray.add(imageG);
				for(Bitmap image: galArray){
					imageGallery.addView(getImages(image, byteArray));
				}

				//clear array
				galArray.clear();
				((Button) findViewById(R.id.choose_gallery)).setText("Add image");
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
			}
		}
	}

	//Add bitmap image to imageview, onclick call new fullscreen activity
	private View getImages(Bitmap image, final byte[] arr) {
		final ImageView imageView = new ImageView(getApplicationContext());

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0,0,0,0);
		imageView.setLayoutParams(lp);
		imageView.setPadding(10,0,10,0);

		imageView.setImageBitmap(image);
		imageView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				//onclick pass byte array using intent for the new activity call
				Intent intent = new Intent(ListedEventActivity.this, GalleryActivity.class);
				intent.putExtra("image", arr);
				startActivity(intent);
			}
		});
		return imageView;
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

<<<<<<< HEAD
	@Override
	public void onClick(View v) {
		if(canParticipate && !participatedAlready) {
			try {
				event.increaseGuests();

				Log.wtf("PARTECIPATIONS for " + userEmail + " #", "" + partecipations);
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

				participatedAlready = true;

				Toast.makeText(ListedEventActivity.this, "Participation added!", Toast.LENGTH_SHORT).show();

				//FIX THIS, need updated value
				//participants.setText((Integer.parseInt(participants.getText().toString()) + 1) + "");
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
			//FIX THIS -> there is no eventMap because just first one, query?

			eventMap = dataSnapshot.getValue(HashMap.class);
			participantsMap = (HashMap) eventMap.get("participants");
			if(participantsMap != null && participantsMap.containsValue(Utils.encodeEmail(userEmail))){
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
			participantsN = participantsMap.size();
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

=======
	public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
		InputStream is = context.getContentResolver().openInputStream(photoUri);
		BitmapFactory.Options dbo = new BitmapFactory.Options();
		dbo.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, dbo);
		is.close();

		int rotatedWidth, rotatedHeight;
		int orientation = 0;

		rotatedWidth = dbo.outWidth;
		rotatedHeight = dbo.outHeight;

		Bitmap srcBitmap;
		is = context.getContentResolver().openInputStream(photoUri);
		if (rotatedWidth > 400 || rotatedHeight > 400) {
			float widthRatio = ((float) rotatedWidth) / ((float) 400);
			float heightRatio = ((float) rotatedHeight) / ((float) 400);
			float maxRatio = Math.max(widthRatio, heightRatio);

			// Create the bitmap from file
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = (int) maxRatio;
			srcBitmap = BitmapFactory.decodeStream(is, null, options);
		} else {
			srcBitmap = BitmapFactory.decodeStream(is);
		}
		is.close();

    /*
     * if the orientation is not 0 (or -1, which means we don't know), we
     * have to do a rotation.
     */
		String type = context.getContentResolver().getType(photoUri);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();


		if (type.equals("image/png")) {
			srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		} else
		if (type.equals("image/jpg") || type.equals("image/jpeg")) {
			srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		}
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
	}
>>>>>>> e9f754d7115dc1fb48dcb667d6456e40e624dc35
}
