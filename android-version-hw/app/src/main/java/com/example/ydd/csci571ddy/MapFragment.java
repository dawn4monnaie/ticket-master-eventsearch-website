package com.example.ydd.csci571ddy;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    // private final static String API_KEY = "AIzaSyB-UV6eMbgrMrfaZ4x4m0w1-stENc6fO-8";
    /*
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );
    */

    TextView venue_name;
    TextView venue_address;
    TextView venue_city;
    TextView venue_phone;
    TextView venue_openhour;
    TextView venue_generalrule;
    TextView venue_childrule;

    TableRow venue_name_row;
    TableRow venue_address_row;
    TableRow venue_city_row;
    TableRow venue_phone_row;
    TableRow venue_openhour_row;
    TableRow venue_generalrule_row;
    TableRow venue_childrule_row;

    private String venue_event_name="";
    private String venue_event_address="";
    private String venue_event_location="";
    private String venue_event_phoneNumber="";
    private String venue_event_openHours="";
    private String venue_event_generalRule="";
    private String venue_event_childlRule="";


    private GoogleMap mGoogleMap;
    private LatLng mLatLng;



    private JSONObject venue_event;

    public MapFragment() {
    }

    public static MapFragment newInstance(String detailsJson) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();

        args.putString("venuejson", detailsJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        try {


            String venuejson_tmp = getArguments().getString("venuejson");
            venue_event = new JSONObject(venuejson_tmp).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
            if (venue_event.has("name")){
                venue_event_name = venue_event.getString("name");
            }
            if (venue_event.has("address") && venue_event.getJSONObject("address").has("line1")){
                venue_event_address = venue_event.getJSONObject("address").getString("line1");
            }
            if (venue_event.has("city")){
                venue_event_location += venue_event.getJSONObject("city").getString("name");
            }
            if (venue_event.has("state")){
                if (venue_event.has("city")){
                    venue_event_location += ",";
                }
                venue_event_location += venue_event.getJSONObject("state").getString("name");
            }
            if (venue_event.has("boxOfficeInfo")){
                if (venue_event.getJSONObject("boxOfficeInfo").has("phoneNumberDetail")) {
                    venue_event_phoneNumber = venue_event.getJSONObject("boxOfficeInfo").getString("phoneNumberDetail");
                }
                if (venue_event.getJSONObject("boxOfficeInfo").has("openHoursDetail")) {
                    venue_event_openHours = venue_event.getJSONObject("boxOfficeInfo").getString("openHoursDetail");
                }
            }
            if (venue_event.has("generalInfo")){
                if (venue_event.getJSONObject("generalInfo").has("generalRule")) {
                    venue_event_generalRule = venue_event.getJSONObject("generalInfo").getString("generalRule");
                }
                if (venue_event.getJSONObject("generalInfo").has("childRule")) {
                    venue_event_childlRule = venue_event.getJSONObject("generalInfo").getString("childRule");
                }
            }


            venue_name = rootView.findViewById(R.id.venue_name);
            venue_address = rootView.findViewById(R.id.venue_address);
            venue_city = rootView.findViewById(R.id.venue_city);
            venue_phone = rootView.findViewById(R.id.venue_phone);
            venue_openhour = rootView.findViewById(R.id.venue_openhour);
            venue_generalrule = rootView.findViewById(R.id.venue_generalrule);
            venue_childrule = rootView.findViewById(R.id.venue_childrule);

            venue_name_row = rootView.findViewById(R.id.venue_name_row);
            venue_address_row = rootView.findViewById(R.id.venue_address_row);
            venue_city_row = rootView.findViewById(R.id.venue_city_row);
            venue_phone_row = rootView.findViewById(R.id.venue_phone_row);
            venue_openhour_row = rootView.findViewById(R.id.venue_openhour_row);
            venue_generalrule_row = rootView.findViewById(R.id.venue_generalrule_row);
            venue_childrule_row = rootView.findViewById(R.id.venue_childrule_row);


            if (!venue_event_name.equals("")){
                venue_name.setText(venue_event_name);
                venue_name_row.setVisibility(View.VISIBLE);
            } else {
                venue_name_row.setVisibility(View.GONE);
            }
            if (!venue_event_address.equals("")){
                venue_address.setText(venue_event_address);
                venue_address.setVisibility(View.VISIBLE);
            } else {
                venue_address_row.setVisibility(View.GONE);
            }
            if (!venue_event_location.equals("")){
                venue_city.setText(venue_event_location);
                venue_city.setVisibility(View.VISIBLE);
            } else {
                venue_city_row.setVisibility(View.GONE);
            }
            if (!venue_event_phoneNumber.equals("")){
                venue_phone.setText(venue_event_phoneNumber);
                venue_phone_row.setVisibility(View.VISIBLE);
            } else {
                venue_phone_row.setVisibility(View.GONE);
            }
            if (!venue_event_openHours.equals("")){
                venue_openhour.setText(venue_event_openHours);
                venue_openhour_row.setVisibility(View.VISIBLE);
            } else {
                venue_openhour_row.setVisibility(View.GONE);
            }
            if (!venue_event_generalRule.equals("")){
                venue_generalrule.setText(venue_event_generalRule);
                venue_generalrule_row.setVisibility(View.VISIBLE);
            } else {
                venue_generalrule_row.setVisibility(View.GONE);
            }
            if (!venue_event_childlRule.equals("")){
                venue_childrule.setText(venue_event_childlRule);
                venue_childrule_row.setVisibility(View.VISIBLE);
            } else {
                venue_childrule_row.setVisibility(View.GONE);
            }

            JSONObject location = venue_event.getJSONObject("location");
            double lat = location.getDouble("latitude");
            double lng = location.getDouble("longitude");
            mLatLng = new LatLng(lat, lng);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.addMarker(new MarkerOptions().position(mLatLng).title(venue_event_name));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));

    }


}