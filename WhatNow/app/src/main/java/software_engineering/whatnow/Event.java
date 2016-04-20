package com.fiacco.francesco.whatnow_1;

import android.location.Location;

import java.sql.Time;

/**
 * Created by Francesco on 4/14/2016.
 */
public class Event {

    private Time timeStart;
    private Time timeEnd;
    private Location location;
    private Host host;
    private String name;
    private String description;

    public Event(Time timeEnd, Time timeStart, Location location, Host host, String name, String description) {
        this.timeEnd = timeEnd;
        this.timeStart = timeStart;
        this.location = location;
        this.host = host;
        this.name = name;
        this.description = description;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public Time getTimeEnd() {
        return timeEnd;
    }

    public Location getLocation() {
        return location;
    }

    public Host getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(Time timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public void setName(String name) {
        this.name = name;
    }
}
