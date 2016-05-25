package software_engineering.whatnow;

import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HostQActivity extends AppCompatActivity {
	private EditText hostNameET;
	private EditText addressET;
	private EditText phoneET;
	private EditText websiteET;
	private EditText tripadvisorET;
	private EditText facebookET;
	private ImageView mainPicture;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	private final static int MAX_IMAGE_DIMENSION=400;
	private static final int RESULT_LOAD_IMG = 1;
	private String imageAsString;
	private Bitmap image;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_q);
		Toolbar toolbar = (Toolbar) findViewById(R.id.host_q_toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_host_q);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				saveInfo();
				Snackbar.make(view, "Saving Info...", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
				finish();
			}
		});

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

		mainPicture = (ImageView) findViewById(R.id.host_q_image);
		
	}

	private void saveInfo() {
		if(hostNameET.getText().toString().equals("")){
			Toast.makeText(HostQActivity.this, "Set Host Name first...", Toast.LENGTH_SHORT).show();
			return;
		}
		if(addressET.getText().toString().equals("")){
			Toast.makeText(HostQActivity.this, "Set Address first...", Toast.LENGTH_SHORT).show();
			return;
		}
		if(phoneET.getText().toString().equals("")){
			Toast.makeText(HostQActivity.this, "Set Phone first...", Toast.LENGTH_SHORT).show();
			return;
		}
		if(imageAsString == null || imageAsString.equals("")){
			Toast.makeText(HostQActivity.this, "Set a picture first...", Toast.LENGTH_SHORT).show();
			return;
		}

		editor.putString("host_name", hostNameET.getText().toString());
		editor.putString("host_address", addressET.getText().toString());
		editor.putString("host_phone", phoneET.getText().toString());
		editor.putString("host_picture", imageAsString);

		if(!websiteET.getText().toString().equals(""))
			editor.putString("host_website", websiteET.getText().toString());
		if(!tripadvisorET.getText().toString().equals(""))
			editor.putString("host_tripadvisor", tripadvisorET.getText().toString());
		if(!facebookET.getText().toString().equals(""))
			editor.putString("host_facebook", facebookET.getText().toString());

		editor.commit();
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

}
