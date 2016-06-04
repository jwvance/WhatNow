/* 	CLASS DESCRIPTION:
	-	This is the activity that allows the user to add an event.
	-	The related layout file is activity_add_event that contains content_add_event.
	-	It implements three interfaces rispectively for: DatePicker, TimePicker and
		the Dialog to get an image from the gallery.
	-	Inside OnCreate, except for the usual layout setting, there is the setting of
		default dates and times.
	-	initialize is simply to get a reference to the various EditText (Francesco made
		that) and the listener part doesn't work properly.
	-	addEvent is to add an Event to Firebase, after getting all the info from the
		fields and creating the Event obj.
	-	saveEvents is the method used to save the events to the Preferences, I might
		reuse that to temporarly save them (instead of getting everything from Firebase)
	-	onDateSet is called at the click of OK in the DatePicker, it saves the date
	-	chooseDate is called at the click of the date buttons in the activity,
		it launches the DatePicker
	-	chooseTime is called at the click of the time buttons in the activity,
		it launches the TimePicker
	-	onTimeSet is called at the click of OK in the TimePicker, it saves the time
	-	chooseCategory is called at the click of the choose category button,
		it launches a Dialog to choose the Category
	-	onClick is called when a category is chosen, it saves it
	-	chooseImage is called at the click of the choose image button,
		it launches the gallery
	-	onActivityResult is called automatically when an image is chosen from the
		gallery, it puts it in the top part of the activity layout and saves the path;
		it's going to be uploaded on Firebase
	-	getRealPathFromURI is simply to save the real path instead of a weird thumbnail
		path that doesn't bring you anywhere
*/

package software_engineering.whatnow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.utilities.Base64;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;

