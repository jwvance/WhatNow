package software_engineering.whatnow;

import android.location.Location;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Francesco on 4/14/2016.
 */
public class Event {

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
    private long date;
    private int month;
    private int year;
    private int day;
    private String imagePath;

    public Event(int id, int hourStart, int minuteStart, int hourEnd, int minuteEnd, String location,
                 Host host, String name, String description, Category category, long date, String imagePath) {
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
        this.date = date;
        this.imagePath = imagePath;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
                + category.getName() + ":::" + date + ":::" + imagePath;
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
}
