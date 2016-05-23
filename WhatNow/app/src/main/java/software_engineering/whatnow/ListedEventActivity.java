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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	private static final int RESULT_LOAD_IMG = 1;
//	private Integer imagesGal[];
	private String imagePath;
	private Bitmap imageG;
	private String imgArr[];
	private String imageAsString;
	private boolean onGalleryclick;
	private	ArrayList<Bitmap> galArray;
	private	ArrayList<ImageView> viewArray;

	ImageView lastClicked = null;
	boolean isImageFit;

	HorizontalScrollView hsv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listed_event);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEvent);
		setSupportActionBar(toolbar);

		galArray = new ArrayList<Bitmap>(100);
		viewArray = new ArrayList<ImageView>(100);

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
			distance.setText(event.getDistance() + "away");

			byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
			image.setImageBitmap(bitmap);
		}else{

		}

		//hide scroll bar
		HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		horizontalScrollView.setHorizontalScrollBarEnabled(false);

	}


	public void searchMap(View view){
		String toMaps = "https://www.google.com/maps/place/" + address.getText();
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(toMaps));
		startActivity(i);
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
				String boh= "";
				grantUriPermission(boh, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

				imagePath = getRealPathFromURI(this, uri);
				imageG = scaleImage(this, uri);
				Log.wtf("IMAGE", imagePath);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				imageG.compress(Bitmap.CompressFormat.JPEG, 100, stream);

				byte[] byteArray = stream.toByteArray();
				imageAsString = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);

				//add images to gallery
//				ImageView imageView = (ImageView) findViewById(R.id.image_display_gallery);
				galArray.add(imageG);
				Log.wtf("IMAGE Array ", galArray.toString());

				LinearLayout imageGallery = (LinearLayout)findViewById(R.id.my_gallery);
				for(Bitmap image: galArray){
					imageGallery.addView(getImages(image));
//					imageView.setImageBitmap(imageG);
				}
				//clear array
				galArray.clear();
				Log.wtf("IMAGE Array ", "clear");

				((Button) findViewById(R.id.choose_gallery)).setText("Add image");
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
			}
		}
	}

	private View getImages(Bitmap image) {
		final ImageView imageView = new ImageView(getApplicationContext());
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
//		params.addRule(RelativeLayout.BELOW, R.id.choose_gallery);
//		imageView.setLayoutParams(params);
//
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0,0,0,0);
		imageView.setLayoutParams(lp);
		imageView.setPadding(10,0,10,0);

		imageView.setImageBitmap(image);
		imageView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
					if(isImageFit) {
					isImageFit=false;
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					imageView.setLayoutParams(lp);
					imageView.setAdjustViewBounds(true);
				}else{
					isImageFit=true;
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
					imageView.setLayoutParams(lp);
					imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				}
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
