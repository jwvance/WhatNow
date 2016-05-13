/*  CLASS DESCRIPTION:
    -   This is the class that has all the info about a single event
    -   there are many fields and others will be added soon, they all have getters
        and setters, there are also additional methods
    -   getDistance and the one with meters (made by Carlos) calculates the distance,
        they need to be fixed though (many exceptions and crashes)
    -   toString is used by the saveEvents function
    -   utility methods to get the date as a String MM-DD-YYYY (instead of a long),
        the time as a String like HH:MM, also static versions of both
    -   getLocationFromAddress is another utility function to get a lat&lng pair from
        an address like "Santa Cruz", "College Nine Road, Santa Cruz" or whatever
    -   the following methods are related to the sorting functionality: it was
        implemented so that the compareTo calls different comparing methods depending
        on the sorting criteria set
*/

package software_engineering.whatnow;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

<<<<<<< HEAD
=======
import android.content.*;
import android.preference.PreferenceManager;
import android.view.View;

>>>>>>> catPics
import java.util.Calendar;
import java.util.List;

/**
 * Created by Francesco on 4/14/2016.
 */
public class Event implements Comparable {

    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("dateStart")
    @Expose
    private long dateStart;
    @SerializedName("dateString")
    @Expose
    private String dateString;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("host")
    @Expose
    private Host host;
    @SerializedName("hourEnd")
    @Expose
    private Integer hourEnd;
    @SerializedName("hourStart")
    @Expose
    private Integer hourStart;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("imageAsString")
    @Expose
    private String imageAsString;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("minuteEnd")
    @Expose
    private Integer minuteEnd;
    @SerializedName("minuteStart")
    @Expose
    private Integer minuteStart;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("startTime")
    @Expose
    private String startTime;

    //test
    LocationToolBox locationIs;
    //

    private long dateEnd;
    private int month;
    private int year;
    private int day;
    private int numberOfGuests = 0;
    private long timestamp;
    private int sortingCriteria=0;

    //Location stuff
    private LatLng myLoc;
    private Context cont;


    public Event(){
        //empty constructor
    }

