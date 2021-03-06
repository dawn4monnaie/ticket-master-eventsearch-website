package com.example.ydd.csci571ddy;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class FavoritesTab extends Fragment implements EventAdapter.OnItemClickListener, EventAdapter.OnFavoriteButtonClickListener{
    private final static String TAG = "Favorites";

    private RecyclerView favoritesRecyclerView;
    private FavoritesManager mFavoritesManager;
    private EventAdapter mAdapter;
    private ArrayList<EventItem> favoritesList;
    private TextView noFavorites;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_tab, container, false);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recyclerView);
        mFavoritesManager = new FavoritesManager();
        noFavorites = view.findViewById(R.id.no_favorites);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        favoritesList = new ArrayList<>();
        ArrayList<FavoriteEventItem> favorites = mFavoritesManager.getAllFavoritePlaces(getActivity());
        favoritesList.addAll(favorites);
        mAdapter = new EventAdapter(getActivity(), favoritesList, EventAdapter.FAVORITES_LIST);

        if (favorites.size() == 0) {
            noFavorites.setVisibility(View.VISIBLE);
            Log.d(TAG, "noFavorites set visible in onResume");
//            if (mAdapter != null) {
//                favoritesRecyclerView.setAdapter(mAdapter);
//                Log.d(TAG, "mAdapter get item count:" + mAdapter.getItemCount());
//            }
//            return;
        } else {
            noFavorites.setVisibility(View.GONE);
        }

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnFavoriteButtonClickListener(this);
        favoritesRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        favoritesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getActivity(), "Not network connection, try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
        EventItem placeItem = favoritesList.get(position);
        String placeId = placeItem.getEventId();
        Log.d("results", "no." + position + " place is clicked!");
        Log.d("results", "place_id: " + placeId);
        detailsIntent.putExtra("place_id", placeId);
        detailsIntent.putExtra("place_name", placeItem.getName());
        Gson gson = new Gson();
        String json = gson.toJson(placeItem);
        detailsIntent.putExtra("place_item", json);
        startActivity(detailsIntent);
    }

    @Override
    public void onFavoriteButtonClick(int position) {
        EventItem place = favoritesList.get(position);
        Log.d(TAG, "placed removed:" + place);
        mFavoritesManager.removeFromFavorites(getActivity(), place);
        mAdapter.removeItemAt(position);
        if (mAdapter.getItemCount() == 0) {
//        if (favoritesList.size() == 0) { //this check is also doable
            noFavorites.setVisibility(View.VISIBLE);
            Log.d(TAG, "noFavorites set visible in onFavoriteButtonClick");
        }
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens");
            }
        }
    }
}
