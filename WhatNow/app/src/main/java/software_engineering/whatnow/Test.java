package software_engineering.whatnow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Test extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
	}

	public void submitAppointment(View v) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType("vnd.android.cursor.item/event");
		startActivity(intent);
	}
}
