package software_engineering.whatnow;

import com.firebase.client.Firebase;
import com.facebook.FacebookSdk;

/**
 * Includes one-time initialization of Firebase related code
 */
public class WhatNowApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
        /* Enable disk persistence  */
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        FacebookSdk.sdkInitialize(this);
    }

}