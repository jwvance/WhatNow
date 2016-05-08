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
