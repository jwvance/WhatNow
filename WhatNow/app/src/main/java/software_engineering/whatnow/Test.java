package software_engineering.whatnow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

public class Test extends AppCompatActivity {

	private EditText[] tv = new EditText[5];
	private DatePicker dp;
	private TimePicker tp;
	private Button addEvent;
	private Event event;
	private ArrayList<Event> events;
	private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		initialize();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

		events = loadEvents();


	}

	private ArrayList<Event> loadEvents() {
		ArrayList<Event> output = new ArrayList<Event>();
		int size = preferences.getInt("EventsArray_size", 0);
		StringTokenizer st;
		for (int i = 0; i < size; i++) {
			st = new StringTokenizer(preferences.getString("EventArray_" + i, null), ":::");
			output.add(new Event(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
					Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), st.nextToken(),
					new Host(st.nextToken()), st.nextToken(), st.nextToken(), new Category(st.nextToken()),
					Long.parseLong(st.nextToken())));
		}
		return output;
	}

	private void initialize(){
		tv[0]= (EditText) findViewById(R.id.editText);
		tv[1]= (EditText) findViewById(R.id.editText2);
		tv[2]= (EditText) findViewById(R.id.editText3);
		tv[3]= (EditText) findViewById(R.id.editText4);
		tv[4]= (EditText) findViewById(R.id.editText5);
		dp = (DatePicker) findViewById(R.id.datePicker);
		tp = (TimePicker) findViewById(R.id.timePicker);
		addEvent = (Button) findViewById(R.id.button);
	}

	public void addEvent(View v) {
		event = new Event(tp.getCurrentHour(), tp.getCurrentMinute(), tp.getCurrentHour(), tp.getCurrentMinute(),
				tv[2].getText().toString(), new Host(tv[3].getText().toString()), tv[0].getText().toString(),
				tv[1].getText().toString(), new Category(tv[4].getText().toString()), dp.getMaxDate());

		events.add(event);

		saveEvents(events, events.size() - 1);

		reinitializeUI();
	}

	private void reinitializeUI() {
		for (int i=0; i<5; i++){
			tv[i].setText("");
		}
	}

	private void saveEvents(ArrayList<Event> events, int oldSize) {
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
}
