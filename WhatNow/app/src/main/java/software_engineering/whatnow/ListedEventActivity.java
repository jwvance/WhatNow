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
import android.provider.MediaStore;
import android.provider.Settings;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


	private static final int RESULT_LOAD_IMG = 1;
//	private Integer imagesGal[];
	private String imagePath;
	private Bitmap imageG;
	private String imgArr[];
	private String galleryString;
	private boolean onGalleryclick;
	public	ArrayList<Bitmap> galArray;
	private	ArrayList<String> strArray;
	private ArrayList<String> loadImages;
	private LinearLayout imageGallery;

	private byte[] byteArray;
	private byte[] imageAsBytes;
	private byte[] galleryAsBytes;
	private String imageVal;
	private String keyval;


	ImageView lastClicked = null;
	boolean isImageFit;
	private Firebase mRef;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Firebase.setAndroidContext(this);
		mRef = new Firebase(Constants.DATABASE_URL);

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

			imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			image.setImageBitmap(bitmap);

			image.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					Log.wtf("IMAGE click ", "on picture");
					Intent intent = new Intent(ListedEventActivity.this, GalleryActivity.class);
					intent.putExtra("image", imageAsBytes);
					startActivity(intent);
				}
			});
//			Log.wtf("event id", eventID+"");

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

		galArray = new ArrayList<Bitmap>(100);
		strArray = new ArrayList<String>(100);
		loadImages = new ArrayList<String>(5);

		//hide scroll bar
		HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);

		//check and load gallery images
		Firebase eventRef = mRef.child("gallery");
//		Log.wtf("image firebase", eventRef.toString());

		//from firebase get gallery:### == eventID
		eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot child : dataSnapshot.getChildren()) {
					//key should be the eventID, val is image String
					keyval = child.getKey();
					if (Integer.parseInt(keyval) == eventID) {
						Log.wtf("KEY", keyval);
//						Log.wtf("key is", "same");

						for (DataSnapshot c : child.getChildren()) {
							imageVal = c.getValue().toString();
//							Log.wtf("IMAGE ARR", imageVal);
							galleryAsBytes = Base64.decode(imageVal, Base64.DEFAULT);
							Bitmap bitmapG = BitmapFactory.decodeByteArray(galleryAsBytes, 0, galleryAsBytes.length);
							imageGallery.addView(getImages(bitmapG, galleryAsBytes));
						}
					} else {
						//else don't download anything from firebase
//						Log.wtf("key is", "NOT same");
					}
				}
			}
			@Override
			public void onCancelled (FirebaseError firebaseError){
				//
			}
		});
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

	public void chooseImageGal(View v){
		onGalleryclick = false;
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
//				eventRef.child(Integer.toString(eventID)).setValue(strArray);

				//check if there are images already in the database
				//append else just add
				eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for (DataSnapshot child : dataSnapshot.getChildren()) {
							Log.wtf("eventID", eventID+"");
							if (Integer.parseInt(child.getKey()) == eventID) {
								if (child.exists()) {
									Log.wtf("child", "EXISTS");
									for (DataSnapshot c : child.getChildren()) {
										//load images from database into array
										loadImages.add(c.getValue().toString());
									}
								}
									//add selected image into array
									loadImages.add(galleryString);
									Log.wtf("loaded images", String.valueOf(loadImages.size()));
									//overwrite values if images exist
									eventRef.child(Integer.toString(eventID)).setValue(loadImages);
									loadImages.clear();
								break;
							}
							break;
						}
					}
					@Override
					public void onCancelled(FirebaseError firebaseError) {

					}
				});

				Log.wtf("eventID 2", eventID+"");

				eventRef.child(Integer.toString(eventID)).setValue(strArray);



				//add images to gallery
				galArray.add(imageG);
//				Log.wtf("IMAGE Array ", galArray.toString());
				for(Bitmap image: galArray){
					imageGallery.addView(getImages(image, byteArray));
				}

				Log.wtf("eventID 3", eventID+"");

				//clear array
				galArray.clear();
				((Button) findViewById(R.id.choose_gallery)).setText("Add image");
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
			}
		}
	}

	//Add bitmap image to imageview, onclick call new activity
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
				Log.wtf("IMAGE click ", "on picture");
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

		//png is lossless, will not compress
//		if (type.equals("image/png")) {
//			srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//		} else

		if (type.equals("image/jpg") || type.equals("image/jpeg")) {
			srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		}
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
	}
}
