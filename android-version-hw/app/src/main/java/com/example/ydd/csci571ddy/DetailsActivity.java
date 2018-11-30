package com.example.ydd.csci571ddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;

public class DetailsActivity extends AppCompatActivity {

    final String serverURL = "https://csci-571-hw8-dawn.appspot.com/";

    final String REQUEST_DETAILS_URL = serverURL + "eventdetails";
    final String REQUEST_VENUE_URL = serverURL + "venues";
    final String REQUEST_VENUEID_URL = serverURL + "venuesdetail";
    final String REQUEST_PHOTO_URL = serverURL + "photos";
    final String REQUEST_ARTIST_URL = serverURL + "artistdetails";


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    String eventName;
    private String eventId;
    private String detailsJson;
    private JSONObject detailsJsonObj;

    private Menu menu;
    Toolbar toolbar;

    private String twitterUrl;

    private EventItem mEventItem;
    private FavoritesManager mFavoritesManager;
    private boolean isFavorited;

    private ProgressDialog progressDialog;

    private ArrayList<String> artistteamsArray;

    private ArrayList<String> ARTISTSJSONARRAY;
    Boolean artistFlag = false;
    private ArrayList<String> PHOTOSJSONARRAY;
    private String VENUEJSON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("place_id");
        eventName = intent.getStringExtra("place_name");
        String json = intent.getStringExtra("place_item");
        mEventItem = new Gson().fromJson(json, EventItem.class);
        Log.d("details", "onCreate: placeId: " + eventId);

        getEventDetails();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(eventName);
        addListenerOnNavigation();

