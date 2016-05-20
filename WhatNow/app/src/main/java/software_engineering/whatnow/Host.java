/*  CLASS DESCRIPTION:
    -   This is the class related to the Host idea
    -   It's very empty now, but it's gonna contain much more info soon
    -   every Event obj has a reference to a Host obj
*/

package software_engineering.whatnow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Francesco on 4/14/2016.
 */
public class Host {

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Host(String name) {

        this.name = name;
    }
}
