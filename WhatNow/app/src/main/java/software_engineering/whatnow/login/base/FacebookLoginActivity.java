package software_engineering.whatnow.login.base;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;

public class FacebookLoginActivity {

    private TextView info;
    private LoginButton loginButton;

    //------Other useful info--------
    public static Profile profile;
    public static AccessToken token;

    //------Methods to set info-------
    public static void setProfile(Profile _profile){
        profile = _profile;
    }

    public static void setToken(AccessToken _token){
        token = _token;
    }

}