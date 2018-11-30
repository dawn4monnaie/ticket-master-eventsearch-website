package com.example.ydd.csci571ddy;

public class EventItem {

    private String iconUrl;
    private String name;
    private String address;
    private String eventId;
    private String date;

    public EventItem(String imageUrl, String eventName, String addr, String eventID, String date_tmp) {
        iconUrl = imageUrl;
        name = eventName;
        address = addr;
        eventId = eventID;
        date = date_tmp;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEventId() {
        return eventId;
    }

    public String getDate() {
        return date;
    }


    @Override
    public String toString() {
        return "EventItem{" +
                "iconUrl='" + iconUrl + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", eventId='" + eventId + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

}
