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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.utilities.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;

import software_engineering.whatnow.firebase_stuff.Constants;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnClickListener{

	private static final int RESULT_LOAD_IMG = 1;
	private EditText[] tv = new EditText[5];
	//private DatePicker dp;
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
	private String[] categories = {"BARS","CLUBS","FOOD","SHOPS","OTHERS"};
	private String imagePath;
	private Bitmap image;
	private String imageAsString;

	//private String databaseURL;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		Toolbar toolbar = (Toolbar) findViewById(R.id.new_event_toolbar);
		setSupportActionBar(toolbar);

		AddEventActivity.conEvent = this;
		initialize();

		events = loadEvents(this);

		Calendar mcurrentDate = Calendar.getInstance();

		initialDate = (Button) findViewById(R.id.new_event_initialDate);
		iYear = mcurrentDate.get(Calendar.YEAR);
		iMonth = mcurrentDate.get(Calendar.MONTH);
		iDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
		initialDate.setText(Event.getDateString(iYear,iMonth,iDay));
		initialTime = (Button) findViewById(R.id.new_event_initialTime);
		iHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
		iMinute = mcurrentDate.get(Calendar.MINUTE);
		initialTime.setText(Event.getTimeString(iHour, iMinute));

		mcurrentDate.add(Calendar.HOUR_OF_DAY, 2);
		finalDate = (Button) findViewById(R.id.new_event_finalDate);
		fYear = mcurrentDate.get(Calendar.YEAR);
		fMonth = mcurrentDate.get(Calendar.MONTH);
		fDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
		finalDate.setText(Event.getDateString(fYear,fMonth,fDay));
		finalTime = (Button) findViewById(R.id.new_event_finalTime);
		fHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
		fMinute = mcurrentDate.get(Calendar.MINUTE);
		finalTime.setText(Event.getTimeString(fHour, fMinute));

		category = null;

		//databaseURL = Constants.FIREBASE_URL + "/events";
	}

	public static ArrayList<Event> loadEvents(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

		ArrayList<Event> output = new ArrayList<Event>();
		int size = preferences.getInt("EventsArray_size", 0);
		StringTokenizer st;
		for (int i = 0; i < size; i++) {
			st = new StringTokenizer(preferences.getString("EventArray_" + i, null), ":::");
			try{
				output.add(new Event(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), st.nextToken(),
					new Host(st.nextToken()), st.nextToken(), st.nextToken(), new Category(st.nextToken()),
					Long.parseLong(st.nextToken()), st.nextToken(), false, 0));
			}catch(Exception e){
				Log.wtf("LOAD", "Problem loading events: " + e.getMessage());
			}
		}
		return output;
	}

	private void initialize(){
		tv[0]= (EditText) findViewById(R.id.new_event_name);
		tv[1]= (EditText) findViewById(R.id.new_event_description);
		tv[2]= (EditText) findViewById(R.id.new_event_location);
		tv[3]= (EditText) findViewById(R.id.new_event_host);
		//tv[4]= (EditText) findViewById(R.id.editText5);
		//dp = (DatePicker) findViewById(R.id.datePicker);
		//tp = (TimePicker) findViewById(R.id.timePicker);
		//addEvent = (Button) findViewById(R.id.button);
		final AddEventActivity addEventActivity = this;
		tv[0].addTextChangedListener(new TextWatcher() {
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
		Calendar calendar = Calendar.getInstance();
		calendar.set(iYear, iMonth, iDay);
		event = new Event(new Random().nextInt(1000), iHour, iMinute, fHour, fMinute,
				tv[2].getText().toString(), new Host(tv[3].getText().toString()), name,
				tv[1].getText().toString(), new Category(/*tv[4].getText().toString()*/category),
				calendar.getTimeInMillis(), imageAsString, false, 0);

		events.add(event);

		saveEvents(getApplicationContext(), events, events.size() - 1);

		Firebase.setAndroidContext(this);
		new Firebase(Constants.DATABASE_URL + "/events_list").push().setValue(event);

		//reinitializeUI();
		finish();
	}

	private void reinitializeUI() {
		for (int i=0; i<5; i++){
			tv[i].setText("");
		}
	}

	public static void saveEvents(Context context, ArrayList<Event> events, int oldSize) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();

		editor.remove("EventsArray_size");
		editor.putInt("EventsArray_size", events.size());

        for(int i=0;i<oldSize;i++)
            editor.remove("EventArray_" + i);
        for (int i = 0; i < events.size(); i++) {
            //assicurati che la toString() sia come serve a te
            editor.putString("EventArray_" + i, events.get(i).toString());
        }

        editor.commit();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		if(initial){
			iMonth = monthOfYear + 1;
			iDay = dayOfMonth;
			iYear = year;
			initialDate.setText(Event.getDateString(iYear,iMonth,iDay));
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
			mDatePicker.setTitle("Select Initial Date");
		}else{
			mDatePicker = new DatePickerDialog(this, this, fYear, fMonth, fDay);
			initial = false;
			mDatePicker.setTitle("Select Final Date");
		}
		mDatePicker.show();
	}

	public void chooseTime(View v) {
		TimePickerDialog mTimePicker;
		if (v.getId() == R.id.new_event_initialTime){
			mTimePicker = new TimePickerDialog(this, this, iHour, iMinute, false);
			mTimePicker.setTitle("Select Initial Time");
			initial = true;
		}else{
			mTimePicker = new TimePickerDialog(this, this, fHour, fMinute, false);
			mTimePicker.setTitle("Select Final Time");
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
		}else{
			fHour = hourOfDay;
			fMinute = minute;
			finalTime.setText(Event.getTimeString(fHour,fMinute));
		}
	}

	public void chooseCategory(View v){
		AlertDialog levelDialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Sort by...");
		builder.setSingleChoiceItems(categories, -1, this);
		levelDialog = builder.create();
		levelDialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		category = categories[which];
		((Button) findViewById(R.id.category_picker)).setText(category);
		dialog.dismiss();
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
