package software_engineering.whatnow;

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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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


public class UserQActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {
	private EditText nameET;
	private TextView nameTV;
	private EditText emailET;
	private TextView emailTV;
	private EditText cityET;
	private EditText nationalityET;
	private EditText universityET;
	private EditText workET;
	private EditText sexET;
	private EditText ageET;
	private EditText relationshipET;
	private EditText facebookET;
	private ImageView mainPicture;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private final static int MAX_IMAGE_DIMENSION=400;
	private static final int RESULT_LOAD_IMG = 1;
	private String imageAsString;
	private Bitmap image;
	private Map<String, Object> mapUser;
	private Firebase firebaseUser;
	private String encodedEmail;
	private String userAccountEmail;
	private String userAccountName;
	private boolean fromLogIn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_q);
		Toolbar toolbar = (Toolbar) findViewById(R.id.user_q_toolbar);
		setSupportActionBar(toolbar);

		fromLogIn = getIntent().getBooleanExtra("from_login", true);

		this.setTitle("Set User Info...");

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_user_q);
		fab.setOnClickListener(this);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();

		nameET = (EditText) findViewById(R.id.user_q_name);
		nameTV = (TextView) findViewById(R.id.user_q_name_saved);
		emailET = (EditText) findViewById(R.id.user_q_email);
		emailTV = (TextView) findViewById(R.id.user_q_email_saved);
		cityET = (EditText) findViewById(R.id.user_q_city);
		nationalityET = (EditText) findViewById(R.id.user_q_nationality);
		universityET = (EditText) findViewById(R.id.user_q_university);
		workET = (EditText) findViewById(R.id.user_q_work);
		sexET = (EditText) findViewById(R.id.user_q_sex);
		ageET = (EditText) findViewById(R.id.user_q_age);
		relationshipET = (EditText) findViewById(R.id.user_q_relationship);
		facebookET = (EditText) findViewById(R.id.user_q_facebook);

		nameET.setText(preferences.getString("user_name",""));
		// FIX (maybe with boolean in preferences if logged in properly)
		if(fromLogIn){
			emailTV.setVisibility(View.GONE);
			emailET.setVisibility(View.VISIBLE);
		}else{
			emailTV.setVisibility(View.VISIBLE);
			emailET.setVisibility(View.GONE);
			emailTV.setText(preferences.getString("user_email",""));
		}
		cityET.setText(preferences.getString("user_city",""));
		nationalityET.setText(preferences.getString("user_nationality",""));
		universityET.setText(preferences.getString("user_university",""));
		workET.setText(preferences.getString("user_work",""));
		sexET.setText(preferences.getString("user_sex",""));
		ageET.setText(preferences.getString("user_age",""));
		relationshipET.setText(preferences.getString("user_relationship",""));
		facebookET.setText(preferences.getString("user_facebook",""));

		mainPicture = (ImageView) findViewById(R.id.user_q_image);
		imageAsString = preferences.getString("user_picture", "");
		if(!imageAsString.equals("")) {
			try {
				byte[] imageAsBytes = Base64.decode(imageAsString, Base64.DEFAULT);
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
				mainPicture.setImageBitmap(bitmap);
			}catch(Exception e){
				Log.wtf("USER Q ACTIVITY", "picture problem");
			}
		}

		encodedEmail = preferences.getString(Constants.KEY_ENCODED_EMAIL, "");
		firebaseUser = new Firebase(Constants.USERS_URL + encodedEmail);
		if(fromLogIn) {
			Firebase firebaseLogIn = new Firebase(Constants.FIREBASE_URL + "users/" + encodedEmail);
			firebaseLogIn.addListenerForSingleValueEvent(this);
		}else{
			userAccountEmail = preferences.getString("user_account_email", "");
			userAccountName = preferences.getString("user_account_name", "");
		}
	}

	private boolean saveInfo() {
		if(nameET.getText().toString().equals("")){
			Toast.makeText(UserQActivity.this, "Set Name first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(cityET.getText().toString().equals("")){
			Toast.makeText(UserQActivity.this, "Set City first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(nationalityET.getText().toString().equals("")){
			Toast.makeText(UserQActivity.this, "Set Nationality first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(ageET.getText().toString().equals("")){
			Toast.makeText(UserQActivity.this, "Set Age first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(sexET.getText().toString().equals("")){
			Toast.makeText(UserQActivity.this, "Set Sex first...", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(imageAsString == null || imageAsString.equals("")){
			Toast.makeText(UserQActivity.this, "Set a picture first...", Toast.LENGTH_SHORT).show();
			return false;
		}

		Snackbar.make(findViewById(R.id.fab_user_q), "Saving Info...", Snackbar.LENGTH_LONG)
				.setAction("Action", null).show();

		Map<String, String> userInfoMap = new HashMap<String, String>();
		userInfoMap.put("user_account_name", userAccountName);
		userInfoMap.put("user_account_email", userAccountEmail);
		userInfoMap.put("user_name", nameET.getText().toString());
		userInfoMap.put("user_email", emailET.getText().toString());
		userInfoMap.put("user_city", cityET.getText().toString());
		userInfoMap.put("user_nationality", nationalityET.getText().toString());
		userInfoMap.put("user_sex", sexET.getText().toString());
		userInfoMap.put("user_age", ageET.getText().toString());
		userInfoMap.put("user_picture", imageAsString);

		editor.putString("user_account_name", userAccountName);
		editor.putString("user_account_email", userAccountEmail);
		editor.putString("user_name", nameET.getText().toString());
		editor.putString("user_email", emailET.getText().toString());
		editor.putString("user_city", cityET.getText().toString());
		editor.putString("user_nationality", nationalityET.getText().toString());
		editor.putString("user_sex", sexET.getText().toString());
		editor.putString("user_age", ageET.getText().toString());
		editor.putString("user_picture", imageAsString);

		if(!universityET.getText().toString().equals("")) {
			editor.putString("user_university", universityET.getText().toString());
			userInfoMap.put("user_university", universityET.getText().toString());
		}
		if(!workET.getText().toString().equals("")) {
			editor.putString("user_work", workET.getText().toString());
			userInfoMap.put("user_work", workET.getText().toString());
		}
		if(!relationshipET.getText().toString().equals("")) {
			editor.putString("user_relationship", relationshipET.getText().toString());
			userInfoMap.put("user_relationship", relationshipET.getText().toString());
		}
		if(!facebookET.getText().toString().equals("")) {
			editor.putString("user_facebook", facebookET.getText().toString());
			userInfoMap.put("user_facebook", facebookET.getText().toString());
		}

		firebaseUser.setValue(userInfoMap);
		editor.commit();

		return true;
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		mapUser = dataSnapshot.getValue(HashMap.class);
		Log.wtf("USER Q ACTIVITY", mapUser.get("name").toString());
		userAccountEmail = mapUser.get("email").toString();
		userAccountEmail = Utils.decodeEmail(userAccountEmail);
		userAccountName = mapUser.get("name").toString();
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
				startActivity(new Intent(getApplicationContext(), TabActivity.class));
				editor.putBoolean("logged_in", true);
				editor.putBoolean("is_host", false);
				editor.putBoolean("is_user", true);
				editor.commit();
			}

			finish();
		}
	}

}
