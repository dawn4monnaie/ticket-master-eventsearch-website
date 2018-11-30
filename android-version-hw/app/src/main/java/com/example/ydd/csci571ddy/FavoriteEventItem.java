package com.example.ydd.csci571ddy;

import java.util.Date;

public class FavoriteEventItem extends EventItem {
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public FavoriteEventItem(String imageUrl, String eventName, String addr, String eventID, String date) {
        super(imageUrl, eventName, addr, eventID, date);
        timestamp = new Date().getTime();
    }

    public FavoriteEventItem(EventItem event) {
        super(event.getIconUrl(), event.getName(), event.getAddress(), event.getEventId(), event.getDate());
        timestamp = new Date().getTime();
    }

    @Override
    public String toString() {
        return "FavoriteEventItem{" +
                "iconUrl='" + getIconUrl() + '\'' +
                ", name='" + getName() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", eventId='" + getEventId() + '\'' +
                ", date='" + getDate() + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }




}