import software_engineering.whatnow.firebase_stuff.Constants;
import software_engineering.whatnow.utils.Utils;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener /*DialogInterface.OnClickListener*/{

	private static final int RESULT_LOAD_IMG = 1;
	private EditText[] tv = new EditText[4];
	private EditText eventName;
	private EditText description;
	private EditText location;
	private EditText hostName;
	private EditText phone;
	//private DatePick;er dp;
	//private TimePicker tp;
	private Event event;
	private ArrayList<Event> events;
	private String name;

	//Stuff
	public static Context conEvent;

	private final static int MAX_IMAGE_DIMENSION=400;
	private Button initialDate;
	private Button finalDate;
	private Button initialTime;
	private Button finalTime;
	private int iYear;
	private int iMonth;
	private int iDay;
	private int iHour;
	private int iMinute;
	private boolean initial;
	private int fYear;
	private int fMonth;
	private int fDay;
	private int fHour;
	private int fMinute;
	private String category;
	private String[] categories = {"BARS","CLUBS","FOOD","SHOPS","OTHER"};
	private String imagePath;
	private Bitmap image;
	private String imageAsString;

	private Spinner spinner;
	ArrayAdapter<CharSequence> adapter;

	//private String databaseURL;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		Toolbar toolbar = (Toolbar) findViewById(R.id.new_event_toolbar);
		setSupportActionBar(toolbar);

		AddEventActivity.conEvent = this;
		initialize();

		Calendar mcurrentDate = Calendar.getInstance();

		initialDate = (Button) findViewById(R.id.new_event_initialDate);
		iYear = mcurrentDate.get(Calendar.YEAR);
		iMonth = 1 + mcurrentDate.get(Calendar.MONTH);
		iDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
		initialDate.setText(Event.getDateString(iYear,iMonth,iDay));
		initialTime = (Button) findViewById(R.id.new_event_initialTime);
		iHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
		iMinute = 0;
		initialTime.setText(Event.getTimeString(iHour, iMinute));

		mcurrentDate.add(Calendar.HOUR_OF_DAY, 2);
		finalDate = (Button) findViewById(R.id.new_event_finalDate);
		fYear = mcurrentDate.get(Calendar.YEAR);
		fMonth = 1 + mcurrentDate.get(Calendar.MONTH);
		fDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
		finalDate.setText(Event.getDateString(fYear,fMonth,fDay));
		finalTime = (Button) findViewById(R.id.new_event_finalTime);
		fHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
		fMinute = 0;
		finalTime.setText(Event.getTimeString(fHour, fMinute));

		category = null;

		//initialize spinner
		spinner = (Spinner)findViewById(R.id.spinner);
		adapter = ArrayAdapter.createFromResource(this,R.array.categories, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			  @Override
			  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				  category = (String)parent.getItemAtPosition((position));
			  }

			  @Override
			  public void onNothingSelected(AdapterView<?> parent) {
				  ;
			  }
		});
	}

	public void chooseCategory(View v){
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Context context = getBaseContext();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//nothing
			}
		});
	}

	public static ArrayList<Event> loadEvents(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		ArrayList<Event> output = new ArrayList<Event>();
		int size = preferences.getInt("EventsArray_size", 0);
		StringTokenizer st;
		for (int i = 0; i < size; i++) {
			st = new StringTokenizer(preferences.getString("EventArray_" + i, null), ":::***:::***:::");
			try{
				output.add(new Event(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
						Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), st.nextToken(),
						new Host(st.nextToken(), st.nextToken()), st.nextToken(), st.nextToken(), new Category(st.nextToken()),
						Long.parseLong(st.nextToken()), st.nextToken(), st.nextToken(), false, 0/*, Integer.parseInt(st.nextToken())*/));
			}catch(Exception e){
				Log.wtf("LOAD", "Problem loading events: " + e.getMessage());
			}
		}
		return output;
	}

	public static void appendEvent(Context context, Event event) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

		int arraySize = preferences.getInt("EventsArray_size", 0);

		editor.putString("EventArray_" + (arraySize), event.toString());
		arraySize++;
		editor.putInt("EventsArray_size", arraySize);

		editor.apply();
	}

	//refreshes local array for initial app startup
	public static void deleteEvents(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("EventsArray_size", 0);
		editor.apply();

	}

	public static void saveLocalEvent(Context context, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

	}

		//when event is removed from server, delete it locally as well
	public static void deleteLocalEvent(Context context, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

		//load event list
		ArrayList<Event> events;
		events = AddEventActivity.loadEvents(context);

		Log.wtf("SIZE", Integer.toString(events.size()));

		//find the local event, remove it
		for(Event currEvent : events) {
			if(key.equals(currEvent.getKey())) {
				Log.wtf("removed:", currEvent.getName());
				events.remove(currEvent);
			}
		}

		//delete all events
		editor.putInt("EventsArray_size", 0);

		//resave new array
		int arraySize = 0;

		Log.wtf("SIZE", Integer.toString(events.size()));
		for(Event currEvent : events) {
			Log.wtf("re-added", currEvent.getName());
			editor.putString("EventArray_" + (arraySize), currEvent.toString());
			arraySize++;
			editor.putInt("EventsArray_size", arraySize);
		}

		editor.apply();
	}

	private void initialize(){
		eventName = (EditText) findViewById(R.id.new_event_name);
		description = (EditText) findViewById(R.id.new_event_description);
		location = (EditText) findViewById(R.id.new_event_location);
		hostName = (EditText) findViewById(R.id.new_event_host);
		phone = (EditText) findViewById(R.id.new_event_phone);
		//tv[4]= (EditText) findViewById(R.id.editText5);
		//dp = (DatePicker) findViewById(R.id.datePicker);
		//tp = (TimePicker) findViewById(R.id.timePicker);
		//addEvent = (Button) findViewById(R.id.button);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		location.setText(preferences.getString("host_address", ""));
		hostName.setText(preferences.getString("host_name", ""));
		phone.setText(preferences.getString("host_phone", ""));

		final AddEventActivity addEventActivity = this;
		eventName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				name = s.toString();
				addEventActivity.setTitle(name);
				//	((TextView) findViewById(R.id.textView)).setText(s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				name = s.toString();
				addEventActivity.setTitle(name);
				//	((TextView) findViewById(R.id.textView)).setText(s);
			}
		});
	}

	public void addEvent(View v) {
		if(name == null || name.equals("")){
			Toast.makeText(AddEventActivity.this, "Choose a name first!", Toast.LENGTH_SHORT).show();
			return;
		}

		if ((description == null || description.getText().toString().equals("")) ||
				(location == null || location.getText().toString().equals("")) ||
				(hostName == null || hostName.getText().toString().equals("")) ||
				(phone == null || phone.getText().toString().equals(""))) {
			Toast.makeText(AddEventActivity.this, "Fill every field first!", Toast.LENGTH_SHORT).show();
			return;
		}

		/*for (int i = 1; i < tv.length; i++) {
			if(tv[i] == null || tv[i].getText().equals("")) {
				Toast.makeText(AddEventActivity.this, "Fill every field first!", Toast.LENGTH_SHORT).show();
				return;
			}
		}*/

		if(imageAsString == null || imageAsString.equals("")){
			Toast.makeText(AddEventActivity.this, "Select a picture first!", Toast.LENGTH_SHORT).show();
			return;
		}

	/*	if(iCalendar.after(fCalendar)){
			Toast.makeText(AddEventActivity.this, "Start date must be before end date!", Toast.LENGTH_SHORT).show();
			return;
		}*/
		Calendar iCalendar = Calendar.getInstance();
		iCalendar.set(iYear, iMonth, iDay, iHour, iMinute);
		iCalendar.set(Calendar.SECOND, 0);
		iCalendar.set(Calendar.MILLISECOND, 0);
		Calendar fCalendar = Calendar.getInstance();
		fCalendar.set(fYear, fMonth, fDay, fHour, fMinute);
		fCalendar.set(Calendar.SECOND, 0);
		fCalendar.set(Calendar.MILLISECOND, 0);

		if(!iCalendar.before(fCalendar)){
			Toast.makeText(AddEventActivity.this, "Start date/time must be before end time!", Toast.LENGTH_SHORT).show();
			return;
		}

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Host host = new Host(hostName.getText().toString());

		String email = preferences.getString(Constants.KEY_GOOGLE_EMAIL, null);
		if(email == null) {
			email = preferences.getString(Constants.KEY_ENCODED_EMAIL, null);
			email = Utils.decodeEmail(email);
		}
		if(email != null) {
			host.setBusinessEmail(email);
			Log.wtf("HOST ADD EVENT", email);
		}else
			Log.wtf("HOST ADD EVENT", "email is NULL!!");

		event = new Event(new Random().nextInt(1000), 0, iHour, iMinute, fHour, fMinute,
				location.getText().toString(), host, name,
				description.getText().toString(), new Category(/*tv[4].getText().toString()*/category),
				iCalendar.getTimeInMillis(), imageAsString, "", false, 0/*, 0*/);

		//add to arraylist
		//events.add(event);

		//save to preferences
		//appendEvent(getApplicationContext(), event);

		//save to firebase
		Firebase.setAndroidContext(this);
		new Firebase(Constants.DATABASE_URL).push().setValue(event);

		//return to previous activity
		finish();
	}

	/*private void reinitializeUI() {
		for (int i=0; i<5; i++){
			tv[i].setText("");
		}
	}*/

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		if(initial){
			iMonth = monthOfYear + 1;
			iDay = dayOfMonth;
			iYear = year;
			initialDate.setText(Event.getDateString(iYear,iMonth,iDay));
			fMonth = monthOfYear + 1;
			fDay = dayOfMonth;
			fYear = year;
			finalDate.setText(Event.getDateString(fYear,fMonth,fDay));
		}else{
			fMonth = monthOfYear + 1;
			fDay = dayOfMonth;
			fYear = year;
			finalDate.setText(Event.getDateString(fYear,fMonth,fDay));
		}
	}

	public void chooseDate(View v) {
		DatePickerDialog mDatePicker;
		if(v.getId() == R.id.new_event_initialDate){
			mDatePicker = new DatePickerDialog(this, this, iYear, iMonth, iDay);
			initial = true;
			mDatePicker.setTitle("Starting Date:");
		}else{
			mDatePicker = new DatePickerDialog(this, this, fYear, fMonth, fDay);
			initial = false;
			mDatePicker.setTitle("Ending Date:");
		}
		mDatePicker.show();
	}

	public void chooseTime(View v) {
		TimePickerDialog mTimePicker;
		if (v.getId() == R.id.new_event_initialTime){
			mTimePicker = new TimePickerDialog(this, this, iHour, iMinute, false);
			mTimePicker.setTitle("Starting Time:");
			initial = true;
		}else{
			mTimePicker = new TimePickerDialog(this, this, fHour, fMinute, false);
			mTimePicker.setTitle("Ending Time:");
			initial = false;
		}
		mTimePicker.show();
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if(initial){
			iHour = hourOfDay;
			iMinute = minute;
			initialTime.setText(Event.getTimeString(iHour,iMinute));
			fHour = hourOfDay + 2;
			fMinute = minute;
			finalTime.setText(Event.getTimeString(fHour,fMinute));
		}else{
			fHour = hourOfDay;
			fMinute = minute;
			finalTime.setText(Event.getTimeString(fHour,fMinute));
		}
	}

	public void chooseImage(View v){
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

				//Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				// Log.d(TAG, String.valueOf(bitmap));


				imagePath = getRealPathFromURI(this, uri);
				image = scaleImage(this, uri);
				Log.wtf("IMAGE", imagePath);


				/*Bitmap bmp = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				bmp.recycle();
				byte[] byteArray = stream.toByteArray();
				String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);*/

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, stream);



				byte[] byteArray = stream.toByteArray();
				imageAsString = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);

				ImageView imageView = (ImageView) findViewById(R.id.new_event_image);
				imageView.setImageBitmap(image);


				//	imageView.setVisibility(View.VISIBLE);
				//	imageView.bringToFront();
				//	findViewById(R.id.choose_image).setVisibility(View.INVISIBLE);
				//	((TextView) findViewById(R.id.textViewPic)).setText("Click image to change it");
				((Button) findViewById(R.id.choose_image)).setText("Change image");
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
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
		if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
			float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
			float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
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
		} else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
			srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		}
		byte[] bMapArray = baos.toByteArray();
		baos.close();
		return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
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

}
