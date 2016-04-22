package software_engineering.whatnow;

import android.location.Location;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Francesco on 4/14/2016.
 */
public class Event {

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

    public Event(int hourStart, int minuteStart, int hourEnd, int minuteEnd, String location,
                 Host host, String name, String description, Category category, long date) {
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

    @Override
    public String toString() {
        return ""+ hourStart + ":::" + minuteStart + ":::" +
                 hourEnd + ":::" + minuteEnd + ":::" + location + ":::" +
                host.getName() + ":::" + name + ":::" +description + ":::"
                + category.getName() + ":::" + date;
    }
}
