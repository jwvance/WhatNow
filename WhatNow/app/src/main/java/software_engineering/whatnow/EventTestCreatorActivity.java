package software_engineering.whatnow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;

public class EventTestCreatorActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

	private EditText[] tv = new EditText[5];
	private DatePicker dp;
	private TimePicker tp;
	private Button addEvent;
	private Event event;
	private ArrayList<Event> events;
	private EditText date;
	private EditText time;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_test_creator);

		initialize();

		events = loadEvents(this);
		date = (EditText) findViewById(R.id.dateText);
		Calendar mcurrentDate = Calendar.getInstance();
		mYear = mcurrentDate.get(Calendar.YEAR);
		mMonth = mcurrentDate.get(Calendar.MONTH);
		mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
		date.setText(mMonth + "-" + mDay + "-" + mYear);
		time = (EditText) findViewById(R.id.timeText);
		mHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
		mMinute = mcurrentDate.get(Calendar.MINUTE);
		time.setText(mHour + ":" + mMinute);
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
					Long.parseLong(st.nextToken())));
			}catch(Exception e){
				Log.wtf("LOAD", "Problem loading events: " + e.getMessage());
			}
		}
		return output;
	}

	private void initialize(){
		tv[0]= (EditText) findViewById(R.id.editText);
		tv[1]= (EditText) findViewById(R.id.editText2);
		tv[2]= (EditText) findViewById(R.id.editText3);
		tv[3]= (EditText) findViewById(R.id.editText4);
		tv[4]= (EditText) findViewById(R.id.editText5);
		//dp = (DatePicker) findViewById(R.id.datePicker);
		//tp = (TimePicker) findViewById(R.id.timePicker);
		addEvent = (Button) findViewById(R.id.button);
	}

	public void addEvent(View v) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(mYear, mMonth, mDay);
		event = new Event(new Random().nextInt(1000), mHour, mMinute, mHour, mMinute,
				tv[2].getText().toString(), new Host(tv[3].getText().toString()), tv[0].getText().toString(),
				tv[1].getText().toString(), new Category(tv[4].getText().toString()), calendar.getTimeInMillis());

		events.add(event);

		saveEvents(getApplicationContext(), events, events.size() - 1);

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
		mMonth = monthOfYear + 1;
		mDay = dayOfMonth;
		mYear = year;
		date.setText("" + monthOfYear + "-" + dayOfMonth + "-" + year);
	}

	public void chooseDate(View v) {
		DatePickerDialog mDatePicker;
		mDatePicker = new DatePickerDialog(this, this, mYear, mMonth, mDay);
		mDatePicker.setTitle("Select Date");
		mDatePicker.show();
	}

	public void chooseTime(View v){
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(this, this, mHour, mMinute, false);
		mTimePicker.setTitle("Select Time");
		mTimePicker.show();
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mHour = hourOfDay;
		mMinute = minute;
		time.setText(hourOfDay + ":" + minute);
	}
}
