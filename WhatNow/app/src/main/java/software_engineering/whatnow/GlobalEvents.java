package software_engineering.whatnow;

import java.util.ArrayList;

/**
 * Created by jason on 5/23/2016.
 */
public class GlobalEvents extends WhatNowApplication{

    private ArrayList<Event> events = new ArrayList<Event>();

    public void appendEvent(Event event){
        events.add(event);
    }

    public void updateEvent(String key, Event event){
        for(Event currEvent : events) {
            if(key.equals(currEvent.getKey())) {
                events.remove(currEvent);
                break;
            }
        }
        appendEvent(event);
    }

    public void deleteEvent(String key){
        for(Event currEvent : events) {
            if(key.equals(currEvent.getKey())) {
                events.remove(currEvent);
                break;
            }
        }
    }

    public ArrayList<Event> getEventList() { return events; }

}
