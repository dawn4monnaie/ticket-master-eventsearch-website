package com.example.ydd.csci571ddy;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ArtistFragment extends Fragment{
    private final static String TAG = "Photos";

    private ArrayList<String> ARTISTSJSONARRAY;
    private ArrayList<String>  PHOTOSJSONARRAY;
    private ArrayList<String>  ARTISTNAME;

    RecyclerView artistRecyclerView;

    public ArtistFragment() {
    }

    ArtistAdapter artistAdapter;

    public static ArtistFragment newInstance(ArrayList<String> ARTISTNAME, ArrayList<String> ARTISTSJSONARRAY, ArrayList<String> PHOTOSJSONARRAY) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();

        args.putStringArrayList("artistname", ARTISTNAME);
        args.putStringArrayList("artistjson", ARTISTSJSONARRAY);
        args.putStringArrayList("photojson", PHOTOSJSONARRAY);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARTISTNAME = getArguments().getStringArrayList("artistname");
        ARTISTSJSONARRAY = getArguments().getStringArrayList("artistjson");
        PHOTOSJSONARRAY = getArguments().getStringArrayList("photojson");

        Log.d("artistname", "onCreate Artistname: " + ARTISTNAME.toString());
        Log.d("artistjson", "onCreate Artist: " + ARTISTSJSONARRAY.toString());
        Log.d("photosjson", "onCreate Photos: " + PHOTOSJSONARRAY.toString());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);
        artistRecyclerView = rootView.findViewById(R.id.artist_recyclerView);
        artistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        artistAdapter = new ArtistAdapter(getActivity(), ARTISTNAME, ARTISTSJSONARRAY, PHOTOSJSONARRAY);
        artistRecyclerView.setAdapter(artistAdapter);

        return rootView;
    }


}
