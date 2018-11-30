package com.example.ydd.csci571ddy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity implements EventAdapter.OnItemClickListener{

    private ArrayList<EventItem> resultsList;
    private Toolbar mToolbar;
    private RecyclerView resultsRecyclerView;

    private EventAdapter eventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        resultsList = new ArrayList<>();
        Intent mIntent = getIntent();
        String resultsJSON = mIntent.getStringExtra("resultsJSON");
        Log.d("results_received ", "onCreate: received results JSON: " + resultsJSON);


        mToolbar =  (Toolbar) findViewById(R.id.toolbarResults);
        setSupportActionBar(mToolbar);
        addListenerOnNavigation();

        resultsRecyclerView = findViewById(R.id.results_RecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            JSONObject resultsJsonObj = new JSONObject(resultsJSON);
            parseJSON(resultsJsonObj);
        } catch (JSONException e) {
            Log.e("results", "could not parse JSON");
            e.printStackTrace();
            findViewById(R.id.no_result).setVisibility(View.VISIBLE);


        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (eventAdapter != null) {
            eventAdapter.notifyDataSetChanged();
        }
    }


    private void parseJSON(JSONObject jsonObj) throws JSONException{
        JSONArray resultsJsonArray =  jsonObj.getJSONObject("_embedded").getJSONArray("events");

        if (resultsJsonArray.length() == 0) {
            findViewById(R.id.no_result).setVisibility(View.VISIBLE);
            // findViewById(R.id.paginationButtons).setVisibility(View.GONE);
            return;
        }


        resultsList.clear();

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject event = resultsJsonArray.getJSONObject(i);

            JSONArray temp = event.getJSONArray("classifications");
            String type = temp.getJSONObject(0).getJSONObject("segment").getString("name");
            String iconUrl = "";
            // "All", "Music", "Sports", "Art & Theatre", "Film", "Miscellanenous"

            Log.d("type", type);
            if (type.toLowerCase().equals("music")) {
                iconUrl = "1";
            } else if (type.toLowerCase().equals("sports")) {
                iconUrl = "2";
            } else if (type.toLowerCase().equals("art & theatre")) {
                iconUrl = "3";
            } else if (type.toLowerCase().equals("film")) {
                iconUrl = "4";
            } else  {
                iconUrl = "5";
            }


            String date = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
            String eventName = event.getString("name");
            // ['_embedded']['venues']['0']['name']
            String address = event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            String eventId = event.getString("id");

            Log.d("parse_json_meta_dat", iconUrl);
            Log.d("parse_json_meta_dat", date);
            Log.d("parse_json_meta_dat", eventName);
            Log.d("parse_json_meta_dat", address);
            Log.d("parse_json_meta_dat", eventId);

            resultsList.add(new EventItem(iconUrl, eventName, address, eventId, date));
        }

        Log.d("results", "resultsList length: " + resultsList.size());
        eventAdapter = new EventAdapter(this, resultsList, EventAdapter.RESULTS_LIST);
        eventAdapter.setOnItemClickListener(SearchResultsActivity.this);

        resultsRecyclerView.setAdapter(eventAdapter);


    }


    private void addListenerOnNavigation() {
        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(this, "Not network connection, try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent detailsIntent = new Intent(this, DetailsActivity.class);
        EventItem placeItem = resultsList.get(position);
        String placeId = placeItem.getEventId();
        Log.d("results", "no." + position + " place is clicked!");
        Log.d("results", "place_id: " + placeId);
        Gson gson = new Gson();
        String json = gson.toJson(placeItem);
        detailsIntent.putExtra("place_id", placeId);
        detailsIntent.putExtra("place_name", placeItem.getName());
        detailsIntent.putExtra("place_item", json);
        startActivity(detailsIntent);
    }


}
