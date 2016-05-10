/*  CLASS DESCRIPTION:
    -   Carlos made this one, it is used to get location updates and the distance
        from the device to the event address
    -   there is a Listener to location updates
    -   there is a method to request location updates
    -   the method distance calculates the distance between two lat&lng points
*/

package software_engineering.whatnow;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Charly on 4/23/16.
 */
public class LocationToolBox {
    //public attributes
    public static String LOG_TAG = "MyLocationApplication";
    public static Double storedLatitude = 0.00;
    public static Double storedLongitude = 0.00;
    public LocationListener locationListener;

    //private attributes
    private Context c;
    private LocationData locationData = LocationData.getLocationData();
    boolean hasLocation = false;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    public LocationListener getLocationListener() {
        return this.locationListener;
    }

    public LocationToolBox(Context c){
        this.c = c;
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Location lastLocation = locationData.getLocation();

                // Do something with the location you receive.
                double newAccuracy = location.getAccuracy();

                long newTime = location.getTime();
                // Is this better than what we had?  We allow a bit of degradation in time.
                boolean isBetter = ((lastLocation == null) ||
                        newAccuracy < lastLocation.getAccuracy() + (newTime - lastLocation.getTime()));
                if (isBetter) {
                    // We replace the old estimate by this one.
                    locationData.setLocation(location);
                }
                hasLocation = true;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

    }

    public void requestLocationUpdate() {
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
            if (ContextCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 35000, 10, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 35000, 10, locationListener);

                hasLocation = true;

                Log.i(LOG_TAG, "requesting location update");
            } else {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) c,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Log.i(LOG_TAG, "please allow to use your location");

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions((Activity) c,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        } else {
            Log.i(LOG_TAG, "requesting location update from user");
            //prompt user to enable location
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            c.startActivity(gpsOptionsIntent);
        }
    }


    //Gets the distance between two (Lat, Lon) points. Should return it in Km. Still have to figure out how to return in english units.
    public static Integer distance(Double origLat, Double origLon, Double destLat, Double destLon, StringBuilder inKM){
        Double disLon = Math.toRadians(destLon - origLon);
        Double disLat = Math.toRadians(destLat - origLat);
        Double a = ((Math.sin(disLat/2))*(Math.sin(disLat/2))) + (Math.cos(Math.toRadians(origLat)) * Math.cos(Math.toRadians(destLat)) * Math.sin(disLon/2) * Math.sin(disLon/2) );
        Double c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double dis = 6371000 * c;
        //return simpler values
        Integer distance = dis.intValue();
        if(distance > 1000){
            //inKM = new String("yes");
            inKM.delete(0, inKM.length());
            inKM.append("yes");
            distance = distance/1000;
            return distance;
        }
        else return distance;//inKM = false;
        //return distance;
    }


}
