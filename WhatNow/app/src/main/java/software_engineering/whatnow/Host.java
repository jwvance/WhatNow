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
//    @SerializedName("businessAddress")
//    @Expose
    private String businessAddress;
//    @SerializedName("businessPhone")
//    @Expose
    private String businessPhone;
    @SerializedName("businessEmail")
    @Expose
    private String businessEmail;
//    @SerializedName("businessWebsite")
//    @Expose
    private String businessWebsite;

    public Host(String name) {
        this.name = name;
    }

    public Host(String name, String businessEmail) {
        this.name = name;
        this.businessEmail = businessEmail;
    }

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessWebsite() {
        return businessWebsite;
    }

    public void setBusinessWebsite(String businessWebsite) {
        this.businessWebsite = businessWebsite;
    }
}
