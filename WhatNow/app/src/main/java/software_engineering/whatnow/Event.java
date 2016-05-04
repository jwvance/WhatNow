package software_engineering.whatnow;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;


import java.util.Calendar;
import java.util.List;

/**
 * Created by Francesco on 4/14/2016.
 */
public class Event {

    //test
    LocationToolBox locationIs;
    //

	private int id;
    private int hourStart;
    private int minuteStart;
    private int hourEnd;
    private int minuteEnd;
    private String location;
    private Host host;
    private String name;
    private String description;
    private Category category;
    private long dateStart;
    private long dateEnd;
    private int month;
    private int year;
    private int day;

    //Location stuff
    private LatLng myLoc;

    private String imagePath;


    public Event(int id, int hourStart, int minuteStart, int hourEnd, int minuteEnd, String location,
                 Host host, String name, String description, Category category, long dateStart, String imagePath) {
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

        this.myLoc = getLocationFromAddress(AddEventActivity.conEvent, this.location);

        this.imagePath = imagePath;


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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return id + ":::" + hourStart + ":::" + minuteStart + ":::" +
                hourEnd + ":::" + minuteEnd + ":::" + location + ":::" +
                host.getName() + ":::" + name + ":::" +description + ":::"
                + category.getName() + ":::" + dateStart + ":::" + imagePath;
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
}
