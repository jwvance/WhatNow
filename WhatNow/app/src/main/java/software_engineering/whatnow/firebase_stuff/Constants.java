package software_engineering.whatnow.firebase_stuff;

/**
 * Created by Steve on 5/4/16.
 * hey STEVE!
 */
public class Constants {
	//	public static final String FIREBASE_URL = "https://ucscwhatnow.firebaseio.com";
	public static final String FIREBASE_URL = "https://flickering-heat-2569.firebaseio.com/";

	//public static final String DATABASE_URL = "https://ucscwhatnow.firebaseio.com/events";
	public static final String DATABASE_URL = "https://ucscwhatnow.firebaseio.com/events/test";

	/**
	 * Constants related to locations in Firebase, such as the name of the node
	 */
	public static final String FIREBASE_LOCATION_USERS = "users";
	public static final String FIREBASE_LOCATION_USER_LISTS = "userLists";
	public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";



	/**
	 * Constants for Firebase object properties
	 */
	public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
	public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
	public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE = "timestampLastChangedReverse";

	/**
	 * Constants for Firebase URL
	 */
	public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;

	/**
	 * Constants for bundles, extras and shared preferences keys
	 */
	public static final String KEY_LAYOUT_RESOURCE = "LAYOUT_RESOURCE";
	public static final String KEY_SIGNUP_EMAIL = "SIGNUP_EMAIL";
	public static final String KEY_PROVIDER = "PROVIDER";
	public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
	public static final String KEY_GOOGLE_EMAIL = "GOOGLE_EMAIL";
	public static final String KEY_FACEBOOK_EMAIL = "FACEBOOK_EMAIL";


	/**
	 * Constants for Firebase login
	 */
	public static final String GOOGLE_PROVIDER = "google";
	public static final String FACEBOOK_PROVIDER = "facebook";

	public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";



}
