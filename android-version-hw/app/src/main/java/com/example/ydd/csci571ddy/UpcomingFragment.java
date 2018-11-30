package com.example.ydd.csci571ddy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class UpcomingFragment extends Fragment{

    private String[] upcomingTypes = {"Default", "Event Name", "Time", "Artist", "Type"};
    private String[] upcomingOrder = {
            "Ascending",
            "Descending"
    };

    private Spinner upcomingTypeSpinner;
    private Spinner upcomingOrderSpinner;

    public UpcomingFragment() {
    }

    public static UpcomingFragment newInstance(String VENUEJSON) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putString("venuejson", VENUEJSON);
        fragment.setArguments(args);
        return fragment;
    }


    private ArrayList<UpcomingItem> upcomingsOnDisplay;

    private RecyclerView mRecyclerView;
    private UpcomingAdapter mUpcomingAdapter;
    private TextView noUpcomingText;

    private JSONObject venueJson;
    private ArrayList<UpcomingItem> Upcomings;
    private boolean UpcomingExist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);
        noUpcomingText = rootView.findViewById(R.id.no_upcoming);

        upcomingsOnDisplay = new ArrayList<>();

        String details = getArguments().getString("venuejson");
        UpcomingExist = false;
        try {
            venueJson = new JSONObject(details);
            parseVenueJson(venueJson);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        upcomingTypeSpinner = rootView.findViewById(R.id.upcoming_type_spinner);
        upcomingOrderSpinner = rootView.findViewById(R.id.upcoming_order_spinner);

        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, upcomingTypes);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upcomingTypeSpinner.setAdapter(typesAdapter);

        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, upcomingOrder);
        ordersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upcomingOrderSpinner.setAdapter(ordersAdapter);

        AdapterView.OnItemSelectedListener mListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateRecyclerView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        upcomingTypeSpinner.setOnItemSelectedListener(mListener);
        upcomingOrderSpinner.setOnItemSelectedListener(mListener);

        mRecyclerView = rootView.findViewById(R.id.upcoming_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // after get the new sorted Upcoming Events -> Default!
        mUpcomingAdapter = new UpcomingAdapter(Upcomings);
        addOnItemClickListenerOnRecyclerView();
        mRecyclerView.setAdapter(mUpcomingAdapter);

        return rootView;
    }



    // get a ArrayList < Event >
    private void parseVenueJson(JSONObject venueJson) throws JSONException, ParseException {
        Upcomings = new ArrayList<UpcomingItem>();
        UpcomingExist = true;

        if (venueJson.getJSONObject("resultsPage").getJSONObject("results").has("event")){
            JSONArray events = venueJson.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("event");
            for(int i = 0; i < events.length(); i++){
                JSONObject event = events.getJSONObject(i);
                String displayName = event.getString("displayName");
                String uri = event.getString("uri");
                String artist = "";
                if (event.getJSONArray("performance").length() != 0){
                    artist = event.getJSONArray("performance").getJSONObject(0).getString("displayName");
                }
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
                Date date_raw = simpleDateFormat.parse(event.getJSONObject("start").getString("date"));
                simpleDateFormat.applyPattern("MMM dd, yyyy");
                String date = simpleDateFormat.format(date_raw);
                if (event.getJSONObject("start").has("time")) {
                    String time = event.getJSONObject("start").getString("time");
                    if (!time.equals("null"))
                        date += " " + time;
                }
                String type = event.getString("type");

                UpcomingItem upcomingitem = new UpcomingItem(displayName, uri, artist, date, type);
                Upcomings.add(upcomingitem);
            }
        }
    }


    private void updateRecyclerView() {
        noUpcomingText.setVisibility(View.GONE);
        int upcomingTypeNum = upcomingTypeSpinner.getSelectedItemPosition();
        int upcomingOrderNum = upcomingOrderSpinner.getSelectedItemPosition();

        upcomingsOnDisplay = new ArrayList<>(Upcomings);

        if(upcomingsOnDisplay.size() == 0) {
            noUpcomingText.setVisibility(View.VISIBLE);
        }

        upcomingOrderSpinner.setEnabled(true);
        switch (upcomingTypeNum) {
            // "Default", "Event Name", "Time", "Artist", "Type"
            case 0:
                upcomingOrderSpinner.setEnabled(false);
                break;
            case 1:
                Collections.sort(upcomingsOnDisplay, UpcomingItem.upcomingNameComparator);
            case 2:
                // Collections.sort(upcomingsOnDisplay, UpcomingItem.upcomingDateComparator);
                break;
            case 3:
                Collections.sort(upcomingsOnDisplay, UpcomingItem.upcomingArtistComparator);
            case 4:
                Collections.sort(upcomingsOnDisplay, UpcomingItem.upcomingTypeComparator);
        }

        switch (upcomingOrderNum) {
            case 0:
                break;
            case 1:
                Collections.reverse(upcomingsOnDisplay);
        }
        mUpcomingAdapter = new UpcomingAdapter(upcomingsOnDisplay);
        addOnItemClickListenerOnRecyclerView();
        mRecyclerView.setAdapter(mUpcomingAdapter);
    }



    private void addOnItemClickListenerOnRecyclerView() {
        mUpcomingAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // int pos = mRecyclerView.getChildAdapterPosition(v);
               // String url = upcomingsOnDisplay.get(pos).getUri();
               // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
               // startActivity(intent);
            }
        });
    }


}