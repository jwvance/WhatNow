package software_engineering.whatnow;

import com.firebase.client.Firebase;
import android.content.Context;
import android.support.multidex.MultiDex;
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

    //override attachbasecontext() method and enable multidex
    //http://developer.android.com/tools/building/multidex.html
    //http://developer.android.com/reference/android/support/multidex/MultiDexApplication.html
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);}

}