package com.example.ydd.csci571ddy;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;



public class InfoFragment extends Fragment {

    private String detailsJson;

    private TextView artistteamsnameTextView;
    private TextView venuesnameTextView;
    private TextView timeTextView;
    private TextView categoryTextView;
    private TextView statusTextView;
    private TextView ticketpageTextView;
    private TextView seatmapTextView;
    private TextView priceTextView;

    private TableRow artistteamsnameRowTextView;
    private TableRow venuesnameRowTextView;
    private TableRow timeRowTextView;
    private TableRow categoryRowTextView;
    private TableRow statusRowTextView;
    private TableRow ticketpageRowTextView;
    private TableRow seatmapRowTextView;
    private TableRow priceRowTextView;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment InfoFragment.
     */
    public static InfoFragment newInstance(String detailsJson) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString("details", detailsJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailsJson = getArguments().getString("details");
            Log.d("info", "onCreate details json: " + detailsJson);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        artistteamsnameTextView  = rootView.findViewById(R.id.info_artistteamsname);
        venuesnameTextView  = rootView.findViewById(R.id.info_venuesname);
        timeTextView  = rootView.findViewById(R.id.info_time);
        categoryTextView  = rootView.findViewById(R.id.info_category);
        statusTextView  = rootView.findViewById(R.id.info_status);
        ticketpageTextView  = rootView.findViewById(R.id.info_ticketpage);
        seatmapTextView  = rootView.findViewById(R.id.info_seatmap);
        priceTextView = rootView.findViewById(R.id.info_pricerange);

        artistteamsnameRowTextView  = rootView.findViewById(R.id.info_artistteamsname_row);
        venuesnameRowTextView  = rootView.findViewById(R.id.info_venuesname_row);
        timeRowTextView  = rootView.findViewById(R.id.info_time_row);
        categoryRowTextView  = rootView.findViewById(R.id.info_category_row);
        statusRowTextView  = rootView.findViewById(R.id.info_status_row);
        ticketpageRowTextView  = rootView.findViewById(R.id.info_ticketpage_row);
        seatmapRowTextView  = rootView.findViewById(R.id.info_seatmap_row);
        priceRowTextView = rootView.findViewById(R.id.info_pricerange_row);



        try {
            parseJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void parseJSON() throws JSONException{
        JSONObject detailsJsonObj = new JSONObject(detailsJson);

        /*
            private TextView artistteamsnameTextView;
            private TextView venuesnameTextView;
            private TextView timeTextView;
            private TextView categoryTextView;
            private TextView priceTextView;
            private TextView statusTextView;
            private TextView ticketpageTextView;
            private TextView seatmapTextView;
         */

        Boolean exist = false;
        ArrayList<String> artistteamsArray = new ArrayList<>();
        String event_artistname = "";

        ArrayList<String> venuesnameArray = new ArrayList<>();
        String event_venuesname = "";

        if (detailsJsonObj.has("_embedded")){
            JSONObject embedded = detailsJsonObj.getJSONObject("_embedded");
            exist = false;
            if (embedded.has("attractions")){
                JSONArray tmp = embedded.getJSONArray("attractions");
                for(int i = 0; i < tmp.length(); i++) {
                    JSONObject component = tmp.getJSONObject(i);
                    String name = component.getString("name");
                    artistteamsArray.add(name);
                }
                if (artistteamsArray.size() != 0){
                    event_artistname = TextUtils.join(" | ", artistteamsArray);
                    artistteamsnameTextView.setText(event_artistname);
                    exist = true;
                }
            }
            if (!exist){
                venuesnameRowTextView.setVisibility(View.GONE);
            }

            exist = false;
            if (embedded.has("venues")){
                JSONArray tmp = embedded.getJSONArray("venues");
                for(int i = 0; i < tmp.length(); i++) {
                    JSONObject component = tmp.getJSONObject(i);
                    String name = component.getString("name");
                    venuesnameArray.add(name);
                }
                if (venuesnameArray.size() != 0){
                    event_venuesname = TextUtils.join(" | ", venuesnameArray);
                    venuesnameTextView.setText(event_venuesname);
                    exist = true;
                }
            }
            if (!exist){
                venuesnameRowTextView.setVisibility(View.GONE);
            }
        }


        String event_date_localDate = "";
        String event_date_localTime = "";
        exist = false;
        if (detailsJsonObj.has("dates")){
            JSONObject dates = detailsJsonObj.getJSONObject("dates");
            if (dates.has("start")){
                JSONObject start = dates.getJSONObject("start");
                if (start.has("localDate")){
                    event_date_localDate = start.getString("localDate");
                }
                if (start.has("localTime")){
                    event_date_localTime = start.getString("localTime");
                }
                Log.d("localDate", event_date_localDate);
                Log.d("localTime", event_date_localTime);
                Date date = new Date();
                String date2 = "";
                try {
                    String pattern = "yyyy-MM-dd";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
                    date = simpleDateFormat.parse(event_date_localDate);
                    simpleDateFormat.applyPattern("MMM dd, yyyy");
                    date2 = simpleDateFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // event_date_localTime is pending!!!!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~!-~~~~
                String tmp = date2 + " " + event_date_localTime;
                timeTextView.setText(tmp);
                exist = true;
            }
        }
        if (!exist){
            timeRowTextView.setVisibility(View.GONE);
        }


        String event_category="";
        exist = false;
        if (detailsJsonObj.has("classifications")){
            JSONArray classifications = detailsJsonObj.getJSONArray("classifications");
            if (classifications.length() != 0) {
                JSONObject temp_catergory = classifications.getJSONObject(0);
                if (temp_catergory.has("genre")){
                    event_category += temp_catergory.getJSONObject("genre").getString("name");
                }
                if (temp_catergory.has("segment")){
                    String segmentname = temp_catergory.getJSONObject("segment").getString("name");
                    event_category += " | " + segmentname;
                }
                categoryTextView.setText(event_category);
                exist = true;
            }
        }
        if (!exist){
            categoryRowTextView.setVisibility(View.GONE);
        }

        String event_prices="";
        if (detailsJsonObj.has("priceRanges")) {
            JSONArray priceRanges = detailsJsonObj.getJSONArray("priceRanges");
            if (priceRanges.length() != 0) {
                JSONObject temp_prices = priceRanges.getJSONObject(0);
                String currency = "$";
                if (temp_prices.has("currency")){
                    currency = temp_prices.getString("currency");
                }
                Boolean temp_flag = false;
                if (temp_prices.has("min")){
                    event_prices += currency + temp_prices.getString("min");
                    temp_flag = true;
                }
                if (temp_flag){
                    event_prices += " ~ ";
                }

                if (temp_prices.has("max")){
                    event_prices += currency + temp_prices.getString("max");
                }
            }
        }
        if (!event_prices.equals("")) {
            priceTextView.setText(event_prices);
        } else {
            priceRowTextView.setVisibility(View.GONE);
        }


        String event_status = "";
        exist = false;
        if (detailsJsonObj.has("dates")){
            if (detailsJsonObj.getJSONObject("dates").has("status")){
                if (detailsJsonObj.getJSONObject("dates").getJSONObject("status").has("code")){
                    event_status = detailsJsonObj.getJSONObject("dates").getJSONObject("status").getString("code");
                }
                statusTextView.setText(event_status);
                exist = true;
            }
        }
        if (!exist){
            statusRowTextView.setVisibility(View.GONE);
        }


        String event_url = "";
        if (detailsJsonObj.has("url")){
            event_url = detailsJsonObj.getString("url");
            Spanned event_url_hyperlink = Html.fromHtml("<a href=\"" + event_url+ "\">Ticketmaster</a>");
            ticketpageTextView.setMovementMethod(LinkMovementMethod.getInstance());
            ticketpageTextView.setText(event_url_hyperlink);
        } else {
            ticketpageRowTextView.setVisibility(View.GONE);
        }


        String event_seatmap = "";
        if (detailsJsonObj.has("seatmap") && (detailsJsonObj.getJSONObject("seatmap").has("staticUrl"))  ){
            event_seatmap = detailsJsonObj.getJSONObject("seatmap").getString("staticUrl");

            Spanned event_seatmap_hyperlink = Html.fromHtml("<a href=\"" + event_seatmap+ "\">View Here</a>");
            seatmapTextView.setMovementMethod(LinkMovementMethod.getInstance());
            seatmapTextView.setText(event_seatmap_hyperlink);
        } else{
            seatmapRowTextView.setVisibility(View.GONE);
        }

    }


}
