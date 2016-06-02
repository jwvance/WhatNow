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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import software_engineering.whatnow.firebase_stuff.Constants;

public class EditEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener /*DialogInterface.OnClickListener*/{

	private static final int RESULT_LOAD_IMG = 1;
	private EditText[] tv = new EditText[4];
	private Event event;
	private Event newEvent;
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
	private String key;
	private String[] categories = {"BARS","CLUBS","FOOD","SHOPS","OTHER"};
	private String imagePath;
	private Bitmap image;
	private String imageAsString;

	private Spinner spinner;
	ArrayAdapter<CharSequence> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		if(bundle.getString("KEY") != null){
			key = bundle.getString("KEY");
		}

		setContentView(R.layout.activity_edit_event);
		Toolbar toolbar = (Toolbar) findViewById(R.id.edit_event_toolbar);
		setSupportActionBar(toolbar);

		EditEventActivity.conEvent = this;
		initialize();

		//load events
		events = ((GlobalEvents) getApplication()).getEventList();

		for(Event currEvent : events) {
			if(key.equals(currEvent.getKey())) {
				event = currEvent;
				break;
			}
		}

		EditText oldName = (EditText) findViewById(R.id.edit_event_name);
		oldName.setText(event.getName());
		tv[0].setText(event.getName());
		name = event.getName();

		EditText oldDescription = (EditText) findViewById(R.id.edit_event_description);
		oldDescription.setText(event.getDescription());
		tv[1].setText(event.getDescription());

		EditText oldHost = (EditText) findViewById(R.id.edit_event_host);
		oldHost.setText(event.getHost().getName());
		tv[3].setText(event.getHost().getName());

		EditText oldAddress = (EditText) findViewById(R.id.edit_event_location);
		oldAddress.setText(event.getLocation());
		tv[2].setText(event.getLocation());

		ImageView oldImage = (ImageView) findViewById(R.id.edit_event_image);
		byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		oldImage.setImageBitmap(bitmap);
		imageAsString = event.getImageAsString();

		final EditEventActivity editEventActivity = this;
		editEventActivity.setTitle("");

		Calendar mcurrentDate = Calendar.getInstance();
		initialDate = (Button) findViewById(R.id.edit_event_initialDate);
		iYear = mcurrentDate.get(Calendar.YEAR);
		iMonth = 1 + mcurrentDate.get(Calendar.MONTH);
		iDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
		initialDate.setText(Event.getDateString(iYear,iMonth,iDay));
		initialTime = (Button) findViewById(R.id.edit_event_initialTime);
		iHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
		iMinute = 0;
		initialTime.setText(Event.getTimeString(iHour, iMinute));

		mcurrentDate.add(Calendar.HOUR_OF_DAY, 2);
		finalDate = (Button) findViewById(R.id.edit_event_finalDate);
		fYear = mcurrentDate.get(Calendar.YEAR);
		fMonth = 1 + mcurrentDate.get(Calendar.MONTH);
		fDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
		finalDate.setText(Event.getDateString(fYear,fMonth,fDay));
		finalTime = (Button) findViewById(R.id.edit_event_finalTime);
		fHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
		fMinute = 0;
		finalTime.setText(Event.getTimeString(fHour, fMinute));

		category = event.getCategory().getName();

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

	private void initialize(){
		tv[0] = (EditText) findViewById(R.id.edit_event_name);
		tv[1] = (EditText) findViewById(R.id.edit_event_description);
		tv[2] = (EditText) findViewById(R.id.edit_event_location);
		tv[3] = (EditText) findViewById(R.id.edit_event_host);
	}

	public void editEvent(View v) {
		Firebase.setAndroidContext(this);
		Firebase firebase = new Firebase(Constants.DATABASE_URL);

		if(name == null || name.equals("")){
			Toast.makeText(EditEventActivity.this, "Choose a name first!", Toast.LENGTH_SHORT).show();
			return;
		}

		for (int i = 1; i < tv.length; i++) {
			if(tv[i] == null || tv[i].getText().equals("")) {
				Toast.makeText(EditEventActivity.this, "Fill every field first!", Toast.LENGTH_SHORT).show();
				return;
			}
		}

		if(imageAsString == null || imageAsString.equals("")){
			Toast.makeText(EditEventActivity.this, "Select a picture first!", Toast.LENGTH_SHORT).show();
			return;
		}

		Calendar iCalendar = Calendar.getInstance();
		iCalendar.set(iYear, iMonth, iDay);
		Calendar fCalendar = iCalendar;
		fCalendar.set(fYear, fMonth, fDay);

		if(!iCalendar.before(fCalendar) && (iHour > fHour || (iHour == fHour && iMinute >= fMinute))){
			Toast.makeText(EditEventActivity.this, "Start time must be before end time!", Toast.LENGTH_SHORT).show();
			return;
		}
		if(iCalendar.after(fCalendar)){
			Toast.makeText(EditEventActivity.this, "Start date must be before end date!", Toast.LENGTH_SHORT).show();
			return;
		}

		newEvent = new Event(new Random().nextInt(1000), iHour, iMinute, fHour, fMinute,
				tv[2].getText().toString(), new Host(tv[3].getText().toString()), tv[0].getText().toString(),
				tv[1].getText().toString(), new Category(/*tv[4].getText().toString()*/category),
				iCalendar.getTimeInMillis(), fCalendar.getTimeInMillis(), imageAsString, event.getKey(), false, 0);


		Log.wtf("key", newEvent.getKey());

		//remove from firebase
		firebase.child(newEvent.getKey()).removeValue();

		//upload to firebase
		firebase.push().setValue(newEvent);

		//return to previous activity
		Toast.makeText(this, "Event Listing Updated!", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, TabActivity.class);
		startActivity(intent);
	}

	private void reinitializeUI() {
		for (int i=0; i<5; i++){
			tv[i].setText("");
		}
	}

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
		if(v.getId() == R.id.edit_event_initialDate){
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
		if (v.getId() == R.id.edit_event_initialTime){
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
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
				image = scaleImage(this, uri);
				Log.wtf("IMAGE", imagePath);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

				byte[] byteArray = stream.toByteArray();
				imageAsString = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);

				ImageView imageView = (ImageView) findViewById(R.id.edit_event_image);
				imageView.setImageBitmap(image);

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