        mFavoritesManager = new FavoritesManager();
        isFavorited = mFavoritesManager.isFavorited(this, eventId);

    }



    private void getEventDetails() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching details");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        Log.d("fetch details", "eventId: " + eventId);

        RequestParams params = new RequestParams();
        params.put("event_id", eventId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(REQUEST_DETAILS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    detailsJsonObj = response;
                    fetchTKDetails();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // createTwitterContent();
                detailsJson = response.toString();

                Log.d("Fetch details(0)", "Success! JSON: " + detailsJson);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("Fetch details", "Fail " + e.toString());
                Log.d("Fetch details", "Status code " + statusCode);
            }

        });


    }


    private void fetchTKDetails() throws JSONException {


        artistteamsArray = new ArrayList<>();
        ARTISTSJSONARRAY = new ArrayList<String>();
        PHOTOSJSONARRAY = new ArrayList<String>();
        artistFlag = false;


        JSONObject embedded = detailsJsonObj.getJSONObject("_embedded");
        if (embedded.has("attractions")) {
            JSONArray tmp = embedded.getJSONArray("attractions");
            for (int i = 0; i < tmp.length(); i++) {
                JSONObject component = tmp.getJSONObject(i);
                String name = component.getString("name");
                artistteamsArray.add(name);
            }
        }
        JSONArray classifications = detailsJsonObj.getJSONArray("classifications");
        if (classifications.length() != 0) {
            JSONObject temp_catergory = classifications.getJSONObject(0);
            if (temp_catergory.has("segment")){
                String segmentname = temp_catergory.getJSONObject("segment").getString("name");
                if (segmentname.toLowerCase().equals("music")){
                    artistFlag = true;
                }
            }
        }
        // String detailsObjectEventId = detailsJsonObj.getString("id");


        AsyncHttpClient client = new AsyncHttpClient();
        client.setThreadPool(Executors.newSingleThreadExecutor());

        for(int i = 0; i < artistteamsArray.size(); i++) {
            String detailsObjectName = artistteamsArray.get(i);
            if (artistFlag) {
                RequestParams params_artist = new RequestParams();
                params_artist.put("keyword", detailsObjectName);

                // requestArtistFromServer(params_artist);
                client.get(REQUEST_ARTIST_URL, params_artist, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.d("TicketMaster", "Success! JSON: " + response.toString());
                        ARTISTSJSONARRAY.add(response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                        Log.d("REQUEST_ARTIST_URL", "Fail " + e.toString());
                        Log.d("REQUEST_ARTIST_URL", "Status code " + statusCode);
                    }
                });
            }


            RequestParams params_photo = new RequestParams();
            String photodetailsObjectName = detailsObjectName.replaceAll(" ", "+");
            params_photo.put("keyword", photodetailsObjectName);

            //requestPhotoFromServer(params_photo);
            client.get(REQUEST_PHOTO_URL, params_photo, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("requestPhotoFromServer", "Success! JSON: " + response.toString());
                    PHOTOSJSONARRAY.add(response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.d("REQUEST_PHOTO_URL", "Fail " + e.toString());
                    Log.d("REQUEST_PHOTO_URL", "Status code " + statusCode);
                }
            });



        }

        String venuename = detailsJsonObj.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
        RequestParams params_venue = new RequestParams();
        params_venue.put("venuename", venuename);

        // requestVenueFromServer(params_venue);
        client.get(REQUEST_VENUE_URL, params_venue, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("TicketMaster", "Success! JSON: " + response.toString());
                // yelpReviewsJson = response.toString();
                RequestParams params_venueid = new RequestParams();
                String id = null;
                try {
                    id = response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("venue").getJSONObject(0).getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params_venueid.put("id", id);
                requestVenueIDFromServer(params_venueid);
                createTwitterContent();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("TicketMaster", "Fail " + e.toString());
                Log.d("TicketMaster", "Status code " + statusCode);
            }
        });

    }





    private void requestVenueIDFromServer(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(REQUEST_VENUEID_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("TicketMaster", "Success! JSON: " + response.toString());
                VENUEJSON = response.toString();
                updateUI();
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("REQUEST_VENUEID_URL", "Fail " + e.toString());
                Log.d("REQUEST_VENUEID_URL", "Status code " + statusCode);
            }

        });
    }






    private void requestVenueFromServer(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(REQUEST_VENUE_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("TicketMaster", "Success! JSON: " + response.toString());
                // yelpReviewsJson = response.toString();
                RequestParams params_venueid = new RequestParams();
                String id = null;
                try {
                    id = response.getJSONObject("resultsPage").getJSONObject("results").getJSONArray("venue").getJSONObject(0).getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params_venueid.put("id", id);
                requestVenueIDFromServer(params_venueid);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("TicketMaster", "Fail " + e.toString());
                Log.d("TicketMaster", "Status code " + statusCode);
            }
        });
    }

    private void requestPhotoFromServer(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(REQUEST_PHOTO_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("TicketMaster", "Success! JSON: " + response.toString());
                PHOTOSJSONARRAY.add(response.toString());
                updateUI();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("REQUEST_PHOTO_URL", "Fail " + e.toString());
                Log.d("REQUEST_PHOTO_URL", "Status code " + statusCode);
            }
        });

    }

    private void requestArtistFromServer(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(REQUEST_ARTIST_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("TicketMaster", "Success! JSON: " + response.toString());
                ARTISTSJSONARRAY.add(response.toString());
                updateUI();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("REQUEST_ARTIST_URL", "Fail " + e.toString());
                Log.d("REQUEST_ARTIST_URL", "Status code " + statusCode);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        if (isFavorited) {
            menu.getItem(1).setIcon(R.drawable.heart_fill_white);
        } else {
            menu.getItem(1).setIcon(R.drawable.heart_outline_white);
        }

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            if(isFavorited) {
                mFavoritesManager.removeFromFavorites(this, mEventItem);
                item.setIcon(R.drawable.heart_outline_white);
            } else {
                mFavoritesManager.addToFavorites(this, mEventItem);
                item.setIcon(R.drawable.heart_fill_white);
            }
            isFavorited = !isFavorited;
            return true;
        }
        if (id == R.id.action_share) {
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
            if (twitterIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(twitterIntent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    private void addListenerOnNavigation() {
        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void createTwitterContent() {
        try {
            String address = detailsJsonObj.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            String website = detailsJsonObj.getString("url");

            twitterUrl = "https://twitter.com/intent/tweet?";
            String text = "Check out " + eventName + " located at " + address + "\nWebsite:";
            text = URLEncoder.encode(text, "UTF-8");
            website = URLEncoder.encode(website, "UTF-8");
            twitterUrl += "text=" + text + "&url=" + website + "&hashtags=CSCI571EventSearch";
            Log.d("details", "twitter url: " + twitterUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void updateUI() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.detailscontainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }




    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Log.d("details0", "send details to info: " + detailsJson);
                    return InfoFragment.newInstance(detailsJson);
                case 1:
                    Log.d("details1", "send details to info: " + detailsJson);
                    Log.d("details1", "send ARTISTSJSONARRAY to info: " + ARTISTSJSONARRAY.toString());
                    Log.d("details1", "send PHOTOSJSONARRAY to info: " + PHOTOSJSONARRAY.toString());
                    return ArtistFragment.newInstance(artistteamsArray, ARTISTSJSONARRAY, PHOTOSJSONARRAY);
                case 2:
                    Log.d("details2", "send details to info: " + detailsJson);
                    return MapFragment.newInstance(detailsJson);
                case 3:
                    Log.d("details3", "send VENUEJSON to info: " + VENUEJSON);
                    return UpcomingFragment.newInstance(VENUEJSON);
            }

            return null;

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

}
