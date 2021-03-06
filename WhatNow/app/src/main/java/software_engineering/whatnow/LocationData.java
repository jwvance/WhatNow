/*  CLASS DESCRIPTION:
    -   Carlos (*Shobbit) made this one, used to handle the location stuff
*/

package software_engineering.whatnow;

/**
 * Created by Charly on 4/23/16.
 */
/**
 * Code from Shobbit's FindRestaurant app.
 */
import android.location.Location;

/**
 * Created by shobhit on 1/23/16.
 */
public class LocationData {

    private static LocationData instance = null;

    private LocationData(){}

    private Location location;


    public Location getLocation(){
        return location;
    }

    public void setLocation(Location _location){
        location = _location;
    }

    public static LocationData getLocationData(){
        if(instance == null){
            instance = new LocationData();
        }
        return instance;
    }
}