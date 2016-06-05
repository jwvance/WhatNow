
/**
 * Created by Jason on 5/23/2016.
 */

package software_engineering.whatnow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import software_engineering.whatnow.firebase_stuff.Constants;
import software_engineering.whatnow.utils.Utils;

public class EditEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener /*DialogInterface.OnClickListener*/{

	private static final int RESULT_LOAD_IMG = 1;
	private EditText eventName;
	private EditText description;
	private EditText location;
	private EditText hostName;
	private EditText phone;
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

		setContentView(R.layout.activity_add_event);
		Toolbar toolbar = (Toolbar) findViewById(R.id.new_event_toolbar);
		setSupportActionBar(toolbar);

		findViewById(R.id.fab_new_event).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editEvent(findViewById(android.R.id.content));
			}
		});


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

		EditText oldName = (EditText) findViewById(R.id.new_event_name);
		oldName.setText(event.getName());
		eventName.setText(event.getName());
		name = event.getName();

		EditText oldDescription = (EditText) findViewById(R.id.new_event_description);
		oldDescription.setText(event.getDescription());
		description.setText(event.getDescription());

		EditText oldHost = (EditText) findViewById(R.id.new_event_host);
		oldHost.setText(event.getHost().getName());
		hostName.setText(event.getHost().getName());

		EditText oldAddress = (EditText) findViewById(R.id.new_event_location);
		oldAddress.setText(event.getLocation());
		location.setText(event.getLocation());

		ImageView oldImage = (ImageView) findViewById(R.id.new_event_image);
		byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		oldImage.setImageBitmap(bitmap);
		imageAsString = event.getImageAsString();

		TextView chooseImage = (TextView) findViewById(R.id.choose_image);
		chooseImage.setText("Edit Image");

		final EditEventActivity editEventActivity = this;
		editEventActivity.setTitle("");

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

		final EditEventActivity editEventActivity = this;
		eventName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				name = s.toString();
				editEventActivity.setTitle(name);
				//	((TextView) findViewById(R.id.textView)).setText(s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				name = s.toString();
				editEventActivity.setTitle(name);
				//	((TextView) findViewById(R.id.textView)).setText(s);
			}
		});
	}

	public void editEvent(View v) {
		if(name == null || name.equals("")){
			Toast.makeText(EditEventActivity.this, "Choose a name first!", Toast.LENGTH_SHORT).show();
			return;
		}

		if ((description == null || description.getText().toString().equals("")) ||
				(location == null || location.getText().toString().equals("")) ||
				(hostName == null || hostName.getText().toString().equals("")) ||
				(phone == null || phone.getText().toString().equals(""))) {
			Toast.makeText(EditEventActivity.this, "Fill every field first!", Toast.LENGTH_SHORT).show();
			return;
		}

		/*for (int i = 1; i < tv.length; i++) {
			if(tv[i] == null || tv[i].getText().equals("")) {
				Toast.makeText(AddEventActivity.this, "Fill every field first!", Toast.LENGTH_SHORT).show();
				return;
			}
		}*/

		if(imageAsString == null || imageAsString.equals("")){
			Toast.makeText(EditEventActivity.this, "Select a picture first!", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(EditEventActivity.this, "Start date/time must be before end time!", Toast.LENGTH_SHORT).show();
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

		//save to firebase
		Firebase.setAndroidContext(this);
		Firebase mRef = new Firebase(Constants.DATABASE_URL);
//		Firebase eventRef = mRef.child("event");
//		eventRef.child(Integer.toString(eventId)).setValue(event);

		Log.wtf("edit event key", newEvent.getKey());

		//update item
		mRef.child(newEvent.getKey()).setValue(newEvent);

		//return to previous activity
		Toast.makeText(this, "Event Listing Updated!", Toast.LENGTH_LONG).show();

		//return to previous activity
		finish();
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

				ImageView imageView = (ImageView) findViewById(R.id.new_event_image);
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
