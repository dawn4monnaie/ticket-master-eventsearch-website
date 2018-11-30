package com.example.ydd.csci571ddy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;



public class SearchTab extends Fragment {
    final int REQUEST_CODE = 123;
    // https://app.ticketmaster.com/discovery/v2/events.json?apikey=uy2mh8pBbjq91kGF2qse3pSUWbw4oZAS&unit=miles&geoPoint=9q9hvumnu&keyword=Los+Angeles+Lakers&radius=100


    final String RESULTS_URL = "https://csci-571-hw8-dawn.appspot.com/events";
    final String LOCATION_URL = "https://csci-571-hw8-dawn.appspot.com/location";
    //final String RESULTS_URL = "http://localhost:3000/events";


    String[] categories = new String[]{
            "All", "Music", "Sports", "Art & Theatre", "Film", "Miscellanenous"
    };

    String[] units = new String[]{
            "miles", "kilometers"
    };


    LocationManager locationManager;
    LocationListener locationListener;

    double currentLat;
    double currentLon;

    RadioGroup radioLocationGroup;
    // AutoCompleteTextView keywordET;

    AutoCompleteTextView keywordET;

    Spinner spinnerCategories;
    Spinner spinnerUnits;

    EditText distanceET;
    EditText addressET;
    private TextView errorTextKeyword;
    private TextView errorTextAddress;
    private Button clearBtn;


    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );



    private static final int TRIGGER_AUTO_COMPLETE = 300;
    private static final long AUTO_COMPLETE_DELAY = 800;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_tab, container, false);

        keywordET = (AutoCompleteTextView) view.findViewById(R.id.keyword);


        autoSuggestAdapter = new AutoSuggestAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line);
        keywordET.setThreshold(2);
        keywordET.setAdapter(autoSuggestAdapter);
        keywordET.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //selectedText.setText(autoSuggestAdapter.getObject(position));
                        Log.d("postion", String.valueOf(autoSuggestAdapter.getObject(position)));
                    }});
        keywordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(keywordET.getText())) {
                        makeApiCall(keywordET.getText().toString());
                    }
                }
                return false;
            }
        });



        distanceET = (EditText) view.findViewById(R.id.distance);

        addressET = (EditText) view.findViewById(R.id.inputAddress);



        radioLocationGroup = (RadioGroup) view.findViewById(R.id.radioLocation);
        spinnerCategories = (Spinner) view.findViewById(R.id.spinnerCategories);

        spinnerUnits = (Spinner) view.findViewById(R.id.spinnerUnits);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);


        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, units);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnits.setAdapter(adapter2);


        errorTextAddress = view.findViewById(R.id.address_error_message);
        errorTextKeyword = view.findViewById(R.id.keyword_error_message);


        getCurrentLocation();
        addListenerOnRadioButtons();
        addListenerOnSearchButton(view);

        clearBtn = view.findViewById(R.id.buttonClear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearForm();
            }
        });

        return view;
    }



    private void makeApiCall(String text) {
        ApiCall.make(getActivity(), text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONObject("_embedded").getJSONArray("attractions");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }





    @Override
    public void onResume() {
        super.onResume();
        clearForm();
    }

    private void getCurrentLocation() {
        Log.d("search", "getting current location");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("search", "onLocationChanged");
                currentLat = location.getLatitude();
                currentLon = location.getLongitude();
                Log.d("search", "longitue is: " + currentLat);
                Log.d("search", "latitude is: " + currentLon);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000, locationListener);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("search", "onPermissionResult(): Permission granted!");
                getCurrentLocation();
            } else {
                Log.d("search", "onPermissionResult(): Permission denied!");
            }
        }
    }


    private void addListenerOnSearchButton(View view) {
        Button searchBtn = (Button) view.findViewById(R.id.buttonSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    Toast.makeText(getActivity(), "Not network connection, try again later", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (validateInputs()) {
                    String keyword = keywordET.getText().toString();
                    keyword = keyword.replaceAll(" ", "+");

                    Log.d("search", "keyword: " + keyword);
                    String category = String.valueOf(spinnerCategories.getSelectedItem());

                    // "All", "Music", "Sports", "Art & Theatre", "Film", "Miscellanenous"
                    /*
                        <option value="all"> All </option>
                        <option value="music"> Music </option>
                        <option value="sports"> Sports </option>
                        <option value="arttheatre"> Art & Theatre </option>
                        <option value="film"> Film </option>
                        <option value="miscellanenous"> Miscellanenous </option>
                     */


                    if (category.equals("Music")){
                        category = "music";
                    } else if (category.equals("Sports")){
                        category = "sports";
                    } else if (category.equals("Art & Theatre")){
                        category = "arttheatre";
                    } else if (category.equals("Film")){
                        category = "film";
                    } else if (category.equals("Miscellanenous")){
                        category = "miscellanenous";
                    } else {
                        category = "all";
                    }

                    String unit = String.valueOf(spinnerUnits.getSelectedItem());

                    Log.d("search", "category: " + category);
                    String distance = distanceET.getText().toString();
                    distance = distance.isEmpty() ? "10" : distance;
                    Log.d("search", "distance: " + distance);

                    if (unit.equals("kilometers")){
                        distance = String.valueOf(Math.round(Integer.parseInt(distance) * 0.6));
                    }



                    final RequestParams params_result = new RequestParams();
                    params_result.put("keyword", keyword);
                    params_result.put("distance", distance);
                    params_result.put("category", category);


                    String address;
                    if(radioLocationGroup.getCheckedRadioButtonId() == R.id.radioOtherLocation) {
                        address = addressET.getText().toString();
                        Log.d("search", "address: " + address);

                        RequestParams params = new RequestParams();
                        params.put("keyword", address);

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.get(LOCATION_URL, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.d("Search", "Success! JSON: " + response.toString());

                                try {
                                    JSONObject locObj = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");

                                    currentLat = locObj.getDouble("lat");
                                    currentLon = locObj.getDouble("lng");

                                    params_result.put("lat", currentLat);
                                    params_result.put("lng", currentLon);

                                    requestResultEvent(params_result);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                                Log.d("search", "Fail " + e.toString());
                                Log.d("search", "Status code " + statusCode);
                            }
                        });



                    } else {

                        params_result.put("lat", currentLat);
                        params_result.put("lng", currentLon);
                        requestResultEvent(params_result);

                    }



                } else {
                    Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void requestResultEvent(RequestParams params){

        Log.d("search", "params: " + params.toString());

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching results");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(RESULTS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Search", "Success! JSON: " + response.toString());
                progressDialog.dismiss();
                String resultsJSON = response.toString();
                Intent myIntent = new Intent(getActivity(), SearchResultsActivity.class);
                myIntent.putExtra("resultsJSON", resultsJSON);
                startActivity(myIntent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("search", "Fail " + e.toString());
                Log.d("search", "Status code " + statusCode);
                progressDialog.dismiss();
            }
        });


    }



    private void addListenerOnRadioButtons() {
        radioLocationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radioCurrentLocation) {
                    Log.d("search", "current location clicked");
                    addressET.setEnabled(false);
                } else {
                    Log.d("search", "other location clicked");
                    addressET.setEnabled(true);
                }
            }
        });
    }


    private boolean validateInputs() {
        boolean isValid = true;
        String keyword = keywordET.getText().toString();
        if (keyword.trim().isEmpty()) {
            isValid = false;
            errorTextKeyword.setVisibility(View.VISIBLE);
        }
        if (radioLocationGroup.getCheckedRadioButtonId() == R.id.radioOtherLocation) {
            String address = addressET.getText().toString();
            Log.d("form validation", "input address: " + address);
            if (address.trim().isEmpty()) {
                isValid = false;
                errorTextAddress.setVisibility(View.VISIBLE);
            }
        }
        return isValid;
    }

    private void clearForm() {
        errorTextAddress.setVisibility(View.GONE);
        errorTextKeyword.setVisibility(View.GONE);
        keywordET.setText("");
        spinnerCategories.setSelection(0);
        spinnerUnits.setSelection(0);

        distanceET.setText("");
        radioLocationGroup.check(R.id.radioCurrentLocation);
        addressET.setText("");
    }


}


