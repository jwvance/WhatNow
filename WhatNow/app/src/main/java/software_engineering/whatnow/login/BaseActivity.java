package software_engineering.whatnow.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import software_engineering.whatnow.R;
import software_engineering.whatnow.TabActivity;
import software_engineering.whatnow.firebase_stuff.Constants;
import software_engineering.whatnow.login.base.LoginActivity;
import software_engineering.whatnow.utils.*;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It implements GoogleApiClient callbacks to enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {


    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    protected String mProvider, mEncodedEmail;
    /* Client used to interact with Google APIs. */
    protected GoogleApiClient mGoogleApiClient;
    protected Firebase.AuthStateListener mAuthListener;
    protected Firebase mFirebaseRef;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    /*    mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if(mSharedPref.getBoolean("logged_in", false)){
            Log.wtf("LOGIN", "about to open TabActivity from Base");
            Intent intent = new Intent(this, TabActivity.class);
            startActivity(intent);
            finish();
            return;
        }*/

        /* Setup the Google API object to allow Google logins */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */

        /* Setup the Google API object to allow Google+ logins */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /**
         * Getting mProvider and mEncodedEmail from SharedPreferences
         */
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */
        mEncodedEmail = sp.getString(Constants.KEY_ENCODED_EMAIL, null);
        mProvider = sp.getString(Constants.KEY_PROVIDER, null);

        if (!((this instanceof LoginActivity))) {
            mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
            mAuthListener = new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                     /* The user has been logged out */
                    if (authData == null) {
                        /* Clear out shared preferences */
                        SharedPreferences.Editor spe = sp.edit();
                        spe.putString(Constants.KEY_ENCODED_EMAIL, null);
                        spe.putString(Constants.KEY_PROVIDER, null);

                        takeUserToLoginScreenOnUnAuth();
                    }
                }
            };
            mFirebaseRef.addAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        if (id == R.id.action_logout) {
			Log.wtf("LOGOUT", "inside Base about to call logout()");
			logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void initializeBackground(LinearLayout linearLayout) {

        linearLayout.setBackgroundResource(R.drawable.background_image);

//        /**
//         * Set different background image for landscape and portrait layouts
//         */
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            linearLayout.setBackgroundResource(R.drawable.background_image_wide);
//        } else {
//            linearLayout.setBackgroundResource(R.drawable.background_image);
//        }
    }

    /**
     * Logs out the user from their current session and starts LoginActivity.
     * Also disconnects the mGoogleApiClient if connected and provider is Google
     */
    protected void logout() {

		Log.wtf("LOGOUT", "inside Base about to log out");
        /* Logout if mProvider is not null */
        if (mProvider != null) {
            Log.e(LOG_TAG, "PROVIDER:" + mProvider);

            mFirebaseRef.unauth();

            if (mProvider.equals(Constants.GOOGLE_PROVIDER)) {

                /* Logout from Google+ */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                //nothing
                            }
                        });
            }
        }
		mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.remove("logged_in");
		editor.commit();
    }

    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
