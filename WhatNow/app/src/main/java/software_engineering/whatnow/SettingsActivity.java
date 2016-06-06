package software_engineering.whatnow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity{

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editText =(EditText) findViewById(R.id.editText);
    }

    public void saveSettings(View view){
        Float maximumDistance = -1.0f;

        try{
            maximumDistance = Float.parseFloat(editText.getText().toString());
            if (maximumDistance<0){
                Toast.makeText(getApplicationContext(), "Invalid Distance", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            maximumDistance = -1.0f;
            Toast.makeText(getApplicationContext(), "Invalid Distance", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if (maximumDistance>0){
            editor.putFloat("maximumDistance", maximumDistance);
            editor.commit();
        }

        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);
    }
}