    public Event(int id, int hourStart, int minuteStart, int hourEnd, int minuteEnd, String location,
<<<<<<< HEAD
                 Host host, String name, String description, Category category, long dateStart, String imageAsString, boolean fromFirebase, long timeStamp) {
=======
                 Host host, String name, String description, Category category, long dateStart, String imagePath, Context cont) {
>>>>>>> catPics
        this.id = id;
        this.hourStart = hourStart;
        this.minuteStart = minuteStart;
        this.hourEnd = hourEnd;
        this.minuteEnd = minuteEnd;
        this.location = location;
        this.host = host;
        this.name = name;
        this.description = description;
        this.category = category;
        this.dateStart = dateStart;
<<<<<<< HEAD
        if (fromFirebase){
            this.timestamp=timeStamp;
        } else this.timestamp = System.currentTimeMillis();

        try {
            this.myLoc = getLocationFromAddress(AddEventActivity.conEvent, this.location);
        }catch(NullPointerException npe){
            this.myLoc = new LatLng(36.9741,-122.0308);	//Santa Cruz
            npe.printStackTrace();
        }
        this.imageAsString = imageAsString;
=======
        this.timestamp = System.currentTimeMillis();
        this.cont = cont;

		try {
			this.myLoc = getLocationFromAddress(this.cont, this.location);
		}catch(NullPointerException npe){
			this.myLoc = new LatLng(36.9741,-122.0308);	//Santa Cruz
			npe.printStackTrace();
		}
        this.imagePath = imagePath;
>>>>>>> catPics


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateStart);

        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public long getDateStart() {
        return dateStart;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public int getHourStart() {
        return hourStart;
    }

    public void setHourStart(int hourStart) {
        this.hourStart = hourStart;
    }

    public int getMinuteStart() {
        return minuteStart;
    }

    public void setMinuteStart(int minuteStart) {
        this.minuteStart = minuteStart;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(int hourEnd) {
        this.hourEnd = hourEnd;
    }

    public int getMinuteEnd() {
        return minuteEnd;
    }

    public void setMinuteEnd(int minuteEnd) {
        this.minuteEnd = minuteEnd;
    }

    public String getLocation() {
        return location;
    }

    //Carlos's test for getting distance.
    public String getDistance() {

        Double storedLatitude = LocationToolBox.storedLatitude;
        Double storedLongitude = LocationToolBox.storedLongitude;
        StringBuilder isKM = new StringBuilder("no");
        Integer dis = locationIs.distance(storedLatitude, storedLongitude, this.myLoc.latitude, this.myLoc.longitude, isKM);
        String disIs = ((isKM.toString().equals("yes")) ? dis.toString() + "km" : dis.toString() + "m");
        return disIs;
    }

    public int getDistanceInMeters(){
        Double storedLatitude = LocationToolBox.storedLatitude;
        Double storedLongitude = LocationToolBox.storedLongitude;
        StringBuilder isKM = new StringBuilder("no");
        Integer dis = locationIs.distance(storedLatitude, storedLongitude, this.myLoc.latitude, this.myLoc.longitude, isKM);

        return dis;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageAsString() {
        return imageAsString;
    }

    public void setImageAsString(String imageAsString) {
        this.imageAsString = imageAsString;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public void increaseGuests(){
        this.numberOfGuests++;
    }

    public void decreaseGuests(){ this.numberOfGuests--; }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return id + ":::***:::***:::" + hourStart + ":::***:::***:::" + minuteStart + ":::***:::***:::" +
                hourEnd + ":::***:::***:::" + minuteEnd + ":::***:::***:::" + location + ":::***:::***:::" +
                host.getName() + ":::***:::***:::" + name + ":::***:::***:::" +description + ":::***:::***:::"
                + category.getName() + ":::***:::***:::" + dateStart + ":::***:::***:::" + imageAsString;
    }

    public String getDateString(){
        return month + "-" + (day < 10 ? 0 : "") + day + "-" + year;
    }

    public String getStartTime(){
        return "" + (hourStart < 10 ? 0 : "") + hourStart + " : " + (minuteStart < 10 ? 0 : "") + minuteStart;
    }

    public String getEndTime(){
        return "" + (hourEnd < 10 ? 0 : "") + hourEnd + " : " + (minuteEnd < 10 ? 0 : "") + minuteEnd;
    }

    public static String getTimeString(int hour, int minute){
        return "" + (hour < 10 ? 0 : "") + hour + " : " + (minute < 10 ? 0 : "") + minute;
    }

    public static String getDateString(int year, int month, int day){
        return month + "-" + (day < 10 ? 0 : "") + day + "-" + year;
    }
    public void setSortingCriteria(int sortingCriteria) {
        this.sortingCriteria = sortingCriteria;
    }

    public void setMyLoc(Context context){
        this.myLoc = getLocationFromAddress(context, this.location);
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    //returns -1 if this is before the other event, 0 if they are at the same time
    //and 1 if the second event is before this one
    public int compareToByTime(Event event){
        if (this.getYear()<event.getYear())
            return -1;
        else if (this.getYear()>event.getYear())
            return 1;
        if (this.getMonth()<event.getMonth())
            return -1;
        else if (this.getMonth()>event.getMonth())
            return 1;
        if (this.getDay()<event.getDay())
            return -1;
        else if (this.getDay()>event.getDay())
            return 1;
        if (this.getHourStart()<event.getHourStart())
            return -1;
        else if (this.getHourStart()>event.getHourStart())
            return 1;
        if (this.getMinuteStart()<event.getMinuteStart())
            return -1;
        else if (this.getMinuteStart()>event.getMinuteStart())
            return 1;


        return 0;
    }


    //returns -1 if this is closer than the other event, 0 if they are at the same distance
    //and 1 if the second event is closer to this one
    public int compareToByDistance(Event event){
        return (this.getDistanceInMeters()-event.getDistanceInMeters());
    }

    //returns 1 if this has more attendences than the other event, 0 if they have the same
    //and -1 if the second event has more attendences this one
    public int compareToByPopularity(Event event){
        if (this.getNumberOfGuests()>event.getNumberOfGuests())
            return 1;
        else if (this.getNumberOfGuests()<event.getNumberOfGuests())
            return -1;
        return 0;
    }

    //returns 1 if this has been created more recently than the other event, 0 if they have
    //been created at the same time and -1 if the second event is more recent than this one
    public int compareToByCreationTime(Event event){
        if (this.getTimestamp()>event.getTimestamp())
            return 1;
        else if (this.getTimestamp()<event.getTimestamp())
            return -1;
        return 0;
    }

    @Override
    public int compareTo(Object otherEvent) {
        int result=0;
        Event event = (Event) otherEvent;


        switch (this.sortingCriteria) {

            case 0:
                result = compareToByPopularity(event);
                // popularity
                break;
            case 1:
                result = compareToByTime(event);
                // incoming
                break;
            case 2:
                result = compareToByDistance(event);
                // distance
                break;
            case 3:
                result = compareToByCreationTime(event);
                // recent
                break;
        }

        return result;
    }
}
