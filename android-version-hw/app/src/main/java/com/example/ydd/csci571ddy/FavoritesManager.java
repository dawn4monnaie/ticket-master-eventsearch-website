package com.example.ydd.csci571ddy;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class FavoritesManager {
    private final static String PREFERENCE_FILE_KEY = "com.example.csci571ddy.event_search.favorites_preference_file";
    private Gson mGson;
    public FavoritesManager() {
        mGson = new Gson();
    }

    public void onResultsFavoriteButtonClick(Context context, EventItem place, ImageView favoBtn) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String placeId = place.getEventId();
        if (sharedPref.contains(placeId)) {
            editor.remove(placeId);
            favoBtn.setImageResource(R.drawable.heart_outline_black);
            String text = place.getName() + " was removed from favorites";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            FavoriteEventItem favoritePlace = new FavoriteEventItem(place);
            String json = mGson.toJson(favoritePlace);
            editor.putString(placeId, json);
            favoBtn.setImageResource(R.drawable.heart_fill_red);
            String text = place.getName() + " was added to favorites";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
        editor.commit();
    }

    public void removeFromFavorites(Context context, EventItem eventItem) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(eventItem.getEventId());
        editor.commit();
        String text = eventItem.getName() + " was removed from favorites";
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


    public void addToFavorites(Context context, EventItem event) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String placeId = event.getEventId();
        FavoriteEventItem favoritePlace = new FavoriteEventItem(event);
        String json = mGson.toJson(favoritePlace);
        editor.putString(placeId, json);
        editor.commit();
        String text = event.getName() + " was added to favorites";
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


    public boolean isFavorited(Context context, String placeId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.contains(placeId);
    }


    public ArrayList<FavoriteEventItem> getAllFavoritePlaces(Context context) {
        ArrayList<FavoriteEventItem> favorites = new ArrayList<>();
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String,?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            FavoriteEventItem favorite = mGson.fromJson(json, FavoriteEventItem.class);
            favorites.add(favorite);
        }

        Collections.sort(favorites, new Comparator<FavoriteEventItem>() {
            @Override
            public int compare(FavoriteEventItem o1, FavoriteEventItem o2) {
                return (int) (o1.getTimestamp() - o2.getTimestamp());
            }
        });



        return favorites;

    }


}
