/*  CLASS DESCRIPTION:
    -   This is class simply expresses the Category
    -   every Event obj has a reference to a Category obj
*/

package software_engineering.whatnow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Francesco on 4/21/2016.
 */
public class Category {

    @SerializedName("name")
    @Expose
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ArrayList<String> getCategories(){
        ArrayList<String> categories = new ArrayList<String>();

        categories.add("ALL");
        categories.add("BARS");
        categories.add("CLUBS");
        categories.add("FOOD");
        categories.add("SHOPS");
        categories.add("OTHER");

        return categories;
    }
}
