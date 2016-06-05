package software_engineering.whatnow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import software_engineering.whatnow.firebase_stuff.Constants;
import software_engineering.whatnow.utils.Utils;


public class HostQActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {
	private EditText hostNameET;
	private EditText addressET;
	private EditText phoneET;
	private EditText websiteET;
	private EditText tripadvisorET;
	private EditText facebookET;
	private EditText yelpET;
	private ImageView mainPicture;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private final static int MAX_IMAGE_DIMENSION=400;
	private static final int RESULT_LOAD_IMG = 1;
	private String imageAsString;
	private Bitmap image;
	private Map<String, Object> mapUser;
	private Firebase firebaseHost;
	private String encodedEmail;
	private String hostAccountEmail;
	private String hostAccountName;
	private boolean fromLogIn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_q);
		Toolbar toolbar = (Toolbar) findViewById(R.id.host_q_toolbar);
		setSupportActionBar(toolbar);

		fromLogIn = getIntent().getBooleanExtra("from_login", true);

		this.setTitle("Set Host Info...");

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_host_q);
		fab.setOnClickListener(this);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();

		hostNameET = (EditText) findViewById(R.id.host_q_name);
		hostNameET.setText(preferences.getString("host_name", ""));
		addressET = (EditText) findViewById(R.id.host_q_address);
		addressET.setText(preferences.getString("host_address", ""));
		phoneET = (EditText) findViewById(R.id.host_q_phone);
		phoneET.setText(preferences.getString("host_phone", ""));
		websiteET = (EditText) findViewById(R.id.host_q_website);
		websiteET.setText(preferences.getString("host_website", ""));
		tripadvisorET = (EditText) findViewById(R.id.host_q_tripadvisor);
		tripadvisorET.setText(preferences.getString("host_tripadvisor", ""));
		facebookET = (EditText) findViewById(R.id.host_q_facebook);
		facebookET.setText(preferences.getString("host_facebook", ""));
		yelpET = (EditText) findViewById(R.id.host_q_yelp);
		yelpET.setText(preferences.getString("host_yelp", ""));

		mainPicture = (ImageView) findViewById(R.id.host_q_image);
		imageAsString = preferences.getString("host_picture", "");
		if(!imageAsString.equals("")) {
			try {
				byte[] imageAsBytes = Base64.decode(imageAsString, Base64.DEFAULT);
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
				mainPicture.setImageBitmap(bitmap);
			}catch(Exception e){
				Log.wtf("HOST Q ACTIVITY", "picture problem");
			}
		}

		encodedEmail = preferences.getString(Constants.KEY_ENCODED_EMAIL, "");
		firebaseHost = new Firebase(Constants.HOSTS_URL + encodedEmail);
		Firebase firebaseUser = new Firebase(Constants.FIREBASE_URL + "users/" + encodedEmail);
		firebaseUser.addListenerForSingleValueEvent(this);
	}

	private boolean saveInfo() {
		if(hostNameET.getText().toString().equals("")){
			Toast.makeText(HostQActivity.this, "Set Host Name first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(addressET.getText().toString().equals("")){
			Toast.makeText(HostQActivity.this, "Set Address first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(phoneET.getText().toString().equals("")){
			Toast.makeText(HostQActivity.this, "Set Phone first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(imageAsString == null || imageAsString.equals("")){
			Toast.makeText(HostQActivity.this, "Set a picture first...", Toast.LENGTH_SHORT).show();
			return false;
		}

		Snackbar.make(findViewById(R.id.fab_host_q), "Saving Info...", Snackbar.LENGTH_LONG)
				.setAction("Action", null).show();

		Map<String, String> hostInfoMap = new HashMap<String, String>();
		hostInfoMap.put("host_account_email", hostAccountEmail);
		hostInfoMap.put("host_account_name", hostAccountName);
		hostInfoMap.put("host_name", hostNameET.getText().toString());
		hostInfoMap.put("host_address", addressET.getText().toString());
		hostInfoMap.put("host_phone", phoneET.getText().toString());
		hostInfoMap.put("host_picture", imageAsString);

		editor.putString("host_name", hostNameET.getText().toString());
		editor.putString("host_address", addressET.getText().toString());
		editor.putString("host_phone", phoneET.getText().toString());
		editor.putString("host_picture", imageAsString);

		if(!websiteET.getText().toString().equals("")) {
			editor.putString("host_website", websiteET.getText().toString());
			hostInfoMap.put("host_website", websiteET.getText().toString());
		}
		if(!tripadvisorET.getText().toString().equals("")) {
			editor.putString("host_tripadvisor", tripadvisorET.getText().toString());
			hostInfoMap.put("host_tripadvisor", tripadvisorET.getText().toString());
		}
		if(!facebookET.getText().toString().equals("")) {
			editor.putString("host_facebook", facebookET.getText().toString());
			hostInfoMap.put("host_facebook", facebookET.getText().toString());
		}
		if(!yelpET.getText().toString().equals("")) {
			editor.putString("host_yelp", yelpET.getText().toString());
			hostInfoMap.put("host_yelp", yelpET.getText().toString());
		}

		firebaseHost.setValue(hostInfoMap);
		editor.commit();

		return true;
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		mapUser = dataSnapshot.getValue(HashMap.class);
		Log.wtf("HOST Q ACTIVITY", mapUser.get("name").toString());
		hostAccountEmail = mapUser.get("email").toString();
		hostAccountEmail = Utils.decodeEmail(hostAccountEmail);
		hostAccountName = mapUser.get("name").toString();
	}

	@Override
	public void onCancelled(FirebaseError firebaseError) {

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

				image = scaleImage(this, uri);
				Log.wtf("IMAGE", getRealPathFromURI(this, uri));

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

				byte[] byteArray = stream.toByteArray();
				imageAsString = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);

				mainPicture.setImageBitmap(image);

				((Button) findViewById(R.id.host_q_choose_image)).setText("Change image");
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

	@Override
	public void onClick(View view) {
		if(saveInfo()){
			if(fromLogIn) {
				editor.putBoolean("logged_in", true);
				editor.putBoolean("is_host", true);
				editor.commit();
				askUser();
				//startActivity(new Intent(getApplicationContext(), TabActivity.class));
			}else{
				finish();
			}
		}
	}

	private void askUser(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage("Do you want also to register a User account?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getApplicationContext(), UserQActivity.class);
						intent.putExtra("from_login", true);
						startActivity(intent);
						dialog.cancel();
						finish();
					}
				})

				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getApplicationContext(), TabActivity.class);
						startActivity(intent);
						editor.putBoolean("is_user", false);
						editor.commit();
						dialog.cancel();
						finish();
					}
				});

		//show dialogue
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
