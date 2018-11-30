package com.example.ydd.csci571ddy;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class UpcomingItem {
    private String displayName;
    private String uri;
    private String artist;
    private String date;
    private String type;

    public UpcomingItem(String displayName, String uri, String artist, String date, String type) {
        this.displayName = displayName;
        this.uri = uri;
        this.artist = artist;
        this.date = date;
        this.type = type;
    }


    @Override
    public String toString() {
        String str = "displayName: " + displayName + "\n";
        str += "uri: " + uri + "\n";
        str += "artist: " + artist + "\n";
        str += "date: " + date + "\n";
        str += "type: " + type + "\n";
        return str;
    }


    public static Comparator<UpcomingItem> upcomingNameComparator = new Comparator<UpcomingItem>() {
        @Override
        public int compare(UpcomingItem o1, UpcomingItem o2) {
            return o1.displayName.toLowerCase().compareTo(o2.displayName.toLowerCase());
        }
    };

    public static Comparator<UpcomingItem> upcomingDateComparator = new Comparator<UpcomingItem>() {
        @Override
        public int compare(UpcomingItem o1, UpcomingItem o2) {

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy HH:MM:SS");

            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = sdf.parse(o1.date);
            } catch (ParseException e) {
                try {
                    date1 = sdf2.parse(o1.date);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                date2 = sdf.parse(o2.date);
            } catch (ParseException e) {
                try {
                    date2 = sdf2.parse(o2.date);
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }

            return date1.compareTo(date2);
        }
    };

    public static Comparator<UpcomingItem> upcomingArtistComparator = new Comparator<UpcomingItem>() {
        @Override
        public int compare(UpcomingItem o1, UpcomingItem o2) {
            return o1.artist.toLowerCase().compareTo(o2.artist.toLowerCase());
        }
    };

    public static Comparator<UpcomingItem> upcomingTypeComparator = new Comparator<UpcomingItem>() {
        @Override
        public int compare(UpcomingItem o1, UpcomingItem o2) {
            return o1.type.toLowerCase().compareTo(o2.type.toLowerCase());
        }
    };


    public String getUri() {
        return uri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getArtist() {
        return artist;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }
}

